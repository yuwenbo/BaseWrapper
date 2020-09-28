package usage.ywb.wrapper.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class SignatureView extends View {

    private static final float STROKE_WIDTH = 10f;

    /**
     * Need to track this so the dirty region can accommodate the stroke.
     **/
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

    private Paint paint = new Paint();
    private Path path = new Path();

    /**
     * Optimizes painting by invalidating the smallest possible area.
     */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    private RectF drawMaxRect;

    private OnSignatureListener listener;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public void setOnSignatureListener(OnSignatureListener listener) {
        this.listener = listener;
    }

    /**
     * Erases the signature.
     */
    public void clear() {
        path.reset();
        drawMaxRect = null;
        // Repaints the entire view.
        invalidate();
    }

    public Bitmap getDrawDistrictBitmap() {
        if (drawMaxRect == null) {
            return null;
        }
        int reserveSize = (int) STROKE_WIDTH;
        int width = (int) drawMaxRect.width() + reserveSize + reserveSize / 2;
        int height = (int) drawMaxRect.height() + reserveSize + reserveSize / 2;
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Path newPath = new Path();
        path.offset(-drawMaxRect.left + reserveSize / 2, -drawMaxRect.top + reserveSize / 2, newPath);
        //canvas.drawBitmap();
        //paint.setStrokeWidth(STROKE_WIDTH / 2);
        canvas.drawARGB(255, 255, 255, 255);
        canvas.drawPath(newPath, paint);
        //paint.setStrokeWidth(STROKE_WIDTH);
        return output;
    }

    public Bitmap getDrawDistrictBitmap(int width, int height) {
        if (drawMaxRect == null) {
            return null;
        }
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        if (bitmap == null) {
            return null;
        }
        bitmap = getZoomBitmap(bitmap, width, height);
        return bitmap;
    }

    private Bitmap getZoomBitmap(Bitmap orgBitmap, double newWidth, double newHeight) {
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        // 获取图片的宽和高
        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                updateDrawMaxRect(eventX, eventY);
                if (listener != null) {
                    listener.onSignatureStart();
                }
                // There is no end point yet, so don't waste cycles invalidating.
                return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // Start tracking the dirty region.
                resetDirtyRect(eventX, eventY);

                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                    updateDrawMaxRect(historicalX, historicalY);
                }

                // After replaying history, connect the line to the touch point.
                path.lineTo(eventX, eventY);
                break;

        }

        // Include half the stroke width to avoid clipping.
        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void updateDrawMaxRect(float x, float y) {
        if (drawMaxRect == null) {
            drawMaxRect = new RectF(x, y, x, y);
            return;
        }

        if (x < drawMaxRect.left) {
            drawMaxRect.left = x;
        } else if (x > drawMaxRect.right) {
            drawMaxRect.right = x;
        }

        if (y < drawMaxRect.top) {
            drawMaxRect.top = y;
        } else if (y > drawMaxRect.bottom) {
            drawMaxRect.bottom = y;
        }
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * points.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {

        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    public interface OnSignatureListener {
        void onSignatureStart();
    }
}