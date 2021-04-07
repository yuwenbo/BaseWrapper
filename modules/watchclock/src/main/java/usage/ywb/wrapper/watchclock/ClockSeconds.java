package usage.ywb.wrapper.watchclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/6/30 ]
 */
public class ClockSeconds extends View {

    /**
     * 指针相对自身控件的中心点X坐标
     */
    private float centerX;
    /*
     * 指针相对自身控件的中心点Y坐标
     */
    private float centerY;

    private float second = 0;


    /**
     * 秒的指针画笔
     */
    private Paint secondPaint;

    public ClockSeconds(Context context) {
        this(context, null);
    }

    public ClockSeconds(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockSeconds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        secondPaint = new Paint();
        secondPaint.setColor(getResources().getColor(R.color.red));
        secondPaint.setAntiAlias(true);
        secondPaint.setStrokeWidth(5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        centerX = (getHeight() > getWidth() ? getWidth() : getHeight()) / 2;
        centerY = centerX;

        // 画秒针
        canvas.save();
        canvas.rotate(second * 6, centerX, centerY);
        canvas.drawLine(centerX, 0, centerX, centerY / 8 * 9, secondPaint);
        canvas.restore();

        // 画中心点
        canvas.drawCircle(centerX, centerY, 10, secondPaint);

    }

    /**
     * 设置秒（按秒更新指针）
     * @param second
     */
    public void setTime(float second) {
        this.second = second;
        postInvalidate();
    }

}
