package usage.ywb.wrapper.watchclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 *
 * 指针(时，分，秒)
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/6 ]
 */
public class ClockPoints extends View {

    /**
     * 小时的指针画笔
     */
    private Paint hoursPaint;
    /**
     * 分钟的指针画笔
     */
    private Paint minutePaint;
    /**
     * 秒的指针画笔
     */
    private Paint secondPaint;

    private int hours = 0;
    private int minute = 0;
    private int second = 0;

    /**
     * 指针相对自身控件的中心点X坐标
     */
    private float centerX;
    /*
     * 指针相对自身控件的中心点Y坐标
     */
    private float centerY;

    public ClockPoints(Context context) {
        this(context, null);
    }

    public ClockPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockPoints(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.clockPoint);
        int hColor = typedArray.getColor(R.styleable.clockPoint_hoursColor, 0x66CC33);
        int mColor = typedArray.getColor(R.styleable.clockPoint_minuteColor, 0x99CCFF);
        int sColor = typedArray.getColor(R.styleable.clockPoint_secondColor, 0x00000000);
        typedArray.recycle();
        hoursPaint = new Paint();
        hoursPaint.setColor(hColor);
        hoursPaint.setAntiAlias(true);
        hoursPaint.setStrokeWidth(10);
        minutePaint = new Paint();
        minutePaint.setColor(mColor);
        minutePaint.setAntiAlias(true);
        minutePaint.setStrokeWidth(8);
        secondPaint = new Paint();
        secondPaint.setColor(sColor);
        secondPaint.setAntiAlias(true);
        secondPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = (getHeight() > getWidth() ? getWidth() : getHeight()) / 2;
        centerY = centerX;

        // 画时针
        canvas.save();
        canvas.rotate(hours * 30 + minute / 2, centerX, centerY);
        canvas.drawLine(centerX, centerY / 2, centerX, centerY / 8 * 9, hoursPaint);
        canvas.restore();

        // 画分针
        canvas.save();
        canvas.rotate(minute * 6 + second / 10, centerX, centerY);
        canvas.drawLine(centerX, centerY / 4, centerX, centerY / 8 * 9, minutePaint);
        canvas.restore();

        // 画秒针
        canvas.save();
        canvas.rotate(second * 6, centerX, centerY);
        canvas.drawLine(centerX, 0, centerX, centerY / 8 * 9, secondPaint);
        canvas.restore();

        // 画中心点
        canvas.drawCircle(centerX, centerY, 10, secondPaint);

    }

    /**
     * 设置时分秒（按时分秒更新指针）
     * @param hours
     * @param minute
     * @param second
     */
    public void setTime(int hours, int minute, int second) {
        this.hours = hours;
        this.minute = minute;
        this.second = second;
        postInvalidate();
    }

}
