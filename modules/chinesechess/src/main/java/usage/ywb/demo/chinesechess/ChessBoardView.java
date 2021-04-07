package usage.ywb.demo.chinesechess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;


/**
 * 中国象棋棋盘
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/13 ]
 */
public class ChessBoardView extends View {


    /**
     * 棋盘宽
     */
    private int boardWidth = 0;
    /**
     * 棋盘高
     */
    private int boardHeight = 0;

    /**
     * 棋盘格子宽
     */
    private int gridWidth = 0;

    /**
     * 画棋盘格子的画笔
     */
    private Paint gridPaint;

    /**
     * 棋盘边界的矩阵
     */
    private Rect boardRect;

    /**
     * 棋盘矩阵的颜色
     */
    private int color;

    private TextPaint tPaint;


    /**
     * 设置格子颜色
     */
    public void setColor(int color) {
        this.color = getResources().getColor(color);
    }

    /**
     * @return 棋盘格子宽度
     */
    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * @return 棋盘边界矩阵(相对于自身布局)
     */
    public Rect getBoardRect() {
        return boardRect;
    }


    public ChessBoardView(Context context) {
        this(context, null);
    }

    public ChessBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gridPaint = new Paint();
        tPaint = new TextPaint();
        boardRect = new Rect();
        setColor(R.color.black);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boardHeight == 0) {
            boardHeight = getWidth() > getHeight() ? getHeight() : getWidth();
            gridWidth = boardHeight / 9;
            boardWidth = gridWidth * 8;
        }
        int marginLeft = (getWidth() - boardWidth) / 2;
        int marginRight = marginLeft + boardWidth;
        int marginTop = (getHeight() - boardHeight) / 2;
        int marginBottom = marginTop + boardHeight;

        boardRect.set(marginLeft, marginTop, marginRight, marginBottom);
        gridPaint.setStrokeWidth(3);
        gridPaint.setColor(color);

        // 画横线
        for (int i = 1; i < 9; i++) {
            canvas.drawLine(marginLeft, marginTop + i * gridWidth, marginRight, marginTop + i * gridWidth, gridPaint);
        }
        // 画竖线
        for (int j = 1; j < 8; j++) {
            canvas.drawLine(marginLeft + j * gridWidth, marginTop, marginLeft + j * gridWidth, marginTop + 4 * gridWidth, gridPaint);
            canvas.drawLine(marginLeft + j * gridWidth, marginTop + 5 * gridWidth, marginLeft + j * gridWidth, marginTop + 9 * gridWidth, gridPaint);
        }

        // 画士线
        canvas.drawLine(marginLeft + 3 * gridWidth, marginTop, marginRight - 3 * gridWidth, marginTop + 2 * gridWidth, gridPaint);
        canvas.drawLine(marginLeft + 5 * gridWidth, marginTop, marginRight - 5 * gridWidth, marginTop + 2 * gridWidth, gridPaint);
        canvas.drawLine(marginLeft + 3 * gridWidth, marginBottom, marginRight - 3 * gridWidth, marginBottom - 2 * gridWidth, gridPaint);
        canvas.drawLine(marginLeft + 5 * gridWidth, marginBottom, marginRight - 5 * gridWidth, marginBottom - 2 * gridWidth, gridPaint);

        // 画带4个角的小卒子格格
        drawPoint(marginLeft, marginBottom - 3 * gridWidth, canvas, false, true);
        drawPoint(marginLeft + 2 * gridWidth, marginBottom - 3 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 4 * gridWidth, marginBottom - 3 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 6 * gridWidth, marginBottom - 3 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 8 * gridWidth, marginBottom - 3 * gridWidth, canvas, true, false);
        drawPoint(marginLeft, marginBottom - 6 * gridWidth, canvas, false, true);
        drawPoint(marginLeft + 2 * gridWidth, marginBottom - 6 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 4 * gridWidth, marginBottom - 6 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 6 * gridWidth, marginBottom - 6 * gridWidth, canvas, true, true);
        drawPoint(marginLeft + 8 * gridWidth, marginBottom - 6 * gridWidth, canvas, true, false);

        gridPaint.setStrokeWidth(6);
        // 画左边界
        canvas.drawLine(marginLeft, marginTop - 3, marginLeft, marginTop + boardHeight + 3, gridPaint);
        // 画右边界
        canvas.drawLine(marginLeft + boardWidth, marginTop - 3, marginLeft + boardWidth, marginTop + boardHeight + 3, gridPaint);
        // 画上边界
        canvas.drawLine(marginLeft, marginTop, marginRight, marginTop, gridPaint);
        // 画下边界
        canvas.drawLine(marginLeft, marginBottom, marginRight, marginBottom, gridPaint);

        // 画楚河汉界字样

        tPaint.setStrokeWidth(gridWidth * 0.2f);
        tPaint.setTextSize(gridWidth * 0.6f);
        tPaint.setColor(color);
        tPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tPaint.setTextLocale(Locale.TAIWAN);
        canvas.save();
        canvas.rotate(90, marginLeft / 2 + marginRight / 2, marginTop / 2 + marginBottom / 2);
        canvas.drawText("汉", boardWidth / 2f + gridWidth * 0.2f, marginTop + 1.8f * gridWidth, tPaint);
        canvas.drawText("界", boardWidth / 2f + gridWidth * 0.2f, marginTop + 2.8f * gridWidth, tPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-90, marginLeft / 2 + marginRight / 2, marginTop / 2 + marginBottom / 2);
        canvas.drawText("楚", boardWidth / 2f + gridWidth * 0.2f, marginTop + 1.8f * gridWidth, tPaint);
        canvas.drawText("河", boardWidth / 2f + gridWidth * 0.2f, marginTop + 2.8f * gridWidth, tPaint);
        canvas.restore();

        if (onDrawFinished != null) {
            onDrawFinished.onDrawFinished();
        }
    }

    private OnDrawFinishedListener onDrawFinished;

    /**
     * 设置棋盘绘制监听
     */
    public void setOnDrawFinished(OnDrawFinishedListener onDrawFinished) {
        this.onDrawFinished = onDrawFinished;
    }

    /**
     * 监听棋盘网格绘制完毕的接口
     */
    public interface OnDrawFinishedListener {

        void onDrawFinished();
    }

    /**
     * 画出棋盘格子中炮和兵的标靶点
     *
     * @param x      棋盘坐标X
     * @param y      棋盘坐标Y
     * @param canvas 画布
     * @param left   左边是否需要画
     * @param right  右边是否需要画
     */
    private void drawPoint(int x, int y, Canvas canvas, boolean left, boolean right) {
        if (left) {
            // 左上 —
            canvas.drawLine(x - gridWidth / 10, y - gridWidth / 10, x - 2 * gridWidth / 10, y - gridWidth / 10, gridPaint);
            // 左上 |
            canvas.drawLine(x - gridWidth / 10, y - gridWidth / 10, x - gridWidth / 10, y - 2 * gridWidth / 10, gridPaint);
            // 左下 —
            canvas.drawLine(x - gridWidth / 10, y + gridWidth / 10, x - 2 * gridWidth / 10, y + gridWidth / 10, gridPaint);
            // 左下 |
            canvas.drawLine(x - gridWidth / 10, y + gridWidth / 10, x - gridWidth / 10, y + 2 * gridWidth / 10, gridPaint);
        }

        if (right) {
            // 右上 —
            canvas.drawLine(x + gridWidth / 10, y - gridWidth / 10, x + 2 * gridWidth / 10, y - gridWidth / 10, gridPaint);
            // 右上 |
            canvas.drawLine(x + gridWidth / 10, y - gridWidth / 10, x + gridWidth / 10, y - 2 * gridWidth / 10, gridPaint);
            // 右下 —
            canvas.drawLine(x + gridWidth / 10, y + gridWidth / 10, x + 2 * gridWidth / 10, y + gridWidth / 10, gridPaint);
            // 右下 |
            canvas.drawLine(x + gridWidth / 10, y + gridWidth / 10, x + gridWidth / 10, y + 2 * gridWidth / 10, gridPaint);
        }
    }


}
