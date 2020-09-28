package usage.ywb.wrapper.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 相机界面的取景框
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2018/08/06 ]
 */
public final class ViewfinderView extends View {

    /**
     * 取景框之外的部分
     */
    private Paint framePaint;
    /**
     * 取景框边界
     */
    private Paint boundPaint;

    /**
     * 裁剪框
     */
    private Rect cropRect;

    /**
     * 裁剪框宽与预览框宽的比
     */
    @Deprecated
    private float cropWidthRate = 1f;
    /**
     * 裁剪框高与预览框高的比
     */
    @Deprecated
    private float cropHeightRate = 1f;

    private OnDrawCompletedListener onDrawCompletedListener;

    public ViewfinderView(Context context) {
        this(context, null);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Deprecated
    public float getCropWidthRate() {
        return cropWidthRate;
    }

    @Deprecated
    public float getCropHeightRate() {
        return cropHeightRate;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = (int) ((w < h ? w : h) * 0.8);
        int height = width;
        cropWidthRate = (float) width / (float) w;
        cropHeightRate = (float) height / (float) h;
        if (cropRect == null) {
            cropRect = new Rect((w - width) / 2, (h - height) / 2, (w + width) / 2, (h + height) / 2);
        } else {
            cropRect.set((w - width) / 2, (h - height) / 2, (w + width) / 2, (h + height) / 2);
        }
        if (onDrawCompletedListener != null) {
            onDrawCompletedListener.onCompleted(w, h, new Rect(cropRect));
        }
        postInvalidate();
    }

    private void initPaint() {
        framePaint = new Paint();
        framePaint.setColor(0xff000000);
        framePaint.setAlpha(127);
        boundPaint = new Paint();
        boundPaint.setStyle(Paint.Style.STROKE);
        boundPaint.setStrokeWidth(3);
        boundPaint.setColor(Color.GREEN);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cropRect == null || cropRect.isEmpty()) {
            return;
        }
        int w = getWidth();
        int h = getHeight();
        // 画出取景框边界外的半透明暗色区域
        canvas.drawRect(0, 0, w, cropRect.top, framePaint);
        canvas.drawRect(0, cropRect.top, cropRect.left, cropRect.bottom, framePaint);
        canvas.drawRect(cropRect.right, cropRect.top, w, cropRect.bottom, framePaint);
        canvas.drawRect(0, cropRect.bottom, w, h, framePaint);
        // 画出取景框边界
        canvas.drawRect(cropRect, boundPaint);
    }

    public void setOnDrawCompletedListener(OnDrawCompletedListener onDrawCompletedListener) {
        this.onDrawCompletedListener = onDrawCompletedListener;
    }

    public interface OnDrawCompletedListener {
        /**
         * @param frameWidth
         * @param frameHeight
         * @param cropRect
         */
        void onCompleted(int frameWidth, int frameHeight, Rect cropRect);
    }

}
