package usage.ywb.wrapper.camera.interfaces;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/8/7 ]
 */
public abstract class CameraLauncher {

    protected OnCaptureListener onCaptureListener;

    public abstract void onPause();

    public abstract void onResume();

    public abstract void release();

    public abstract void capture();

    public abstract void preview();

    /**
     * @param onCaptureListener 拍摄回调
     */
    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        this.onCaptureListener = onCaptureListener;
    }

}
