package usage.ywb.wrapper.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.RelativeLayout;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class ZoomRelativeLayout extends RelativeLayout implements OnScaleGestureListener {

    /**
     * 自定义的处理缩放的接口对象
     */
    private OnZoomListener onZoomListener;
    /**
     * 处理变焦手势的控制器
     */
    private final ScaleGestureDetector detector;
    /**
     * 缩放的有效间隔
     */
    private final static int VISIBLE_SPAN = 30;


    /**
     * 设置监听
     *
     * @param onZoomListener
     */
    public void setOnZoomListener(final OnZoomListener onZoomListener) {
        this.onZoomListener = onZoomListener;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ZoomRelativeLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        detector = new ScaleGestureDetector(context, this);
    }

    /**
     * @param context
     * @param attrs
     */
    public ZoomRelativeLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     */
    public ZoomRelativeLayout(final Context context) {
        this(context, null);
    }

    /**
     * 此接口定义两个抽象方法，当完成缩放手势时回调，需要在根View中实现，
     *
     * @author yuwenbo
     */
    public interface OnZoomListener {
        //展开全部列表
        void expansion();

        //收起全部列表
        void collapse();
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        if (ev.getPointerCount() == 2) {
            //当系统检测到有两个触点的时候拦截触摸事件，会回调当View中onTouchEvent；
            return true;
        }
        //当系统检测到不是两个触点的时候不进行拦截，会继续向子View传递
        return false;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getPointerCount() == 2) {
            //当系统检测到有两个触点的时候，将事件交给ScaleGestureDetector处处理
            return detector.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onScale(final ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(final ScaleGestureDetector detector) {
        //ScaleGestureDetector接收到事件会首先回调此方法，返回值如果是false将不会执行onScale
        return true;
    }

    @Override
    public void onScaleEnd(final ScaleGestureDetector detector) {
        //getPreviousSpan返回的是手指第一次触摸时的两点间距
        //getCurrentSpan返回的是当前实时的两点间距
        final float value = detector.getCurrentSpan() - detector.getPreviousSpan();
        if (onZoomListener != null) {
            if (value > VISIBLE_SPAN) {//设置当缩放手势的间距差值超过30个像素则算一次有效缩放
                onZoomListener.expansion();
            }
            if (value < -VISIBLE_SPAN) {
                onZoomListener.collapse();
            }
        }
    }

}
