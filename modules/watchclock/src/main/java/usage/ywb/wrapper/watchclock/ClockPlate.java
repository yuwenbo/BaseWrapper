package usage.ywb.wrapper.watchclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * 表盘
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/5 ]
 */
public class ClockPlate extends View {

    /**
     * 表盘半径
     */
    private float radius;

    /**
     * 刻度盘边界与刻度之间的间距
     */
    private float padding;

    /**
     * 背景颜色
     */
    private int bgColor;

    /**
     * 刻度颜色
     */
    private int dialColor;

    /**
     * 刻度尺寸
     */
    private float dialSize;
    /**
     * 数字大小
     */
    private float textSize;

    public ClockPlate(Context context) {
        this(context, null);
    }

    public ClockPlate(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockPlate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.clockPlate);
        padding = typedArray.getDimension(R.styleable.clockPlate_platePadding, 12);
        textSize = typedArray.getDimension(R.styleable.clockPlate_plateTextSize, 54);
        dialSize = typedArray.getDimension(R.styleable.clockPlate_plateDialSize, 9);
        bgColor = typedArray.getColor(R.styleable.clockPlate_plateBackground, 0x000);
        dialColor = typedArray.getColor(R.styleable.clockPlate_plateColor, 0xdddddd);
        typedArray.recycle();
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取表盘的中心点坐标
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        // 获取表盘半径
        radius = (getHeight() > getWidth() ? getWidth() : getHeight()) / 2 - padding;

        // 定义画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        // 设置表盘背景
        paint.setColor(bgColor);
        canvas.drawCircle(centerX, centerY, centerX, paint);
        // 设置表盘刻度外圈
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(dialColor);
        paint.setStrokeWidth(dialSize);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // 设置长刻度
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dialSize / 3 * 2);
        canvas.drawLine(centerX, padding, centerX, centerY / 8 + padding, paint);
        int largeAngle = 30;
        while (largeAngle < 360) {
            canvas.save();
            canvas.rotate(largeAngle, centerX, centerY);
            canvas.drawLine(centerX, padding, centerX, centerY / 8 + padding, paint);
            canvas.restore();
            largeAngle += 30;
        }

        // 设置短刻度
        paint.setStrokeWidth(dialSize / 2);
        int smallAngle = 6;
        while (smallAngle < 360) {
            canvas.save();
            canvas.rotate(smallAngle, centerX, centerY);
            canvas.drawLine(centerX, padding, centerX, centerY / 16 + padding, paint);
            canvas.restore();
            smallAngle += 6;
        }

        // 画出小时数字
        int hours = 1;
        paint.setTextSize(textSize);
        paint.setStrokeWidth(textSize / 9);
        canvas.drawText("12", centerX - textSize / 2, centerY / 8 + textSize + padding, paint);
        while (hours < 12) {
            canvas.save();
            if (hours < 10) {
                canvas.rotate(hours * 30 + 2, centerX, centerY);
            } else {
                canvas.rotate(hours * 30, centerX, centerY);
            }
            canvas.drawText(String.valueOf(hours), centerX - textSize / 2, centerY / 8 + textSize + padding, paint);
            hours++;
            canvas.restore();
        }

    }


}
