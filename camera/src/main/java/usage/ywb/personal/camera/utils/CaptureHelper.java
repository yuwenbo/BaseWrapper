package usage.ywb.personal.camera.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.util.Log;
import android.view.TextureView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import usage.ywb.personal.camera.camera1.Camera1Launcher;
import usage.ywb.personal.camera.camera2.Camera2Launcher;
import usage.ywb.personal.camera.interfaces.CameraLauncher;
import usage.ywb.personal.camera.interfaces.OnCaptureListener;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/8/7 ]
 */
public class CaptureHelper implements OnCaptureListener {

    private CameraLauncher mCameraLauncher;

    private Rect mCropRect;
    /**
     * 由于图片的真实尺寸与手机的屏幕预览尺寸并不一定是相同的，
     * 需要得到一个比例，用以计算真实的裁剪框大小和位置
     */
    private float cropWidthRate;
    private float cropHeightRate;

    private OnCaptureCropListener onCaptureCropListener;

    private static final int VERSION_BOUNDARY = Build.VERSION_CODES.LOLLIPOP;

    public CaptureHelper(Activity activity, TextureView textureView) {
        if (Build.VERSION.SDK_INT < VERSION_BOUNDARY) {
            mCameraLauncher = new Camera1Launcher(activity, textureView);
        } else {
            mCameraLauncher = new Camera2Launcher(activity, textureView);
        }
        mCameraLauncher.setOnCaptureListener(this);
    }

    public CameraLauncher getCameraLauncher() {
        return mCameraLauncher;
    }

    public boolean setFlash(boolean isTorch) {
        if (mCameraLauncher instanceof Camera2Launcher) {
            return ((Camera2Launcher) mCameraLauncher).setTorchStatus(isTorch);
        } else if (mCameraLauncher instanceof Camera1Launcher) {
            ((Camera1Launcher) mCameraLauncher).setTorchStatus(isTorch);
            return true;
        }
        return false;
    }


    public void setOnCaptureCropListener(OnCaptureCropListener onCaptureCropListener) {
        this.onCaptureCropListener = onCaptureCropListener;
    }

    @Override
    public void onCaptureResult(byte[] result, int width, int height) {
        Log.i("CaptureHelper", "currentThread：" + Thread.currentThread().getName());
        if (onCaptureCropListener != null) {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT < VERSION_BOUNDARY) {
                bitmap = parse1Bitmap(result, width, height);
            } else {
                bitmap = parse2Bitmap(result, width, height);
            }
            onCaptureCropListener.onCapture(bitmap);
        }
    }

    /**
     * @param cropRect       裁剪框
     * @param cropWidthRate  裁剪框宽与预览框宽的比
     * @param cropHeightRate 裁剪框高与预览框高的比
     */
    public void setCropRect(Rect cropRect, float cropWidthRate, float cropHeightRate) {
        this.mCropRect = cropRect;
        this.cropWidthRate = cropWidthRate;
        this.cropHeightRate = cropHeightRate;
    }

    /**
     * 使用旧的Camera返回YUV的图片
     * Camera1返回的图片数据默认为横向旋转的，处理时View呈现的宽对应图片的高，View的高对应图片的宽
     *
     * @param bytes  图片数据
     * @param width  原图片预览宽（默认横屏，对应屏幕竖直方向）
     * @param height 原图片预览高（默认横屏，对应屏幕横直方向）
     * @return 位图
     */
    private Bitmap parse1Bitmap(byte[] bytes, int width, int height) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
        Rect originalRect = new Rect(0, 0, width, height);
        if (mCropRect == null || mCropRect.isEmpty() || cropWidthRate == 0 || cropHeightRate == 0) {
            yuvImage.compressToJpeg(originalRect, 100, outputStream);
        } else {
            //View呈现的宽对应图片的高，View的高对应图片的宽
            int cropWidth = (int) (cropWidthRate * height);
            int cropHeight = (int) (cropHeightRate * width);
            int x = mCropRect.top * cropWidth / mCropRect.width();
            int y = mCropRect.left * cropHeight / mCropRect.height();
            Rect rect = new Rect(x, y, x + cropHeight, y + cropWidth);
            if (originalRect.contains(rect)) {
                yuvImage.compressToJpeg(rect, 100, outputStream);
            } else {
                yuvImage.compressToJpeg(originalRect, 100, outputStream);
            }
        }
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
        Matrix matrix = new Matrix();
        // 因为相机默认是横屏显示，而我们的手机是竖直方向的设备，所以要旋转90°
        matrix.postRotate(90f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return createBitmap;
    }

    /**
     * 把ImageReader中保存的图片转换成Bitmap，如果有裁剪框就裁剪后返回
     *
     * @param bytes  图片数据
     * @param width  相机返回的图片宽
     * @param height 相机返回的图片高
     * @return 位图
     */
    private Bitmap parse2Bitmap(byte[] bytes, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Rect originalRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if (mCropRect == null || mCropRect.isEmpty() || cropWidthRate == 0 || cropHeightRate == 0) {
            return bitmap;
        } else {
            Rect realCropRect = new Rect();
            // 不同于Camera1，部分机型在Camera2接口中返回的图片数据相对于相机方向旋转了90度
            // ImageReader无法获取图片的方向，所以根据位图的宽高比和预览尺寸的宽高比来判断
            if (height / width != originalRect.height() / originalRect.width()) {
                int cropWidth = (int) (cropWidthRate * originalRect.width());
                int cropHeight = (int) (cropHeightRate * originalRect.height());
                int x = mCropRect.left * cropWidth / mCropRect.width();
                int y = mCropRect.top * cropHeight / mCropRect.height();
                realCropRect.set(x, y, x + cropWidth, y + cropHeight);
            } else {
                int cropWidth = (int) (cropWidthRate * originalRect.height());
                int cropHeight = (int) (cropHeightRate * originalRect.width());
                int x = mCropRect.top * cropWidth / mCropRect.width();
                int y = mCropRect.left * cropHeight / mCropRect.height();
                realCropRect.set(x, y, x + cropHeight, y + cropWidth);
            }
            if (originalRect.contains(realCropRect)) {
                Bitmap result = Bitmap.createBitmap(bitmap, realCropRect.left, realCropRect.top, realCropRect.width(), realCropRect.height());
                bitmap.recycle();
                return result;
            }
        }
        return bitmap;
    }

    /**
     * 图片裁剪监听
     */
    public interface OnCaptureCropListener {
        /**
         * 完成裁剪回调
         *
         * @param bitmap 位图
         */
        void onCapture(Bitmap bitmap);
    }

}
