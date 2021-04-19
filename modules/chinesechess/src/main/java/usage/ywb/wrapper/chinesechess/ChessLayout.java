package usage.ywb.wrapper.chinesechess;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import usage.ywb.wrapper.utils.DensityUtils;

/**
 * 象棋棋子分布布局
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/26 ]
 */
public class ChessLayout extends ViewGroup {

    private static final String TAG = ChessLayout.class.getSimpleName();

    /**
     * 棋盘边界的矩阵
     */
    private Rect mBoardRect;

    /**
     * 棋盘格子宽
     */
    private int mGridWidth;

    private int mChessWidth;

    /**
     * 棋子分布的二维数组
     */
    private Integer[][] mPiecesArray;

    private int mCurrentTargetId;

    public ChessLayout(Context context) {
        this(context, null);
    }

    public ChessLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusableInTouchMode(true);
    }

    /**
     * 设置棋局布局数组
     *
     * @param mPiecesArray 棋子布局数组
     */
    public void setPiecesArray(Integer[][] mPiecesArray) {
        this.mPiecesArray = mPiecesArray;
        removeAllViews();
        List<ChessPieces> piecesList = ChessUtil.getChessList(mPiecesArray);
        for (ChessPieces chessPieces : piecesList) {
            ChessPiecesView piecesView = new ChessPiecesView(getContext());
            piecesView.setChessPieces(chessPieces);
            piecesView.setTextSize(DensityUtils.px2dip(getContext(), 0.6f * mChessWidth));
            LayoutParams params = new LayoutParams(mChessWidth, mChessWidth);
            piecesView.setLayoutParams(params);
            addView(piecesView);
        }
    }

    /**
     * 设置棋盘边界矩阵
     *
     * @param mBoardRect 棋盘相对屏幕边界矩阵
     */
    public void setBoardRect(Rect mBoardRect) {
        this.mBoardRect = mBoardRect;
    }

    /**
     * 设置棋盘单个格子宽度
     *
     * @param mGridWidth 棋盘格子宽度
     */
    public void setGridWidth(int mGridWidth) {
        this.mGridWidth = mGridWidth;
        mChessWidth = mGridWidth * 9 / 10;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ChessPiecesView) {
                ChessPiecesView piecesView = (ChessPiecesView) childView;
                if (mBoardRect != null) {
                    int left = mBoardRect.left + mGridWidth * piecesView.getChessPieces().getX() - mChessWidth / 2;
                    int top = mBoardRect.top + mGridWidth * piecesView.getChessPieces().getY() - mChessWidth / 2;
                    piecesView.layout(left, top, left + mChessWidth, top + mChessWidth);
                }
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof ChessPiecesView) {
            final ChessPiecesView button = (ChessPiecesView) child;
            if (button.getId() == NO_ID) {
                button.setId(View.generateViewId());
            }
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出所有的childView的宽和高(都相同)
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 在使用自定义view时，用了wrap_content。那么在onMeasure中就要调用setMeasuredDimension，
        // 来指定view的宽高。如果使用的fill_parent或者一个具体的dp值。那么直接使用super.onMeasure即可
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int downX = (int) ev.getX();
            int downY = (int) ev.getY();
            int x = (downX - mBoardRect.left + mGridWidth / 2) / mGridWidth;
            int y = (downY - mBoardRect.top + mGridWidth / 2) / mGridWidth;
            if (getFocusedChild() instanceof ChessPiecesView) {
                // 当前拥有焦点的棋子为选中的移动棋子
                ChessPiecesView piecesView = (ChessPiecesView) getFocusedChild();
                if (Rule.checkMove(mPiecesArray, piecesView, x, y)) {// 如果目标位置为该棋子可以移动的位置
                    if (mPiecesArray[y][x] == 0) {// 如果目标位置没有棋子
                        // 直接移动棋子
                        move(piecesView, x, y);
                        return true;
                    } else {// 目标位置有棋子
                        if (Math.abs(mPiecesArray[y][x] - piecesView.getChessPieces().getPiecesNo()) > 7) {
                            // 如果目标位置的棋子与当前棋子分属不同的阵营则直接移动过去完成吃子过程
                            move(piecesView, x, y);
                            return true;
                        } else {
                            mCurrentTargetId = piecesView.getId();
                        }
                    }
                } else {
                    if (mCurrentTargetId == piecesView.getId()) {
                        Toast.makeText(getContext(), "目标位置移动不符合规则", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }



    /**
     * 移动
     *
     * @param view 移动的棋子
     * @param toX  移动的目标PositionX
     * @param toY  移动的目标PositionY
     */
    private void move(ChessPiecesView view, int toX, int toY) {
        ChessManager.addStep(ChessUtil.copy(mPiecesArray));
        ChessPieces chessPieces = view.getChessPieces();
        int toXDelta = mGridWidth * toX;
        int toYDelta = mGridWidth * toY;

        int left = mBoardRect.left + toXDelta - view.getWidth() / 2;
        int top = mBoardRect.top + toYDelta - view.getWidth() / 2;

        mPiecesArray[toY][toX] = chessPieces.getPiecesNo();
        mPiecesArray[chessPieces.getY()][chessPieces.getX()] = 0;
        chessPieces.setPoint(toX, toY);

        view.animate().translationX(left - view.getLeft()).translationY(top - view.getTop()).setDuration(300);
    }


}
