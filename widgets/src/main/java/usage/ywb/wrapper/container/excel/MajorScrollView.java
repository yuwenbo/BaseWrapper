package usage.ywb.wrapper.container.excel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class MajorScrollView extends NestedScrollView {
    public MajorScrollView(@NonNull Context context) {
        super(context);
    }

    public MajorScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MajorScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                intercept = Math.abs(x - lastX) < Math.abs(y - lastY);
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

}
