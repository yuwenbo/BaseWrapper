package usage.ywb.wrapper.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;


/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class CircleLayout extends ViewGroup {

    /**
     * 角速度惯性衰减的加速度
     */
    private static final double SPEED_OFFSET = 0.05d;
    /**
     * 速度衰减的频率（每fps毫秒衰减一次）
     */
    private static final int FPS = 16;

    private static final int SPEED_SIZE = 5;

    /**
     * 控制惯性滑动
     */
    private Handler inertiaHandler = new Handler(Looper.getMainLooper());
    /**
     * 一个先进先出加速度列表
     * 保存手指滑动的最后{@link #SPEED_SIZE}个角速度
     */
    private LinkedList<Double> angleSpeeds = new LinkedList<>();

    private boolean isCreated = false;

    private int layoutWidth, layoutHeight;
    private int mChildWidth, mChildHeight;

    /**
     * 当前轮盘偏移角度
     */
    private double rotateAngle = 0;
    /**
     * 子控件中心点相对于布局中心点的距离（半径）
     */
    private float childRadius;
    /**
     * 内边界半径
     */
    private float insideRadius;
    /**
     * 外边界半径
     */
    private float outsideRadius;

    private Paint paint;
    private float strokeWidth;
    private int strokeColor;

    /**
     * 瞬时偏移角度（瞬时起始角度和瞬时结束角度）
     */
    private double startAngle, moveAngle;
    /**
     * 轮盘旋转的角速度
     */
    private double angleSpeed = 0;

    private GestureDetector gestureDetector;

    private OnItemClickListener onItemClickListener;

    private Runnable inertiaRunnable = new Runnable() {
        @Override
        public void run() {
            if (angleSpeed > Math.abs(SPEED_OFFSET)) {
                rotateAngle += angleSpeed;
                requestLayoutChild();
                angleSpeed -= SPEED_OFFSET;
                inertiaHandler.postDelayed(this, FPS);
            } else if (angleSpeed < -Math.abs(SPEED_OFFSET)) {
                rotateAngle += angleSpeed;
                requestLayoutChild();
                angleSpeed += SPEED_OFFSET;
                inertiaHandler.postDelayed(this, FPS);
            } else {
                inertiaHandler.removeCallbacks(this);
            }
        }
    };

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        strokeColor = Color.GRAY;
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int position = getSelectedPosition(e.getX(), e.getY());
                if (position >= 0 && position < getChildCount() && onItemClickListener != null) {
                    onItemClickListener.onItemClick(CircleLayout.this, getChildAt(position), position, 0);
                }
                return super.onSingleTapUp(e);
            }
        };
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void addAngleSpeed(double angleSpeed) {
        if (angleSpeeds.size() >= SPEED_SIZE) {
            angleSpeeds.pollLast();
        }
        angleSpeeds.addFirst(angleSpeed);
    }

    private double getAngleSpeed() {
        double sum = 0;
        for (double angleSpeed : angleSpeeds) {
            sum += angleSpeed;
        }
        return sum / angleSpeeds.size();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && !isCreated) {
            isCreated = true;
            int mWidth = r - l;
            int mHeight = b - t;
            if (mWidth < mHeight) {
                layout(l, (mHeight - layoutHeight) / 2, r, (mHeight + layoutHeight) / 2);
            } else {
                layout((mWidth - layoutWidth) / 2, t, (mWidth + layoutWidth) / 2, b);
            }
        } else {
            double hypot = Math.hypot(mChildWidth, mChildHeight);
            strokeWidth = 20;
            childRadius = (int) (layoutWidth - hypot) / 2;
            outsideRadius = (layoutWidth - strokeWidth) / 2;
            insideRadius = (float) (layoutWidth / 2 - Math.hypot(mChildWidth, mChildHeight) + strokeWidth / 2);
            requestLayoutChild();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            int tempWidth = child.getMeasuredWidth();
            int tempHeight = child.getMeasuredHeight();
            mChildWidth = tempWidth > mChildWidth ? tempWidth : mChildWidth;
            mChildHeight = tempHeight > mChildHeight ? tempHeight : mChildHeight;
        }
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        setMeasuredDimension(resolveSize(mChildWidth, widthMeasureSpec),
                resolveSize(mChildHeight, heightMeasureSpec));
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        layoutWidth = w < h ? w : h;
        layoutHeight = layoutWidth;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startAngle = getAngle(event.getX(), event.getY());
                inertiaHandler.removeCallbacks(inertiaRunnable);
                angleSpeed = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moveAngle = getAngle(event.getX(), event.getY());
                angleSpeed = moveAngle - startAngle;
                if (angleSpeed > 300) {
                    angleSpeed -= 360;
                } else if (angleSpeed < -300) {
                    angleSpeed += 360;
                }
                addAngleSpeed(angleSpeed);
                rotateAngle += angleSpeed;
                startAngle = moveAngle;
                requestLayoutChild();
                break;
            case MotionEvent.ACTION_UP:
                angleSpeed = getAngleSpeed();
                if (angleSpeeds.size() >= SPEED_SIZE && Math.abs(angleSpeed) > 5) {
                    inertiaHandler.post(inertiaRunnable);
                }
                angleSpeeds.clear();
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.setStrokeWidth(outsideRadius - insideRadius);
        paint.setColor(strokeColor);
        float cx = (float) layoutWidth / 2;
        float cy = (float) layoutHeight / 2;
        canvas.drawCircle(cx, cy, childRadius, paint);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(0xff999999);
        canvas.drawCircle(cx, cy, outsideRadius, paint);
        canvas.drawCircle(cx, cy, insideRadius, paint);
        super.dispatchDraw(canvas);
    }

    private int getSelectedPosition(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 请求布局子控件
     */
    private void requestLayoutChild() {
        float childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        float angleDelay = 360 / childCount;
        int cLeft, cTop;
        for (int i = 0; i < childCount; i++) {
            if (rotateAngle > 360) {
                rotateAngle -= 360;
            } else if (rotateAngle < 0) {
                rotateAngle += 360;
            }
            View childView = getChildAt(i);
            cLeft = Math.round((float) (childRadius * Math.cos(Math.toRadians(rotateAngle)) + (layoutWidth - mChildWidth) / 2));
            cTop = Math.round((float) (childRadius * Math.sin(Math.toRadians(rotateAngle)) + (layoutHeight - mChildHeight) / 2));
            childView.layout(cLeft, cTop, cLeft + mChildWidth, cTop + mChildHeight);
            rotateAngle += angleDelay;
        }
    }

    /**
     * 根据手指触点的位置计算偏移角度
     *
     * @param xTouch 触点相对布局的X坐标
     * @param yTouch 触点相对布局的Y坐标
     * @return 手指触电相对于布局中心点的偏移角度
     */
    private double getAngle(double xTouch, double yTouch) {
        //手指触电相对于控件中心点的位置
        double x = xTouch - (layoutWidth / 2d);
        double y = yTouch - (layoutHeight / 2d);
        double v = Math.toDegrees(Math.asin(y / Math.hypot(x, y)));
        switch (getQuadrant(x, y)) {
            case 1:
                return v;
            case 2:
            case 3:
                return 180 - v;
            case 4:
                return 360 + v;
            default:
                return 0;
        }
    }

    /**
     * 根据手指触点相对于布局中心点的坐标位置计算触点的象限
     */
    private int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    public interface OnItemClickListener {

        void onItemClick(CircleLayout circleLayout, View child, int position, long id);
    }

}
