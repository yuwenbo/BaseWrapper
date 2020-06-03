package usage.ywb.wrapper.container.excel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 右侧矩阵的主容器
 *
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class MajorRecycleView extends RecyclerView {

    public MajorRecycleView(Context context) {
        super(context);
    }

    public MajorRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MajorRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private int lastX;
    private int lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                intercept = Math.abs(x - lastX)  > Math.abs(y - lastY);
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
            default:
                intercept = super.onInterceptTouchEvent(ev);
                break;
        }
        lastX = x;
        lastY = y;
        return intercept;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

}
