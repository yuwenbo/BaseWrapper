package usage.ywb.wrapper.video.camera;

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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/7/19 ]
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraLauncher {

    private static final String TAG = CameraLauncher.class.getSimpleName();

    private static final String DEFAULT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + Environment.DIRECTORY_MOVIES + File.separator + "MyVideo" + File.separator;

    private Activity mActivity;

    private CameraManager mCameraManager;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;

    private TextureView mTextureView;
    private MediaRecorder mMediaRecorder;

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
     * 摄像头ID
     */
    private String mCameraId;

    private boolean isRecording = false;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CameraLauncher(Activity activity, TextureView textureView) {
        this.mActivity = activity;
        this.mTextureView = textureView;
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        //相机预览视图创建
        TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureAvailable");
                startCameraThread();
                initCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureSizeChanged");
                surface.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.i(TAG, "surfaceTextureListener：" + "    onSurfaceTextureDestroyed");
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    private void startCameraThread() {
        if (mCameraThread == null) {
            mCameraThread = new HandlerThread(CAMERA_THREAD);
            mCameraThread.start();
        }
        if (mCameraHandler == null) {
            mCameraHandler = new Handler(mCameraThread.getLooper());
        }
    }

    private void quitCameraThread() {
        if (mCameraThread != null && mCameraThread.quitSafely()) {
            mCameraHandler = null;
            mCameraThread = null;
        }
    }

    /**
     * {@link StreamConfigurationMap#getOutputSizes} 要求传递一个 Class 类型，
     * 然后根据这个类型返回对应的尺寸列表，如果给定的类型不支持，则返回 null，
     * 可以通过 StreamConfigurationMap.isOutputSupportedFor() 方法判断某一个类型是否被支持，常见的类型有：
     * <p>
     * ImageReader：常用来拍照或接收 YUV 数据。
     * MediaRecorder：常用来录制视频。
     * MediaCodec：常用来录制视频。
     * SurfaceHolder：常用来显示预览画面。
     * SurfaceTexture：常用来显示预览画面。
     */
    private void initCamera() {
        if (TextUtils.isEmpty(mCameraId)) {
            mMediaRecorder = new MediaRecorder();
            mCameraId = getCameraId(CameraCharacteristics.LENS_FACING_BACK);
        }
        if (mCharacteristics != null && mPreviewSize == null) {
            //相机可用的配置流
            StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                mPreviewSize = getBestResolution(map.getOutputSizes(MediaRecorder.class), mTextureView.getWidth(), mTextureView.getHeight());
            }
        }
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
     * 相机打开状态的回调
     */
    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "cameraStateCallback：" + "   onOpened：");
            CameraLauncher.this.mCameraDevice = camera;
            Log.i(TAG, "相机已打开，创建预览！");
            startPreview();
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
            quitCameraThread();
            camera.close();
            mCameraDevice = null;
            if (null != mActivity) {
                mActivity.finish();
            }
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
            updatePreview();
            if (isRecording) {
                mMediaRecorder.start();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.i(TAG, "captureStateCallback：" + "  onConfigureFailed");
            mCaptureSession = null;
        }
    };

    public void startPreview() {
        isRecording = false;
        if (mCameraDevice == null || mTextureView == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }
        try {
            closePreview();
            SurfaceTexture mSurfaceTexture = mTextureView.getSurfaceTexture();
            assert mSurfaceTexture != null;
            //设置预览画面的尺寸
            mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface mSurface = new Surface(mSurfaceTexture);
            List<Surface> mSurfaces = new ArrayList<>();
            mSurfaces.add(mSurface);
            //创建一个适用于配置预览的模板
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(mSurface);
            mCameraDevice.createCaptureSession(mSurfaces, captureStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (mCaptureRequestBuilder != null && mCaptureSession != null) {
            try {
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

    private void closePreview() {
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
    }

    public void startRecord() {
        if (mCameraDevice == null || mMediaRecorder == null || mTextureView == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }
        try {
            closePreview();
            initRecorder();
            SurfaceTexture mSurfaceTexture = mTextureView.getSurfaceTexture();
            assert mSurfaceTexture != null;
            //设置预览画面的尺寸
            mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface mPreviewSurface = new Surface(mSurfaceTexture);
            Surface mRecordSurface = mMediaRecorder.getSurface();
            List<Surface> mSurfaces = new ArrayList<>();
            mSurfaces.add(mPreviewSurface);
            mSurfaces.add(mRecordSurface);
            //创建一个适用于配置预览的模板
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(mPreviewSurface);
            mCaptureRequestBuilder.addTarget(mRecordSurface);
            isRecording = true;
            mCameraDevice.createCaptureSession(mSurfaces, captureStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (isRecording) {
            isRecording = false;
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
        }
    }

    private void initRecorder() throws IOException {
        mMediaRecorder.reset();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        File file = new File(DEFAULT_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("文件路径创建失败！");
            }
        }
        mMediaRecorder.setOutputFile(DEFAULT_PATH + "video_" + Calendar.getInstance().getTimeInMillis() + ".MP4");
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        if (mPreviewSize != null) {
            mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mMediaRecorder.setVideoEncodingBitRate(8 * mPreviewSize.getWidth() * mPreviewSize.getHeight());
        } else {
            mMediaRecorder.setVideoSize(1920, 1080);
            mMediaRecorder.setVideoEncodingBitRate(8 * 1920 * 1080);
        }
        mMediaRecorder.setVideoFrameRate(36);
        mMediaRecorder.prepare();
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
     * @param lens {@link CameraCharacteristics#LENS_FACING}
     * @return 摄像头ID
     */
    private String getCameraId(int lens) {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            if (cameraIdList.length > 0) {
                for (String cameraId : cameraIdList) {
                    mCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                    //获取摄像头的方向
                    Integer lensFacing = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    if (lensFacing != null && lensFacing == lens) {
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
     * {@link ImageFormat#YUV_420_888}
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

    /**
     * 释放相机相关资源
     */
    public void release() {
        stopRecord();
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
        quitCameraThread();
    }

}
