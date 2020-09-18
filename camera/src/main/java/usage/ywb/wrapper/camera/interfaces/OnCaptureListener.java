package usage.ywb.wrapper.camera.interfaces;


/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/8/7 ]
 */
public interface OnCaptureListener {

    /**
     * 抓取图片成功
     *
     * @param result 字节数组
     * @param width  图片宽
     * @param height 图片高
     */
    void onCaptureResult(byte[] result, int width, int height);

}
