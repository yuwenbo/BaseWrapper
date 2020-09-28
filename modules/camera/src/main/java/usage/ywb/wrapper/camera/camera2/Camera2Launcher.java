package usage.ywb.wrapper.camera.camera2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import usage.ywb.wrapper.camera.interfaces.CameraLauncher;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/7/19 ]
 */
@SuppressWarnings("FieldCanBeLocal")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Launcher extends CameraLauncher {

    private static final String TAG = Camera2Launcher.class.getSimpleName();

    private Activity mActivity;

    private CameraManager mCameraManager;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;

    private TextureView mTextureView;
    private ImageReader mImageReader;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CameraCharacteristics mCharacteristics;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private static final String CAMERA_THREAD = "CameraBackground";

    /**
     * 最佳预览尺寸
     */
    private Size mPreviewSize;
    /**
     * 后置摄像头ID
     */
    private String mCameraId;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public Camera2Launcher(Activity activity, TextureView textureView) {
        this.mActivity = activity;
        this.mTextureView = textureView;
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    /**
     * 验证相机的有效性
     */
    private CameraManager.AvailabilityCallback availabilityCallback = new CameraManager.AvailabilityCallback() {
        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            super.onCameraAvailable(cameraId);
            Log.i(TAG, "onCameraAvailable：" + cameraId + "  Thread：" + Thread.currentThread().getName());
        }

        @Override
        public void onCameraUnavailable(@NonNull String cameraId) {
            super.onCameraUnavailable(cameraId);
            Log.i(TAG, "onCameraUnavailable：" + cameraId + "  Thread：" + Thread.currentThread().getName());
        }
    };

    /**
     * 相机预览视图创建
     */
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureAvailable");
            startCameraThread();
            Camera2Launcher.this.mSurfaceTexture = surface;
            Camera2Launcher.this.mSurface = new Surface(surface);
            initCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureSizeChanged");
            surface.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureDestroyed");
            surface.release();
            mSurfaceTexture = null;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureUpdated");
        }
    };

    /**
     * 相机打开状态的回调
     */
    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "cameraStateCallback：" + "   onOpened：");
            try {
                if (mTextureView != null && mTextureView.isAvailable()) {
                    Camera2Launcher.this.mCameraDevice = camera;
                    Log.i(TAG, "相机已打开，创建获取会话！");
                    camera.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()), captureStateCallback, mCameraHandler);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "cameraStateCallback：" + "   onDisconnected：");
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.i(TAG, "cameraStateCallback：" + "   onError：" + error);
            //当camera出错时会回调该方法
            //应在该方法调用CameraDevice.close()和做一些其他释放相机资源的操作,防止相机出错而导致一系列问题。
            camera.close();
            mCameraDevice = null;
        }

    };

    /**
     * 相机捕获图像状态回调
     */
    private CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.i(TAG, "captureStateCallback：" + "  onConfigured");
            mCaptureSession = session;
            preview();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.i(TAG, "captureStateCallback：" + "  onConfigureFailed");
            mCaptureSession = null;
        }
    };

    /**
     * 图像预览回调
     */
    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            Log.i(TAG, "captureCallback：" + "   onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.i(TAG, "captureCallback：" + "   onCaptureCompleted");
        }
    };

    /**
     * 图片文件有效读取监听
     */
    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.i(TAG, "imageAvailableListener：" + "  onImageAvailable");
            if (onCaptureListener != null) {
                Image image = reader.acquireNextImage();
                int width = image.getWidth();
                int height = image.getHeight();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                onCaptureListener.onCaptureResult(bytes, width, height);
                image.close();
                reader.close();
            }
        }
    };

    /**
     * 开启相机线程
     */
    private void startCameraThread() {
        if (mCameraThread == null) {
            mCameraThread = new HandlerThread(CAMERA_THREAD);
            mCameraThread.start();
        }
        if (mCameraHandler == null) {
            mCameraHandler = new Handler(mCameraThread.getLooper());
        }
    }

    /**
     * 关闭相机线程
     */
    private void quitCameraThread() {
        if (mCameraThread != null && mCameraThread.quitSafely()) {
            mCameraHandler = null;
            mCameraThread = null;
        }
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (TextUtils.isEmpty(mCameraId)) {
            mCameraId = getBackCameraId();
        }
        if (mCharacteristics != null && mPreviewSize == null) {
            //相机可用的配置流
            StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                /**
                 * {@link StreamConfigurationMap#getOutputSizes(Class)} 要求传递一个 Class 类型，
                 * 然后根据这个类型返回对应的尺寸列表，如果给定的类型不支持，则返回 null，
                 * 可以通过 StreamConfigurationMap.isOutputSupportedFor() 方法判断某一个类型是否被支持，常见的类型有：
                 *
                 * ImageReader：常用来拍照或接收 YUV 数据。
                 * MediaRecorder：常用来录制视频。
                 * MediaCodec：常用来录制视频。
                 * SurfaceHolder：常用来显示预览画面。
                 * SurfaceTexture：常用来显示预览画面。
                 */
                mPreviewSize = getBestResolution(map.getOutputSizes(SurfaceTexture.class), mTextureView.getWidth(), mTextureView.getHeight());
            }
        }
        if (mPreviewSize != null) {
            //设置预览画面的尺寸
            mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 1);
        } else {
            mImageReader = ImageReader.newInstance(mTextureView.getWidth(), mTextureView.getHeight(), ImageFormat.JPEG, 1);
        }
        mImageReader.setOnImageAvailableListener(imageAvailableListener, mCameraHandler);
        openCamera();
    }

    /**
     * 闪光灯开关
     */
    public boolean setTorchStatus(boolean needTorch) {
        if (mCharacteristics != null) {
            Boolean torch = mCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (torch != null && torch && mCaptureSession != null && mCaptureRequestBuilder != null) {//闪光灯是否可用
                if (needTorch) {
                    mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);//开启闪光灯
                } else {
                    mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);//关闭闪光灯
                }
                try {
                    mCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mCameraHandler);
                    return true;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取后置摄像头ID
     *
     * @return
     */
    private String getBackCameraId() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            if (cameraIdList.length > 0) {
                mCameraManager.registerAvailabilityCallback(availabilityCallback, mCameraHandler);
                for (String cameraId : cameraIdList) {
                    mCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                    //获取摄像头的方向
                    Integer lensFacing = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        // 如果是后置摄像头
                        return cameraId;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        if (TextUtils.isEmpty(mCameraId)) {
            Toast.makeText(mActivity, "没有可用相机设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mActivity, "应用没有使用相机的权限，请去设置中配置权限！", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i(TAG, "打开相机");
            mCameraManager.openCamera(mCameraId, cameraStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览
     */
    @Override
    public void preview() {
        if (mCameraDevice != null && mCaptureSession != null) {
            try {
                //创建一个适用于配置预览的模板
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mCaptureRequestBuilder.addTarget(mSurface);
                //CaptureRequest.Builder#set()方法设置预览界面的特征,例如，闪光灯，zoom调焦等
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);//自动对焦
                // 设置自动曝光模式
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                // 获取设备方向
                int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
                // 根据设备方向计算设置照片的方向
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                mCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拍照
     */
    @Override
    public void capture() {
        if (mCameraDevice != null && mCaptureSession != null) {
            try {
//                mCaptureSession.stopRepeating();
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                mCaptureRequestBuilder.addTarget(mSurface);
                mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
                int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
                // 根据设备方向计算设置照片的方向
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                mCaptureSession.capture(mCaptureRequestBuilder.build(), captureCallback, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 恢复
     */
    @Override
    public void onResume() {
        if (mSurfaceTexture != null && mSurface != null && mSurface.isValid()) {
            initCamera();
        }
    }

    /**
     * 暂停
     */
    @Override
    public void onPause() {
        if (mCaptureSession != null) {
            try {
                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mCaptureSession = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    /**
     * 释放相机相关资源
     */
    @Override
    public void release() {
        if (mCameraManager != null) {
            //该回调会一直占用资源而不能被释放,且会继续而且是独立于activity生命周期和单独的CameraManager实例地无终止地接收相机事件!
            mCameraManager.unregisterAvailabilityCallback(availabilityCallback);
        }
        this.onPause();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null && mSurface.isValid()) {
            mSurface.release();
            mSurface = null;
        }
        quitCameraThread();
    }

    /**
     * 获取与预览框最相近的最佳分辨率
     *
     * @param surfaceWidth  相机容器的宽度
     * @param surfaceHeight 相机容器的高度
     */
    private Size getBestResolution(Size[] sizes, int surfaceWidth, int surfaceHeight) {
        int bestWidth = 0, bestHeight = 0;
        Size bestSize;
        int minOffset = Integer.MAX_VALUE;
        float minRated = Float.MAX_VALUE;
        Log.i(TAG, "mSurface size----width = " + surfaceWidth + " ,height = " + surfaceHeight);
        for (Size size : sizes) {
            // 相机所支持的尺寸
            Log.i(TAG, "support sizes:----width = " + size.getWidth() + " ,height = " + size.getHeight());
            // 获取相机真实尺寸和容器组件的差值（因为相机是默认横屏，所以相机宽对应组件的高）
            int offset = Math.abs(size.getWidth() - surfaceHeight);
            // 相机所支持尺寸与当前相机容器的比例差
            float rated = Math.abs((float) size.getWidth() / (float) size.getHeight() - (float) surfaceHeight / (float) surfaceWidth);
            // 当相机支持尺寸与当前容器比例差小于0.03且相机尺寸小于容器尺寸的时候取相机尺寸和容器组件尺寸差值最小的作为最佳分辨率
            if (offset < minOffset && rated < 0.03 && rated < minRated) {
                bestWidth = size.getWidth();
                bestHeight = size.getHeight();
                minOffset = offset;
                minRated = rated;
            }
        }
        Log.i(TAG, "best size----width = " + bestWidth + " ,height = " + bestHeight);
        if (minOffset == Integer.MAX_VALUE) {// 若果没有找到一个满足最佳分辨率条件的尺寸
            if (sizes.length == 0) {
                bestSize = new Size((surfaceHeight >> 3) << 3, (surfaceWidth >> 3) << 3);
            } else {
                bestSize = new Size(sizes[0].getWidth(), sizes[0].getHeight());
            }
        } else {
            bestSize = new Size(bestWidth, bestHeight);
        }
        return bestSize;
    }

    /**
     * {@link ImageFormat#YUV_420_888}
     *
     * @param reader
     * @return
     */
    private byte[] compressToJpeg(ImageReader reader) {
        Image image = reader.acquireNextImage();
        if (image.getFormat() != ImageFormat.YUV_420_888) return null;
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21Bytes = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21Bytes, 0, ySize);
        vBuffer.get(nv21Bytes, ySize, vSize);
        uBuffer.get(nv21Bytes, ySize + vSize, uSize);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(nv21Bytes, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, outputStream);
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.close();
        return result;
    }

}
