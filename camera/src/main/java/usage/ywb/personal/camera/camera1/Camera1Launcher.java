package usage.ywb.personal.camera.camera1;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

import usage.ywb.personal.camera.interfaces.CameraLauncher;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/8/7 ]
 */
public class Camera1Launcher extends CameraLauncher implements SensorController.OnFocusableListener {

    private static final String TAG = Camera1Launcher.class.getSimpleName();

    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;

    private Camera mCamera;
    private Camera.Parameters mParameters;
    private CameraAutoFocus mCameraAutoFocus;
    private PreviewCallback mPreviewCallback;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    private SensorController mSensorController;

    private boolean isFocusing = false;

    private static final String CAMERA_THREAD = "CameraBackground";

    public Camera1Launcher(Activity activity, TextureView textureView) {
        this.mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mSensorController = new SensorController(activity);
        mSensorController.setOnFocusableListener(this);
    }

    /**
     * 相机预览视图创建
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, final int height) {
            mSurfaceTexture = surface;
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.i(TAG, "mSurfaceTextureListener：" + "    onSurfaceTextureDestroyed");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    /**
     * 为相机设置参数
     */
    private void openCamera() {
        try {
            mCamera = Camera.open();
            mCameraAutoFocus = new CameraAutoFocus();
            mPreviewCallback = new PreviewCallback();
            mParameters = mCamera.getParameters();
            if (mPreviewSize == null) {
                mPreviewSize = getBestPreviewResolution(mTextureView.getWidth(), mTextureView.getHeight());
                Log.i(TAG, "best preview sizes:----width = " + mPreviewSize.width + " ,height = " + mPreviewSize.height);
            }
            mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);// 获得摄像预览的大小
            if (mPictureSize == null) {
                mPictureSize = getBestPictureResolution(mPreviewSize);
                Log.i(TAG, "best picture sizes:----width = " + mPictureSize.width + " ,height = " + mPictureSize.height);
            }
            mParameters.setPictureSize(mPictureSize.width, mPictureSize.height);// 设置拍出来的屏幕大小
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);// 自动对焦模式
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(mParameters);
            mCamera.setPreviewTexture(mSurfaceTexture);
            Log.i(TAG, "相机准备就绪，开启预览...");
            preview();
        } catch (IOException e) {
            Log.e(TAG, "相机打开失败");
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        if (mSurfaceTexture != null && mTextureView != null && mTextureView.isAvailable()) {
            openCamera();
        }
        mSensorController.onStart();
    }

    /**
     * 闪光灯
     */
    public void setTorchStatus(boolean needTorch) {
        if (mParameters != null) {
            if (needTorch) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启闪光灯
            } else {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 开启闪光灯
            }
            mCamera.setParameters(mParameters);
        }
    }

    @Override
    public void onPause() {
        mSensorController.onStop();
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.unlock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 释放相机资源
     */
    @Override
    public void release() {
        Log.i(TAG, "----release----");
        this.onPause();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    @Override
    public void preview() {
        if (mCamera != null && mCameraAutoFocus != null) {
            mCamera.stopPreview();
            mCamera.startPreview();
            requestFocus();
        }
    }

    @Override
    public void capture() {
        if (mCamera != null && mPreviewCallback != null) {
            mCamera.setOneShotPreviewCallback(mPreviewCallback);
        }
    }

    @Override
    public void onFocusable() {
        if (mCamera != null && mCameraAutoFocus != null && !isFocusing) {
            requestFocus();
        }
    }

    /**
     * 请求对焦
     */
    private void requestFocus() {
        if (mSurfaceTexture == null) {
            return;
        }
        isFocusing = true;
        mCamera.cancelAutoFocus();
        mCamera.autoFocus(mCameraAutoFocus);
    }

    /**
     * 自动对焦回调
     */
    private class CameraAutoFocus implements Camera.AutoFocusCallback {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i(TAG, "----onAutoFocus----");
            isFocusing = false;
            if (!success) {
                Log.i(TAG, "对焦失败，请求继续对焦");
                // 对焦失败，发送请求继续对焦
                requestFocus();
            }
        }
    }

    /**
     * 获取预览帧画面内容
     */
    private class PreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.i(TAG, "获取相机成功对焦后生成的预览图...");
            if (onCaptureListener != null) {
                onCaptureListener.onCaptureResult(data, mPictureSize.width, mPictureSize.height);
            }
        }
    }

    /**
     * 获取与屏幕预览框尺寸宽高比最接近的最高分辨率
     *
     * @param surfaceWidth  相机容器的宽度
     * @param surfaceHeight 相机容器的高度
     * @return 预览分辨率
     */
    private Camera.Size getBestPreviewResolution(int surfaceWidth, int surfaceHeight) {
        List<Camera.Size> previewSizes = mParameters.getSupportedPreviewSizes();
        int minOffset = Integer.MIN_VALUE;
        float minRated = Float.MAX_VALUE;
        Camera.Size bestSize = null;
        for (Camera.Size size : previewSizes) {
            Log.i(TAG, "support preview sizes:----width = " + size.width + " ,height = " + size.height);
            // 因为相机是默认横屏，所以相机宽对应组件的高（获取相机真实尺寸和容器组件的差值）
            int offset = size.width - surfaceHeight;
            // 相机所支持尺寸与当前相机容器的比例差
            float rated = Math.abs((float) size.width / (float) size.height - (float) surfaceHeight / (float) surfaceWidth);
            // 当相机支持尺寸与当前容器比例差小于0.03且相机尺寸小于容器尺寸的时候取相机尺寸和容器组件尺寸差值最小的作为最佳分表率
            if (offset > minOffset && rated < 0.03 && rated < minRated) {
                minOffset = offset;
                minRated = rated;
                bestSize = size;
            }
        }
        if (bestSize != null) {
            return bestSize;
        } else {
            return previewSizes.get(0);
        }
    }


    /**
     * 获取与最佳预览分辨率最接近的图片尺寸
     *
     * @param previewSize 预览尺寸
     * @return 图片尺寸
     */
    private Camera.Size getBestPictureResolution(Camera.Size previewSize) {
        List<Camera.Size> pictureSizes = mParameters.getSupportedPictureSizes();
        Camera.Size bestSize = null;
        float rated = (float) previewSize.width / (float) previewSize.height;
        int minOffset = Integer.MAX_VALUE;
        for (Camera.Size size : pictureSizes) {
            Log.i(TAG, "support picture sizes:----width = " + size.width + " ,height = " + size.height);
            int offset = Math.abs(previewSize.width - size.width);
            if (offset < minOffset && (float) size.width / (float) size.height == rated) {
                bestSize = size;
                minOffset = offset;
            }
        }
        if (bestSize != null) {
            return bestSize;
        } else {
            return pictureSizes.get(0);
        }
    }


}
