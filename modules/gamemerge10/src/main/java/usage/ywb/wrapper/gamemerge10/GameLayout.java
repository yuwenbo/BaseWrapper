package usage.ywb.wrapper.gamemerge10;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

import usage.ywb.wrapper.utils.DensityUtils;


/**
 * @author yuwenbo
 * @version [ v1.0.0, 2017/3/17 ]
 */
public class GameLayout extends ViewGroup implements View.OnClickListener {

    private static final String TAG = GameLayout.class.getSimpleName();

    /**
     * 矩阵规格（size * size）
     */
    private int mSize;

    /**
     * 块间距
     */
    private int mBlockPadding;

    /**
     * 边框颜色
     */
    private int mBoxColor;

    private float mTextSize;

    /**
     * 父布局-容器的宽度
     */
    private int mWidth;

    /**
     * 子控件-方块的宽度
     */
    private int cWidth;

    /**
     * 最大的数字
     */
    private int mMaxNumber = 0;

    /**
     * 是否是合成的操作
     */
    private boolean isMerge = false;

    /**
     * 用于选中的矩阵数字数组
     */
    private int[][] cacheArray;

    /**
     * 矩阵数字数组
     */
    private int[][] mainArray;

    /**
     * 发生移动的所有块的移动位置集合
     */
    private List<MoveBlock> moveList = new ArrayList<MoveBlock>();

    /**
     * 新增所有块的位置集合(暂时无用)
     */
    private List<Point> addList = new ArrayList<Point>();

    /**
     * 主布局的位置矩阵
     */
    private Rect locationRect;

    /**
     * 被选中的块
     */
    private List<BlockView> selectedBlocksList = new ArrayList<BlockView>();


    public GameLayout(Context context) {
        this(context, null);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "-------------GameLayout------------");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GameLayout);
        mBlockPadding = (int) typedArray.getDimension(R.styleable.GameLayout_padding, 0);
        mSize = typedArray.getInt(R.styleable.GameLayout_numSize, 5);
        mBoxColor = typedArray.getColor(R.styleable.GameLayout_boxColor, ContextCompat.getColor(getContext(), R.color.transparent));
        mTextSize = typedArray.getDimension(R.styleable.GameLayout_boxTextSize, -1);
        cacheArray = new int[mSize][mSize];
        mainArray = new int[mSize][mSize];
        typedArray.recycle();
    }

    /**
     * 初始化数字方块
     */
    private void initBlockView() {
        if (mTextSize <= 0 && cWidth > 0) {
            mTextSize = cWidth >> 1;
        }
        for (int rol = 0; rol < mSize; rol++) {
            for (int row = 0; row < mSize; row++) {
                int round = round(1, 3);
                if (mMaxNumber < round) {
                    mMaxNumber = round;
                }
                mainArray[rol][row] = round;
                cacheArray[rol][row] = round;
                BlockView blockView = new BlockView(getContext());
                blockView.setTextSize(DensityUtils.px2sp(getContext(), mTextSize));
                blockView.setNumber(round);
                blockView.setPosition(rol, row);
                blockView.setOnClickListener(this);
                addView(blockView);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mWidth <= 0 || locationRect == null) {
            return;
        }
        //设置的子控件的位置
        int index = -1;
        Log.i(TAG, "-------------onLayout------------:" + cWidth);
        for (int rol = 0; rol < mSize; rol++) {
            int top = locationRect.top + rol * (cWidth + mBlockPadding) + mBlockPadding;
            int bottom = top + cWidth;
            for (int row = 0; row < mSize; row++) {
                int left = locationRect.left + row * (cWidth + mBlockPadding) + mBlockPadding;
                int right = left + cWidth;
                index++;
                if (index >= getChildCount()) {
                    return;
                }
                getChildAt(index).layout(left, top, right, bottom);
            }
        }
    }

    /**
     * 合并选中的块
     *
     * @param blockView
     * @author yuwenbo
     * @date [ 2017/3/23 17:40 ]
     */
    private void mergeRelatedBlocks(BlockView blockView) {
        moveList.clear();
        cacheArray[blockView.getPoint().x][blockView.getPoint().y] = blockView.getNumber() + 1;
        mMaxNumber = Math.max(mMaxNumber, 1 + blockView.getNumber());
        for (int y = 0; y < mSize; y++) {//方块按竖直列方向下落，从左至右第一列开始
            for (int x = mSize - 1; x >= 0; x--) {//从当前列至下向上开始遍历位置，假设当前遍历到位置A
                for (int x1 = x - 1; x1 >= 0; x1--) {//至下而上遍历位于A上面的位置，假设当前为B
                    if (cacheArray[x][y] == 0) {//如果A位置没有块
                        if (cacheArray[x1][y] != 0) {//如果B位置有块
                            cacheArray[x][y] = cacheArray[x1][y];//把B上的块放到A位置
                            //Log.i(TAG, "坐标移动:[" + y + "," + x1 + "] to [" + y + "," + x + "]");
                            MoveBlock moveBlock = new MoveBlock(y, x1, y, x);
                            moveList.add(moveBlock);
                            cacheArray[x1][y] = 0;//把B的位置变为空
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        addList.clear();
        for (int x = 0; x < mSize; x++) {
            for (int y = 0; y < mSize; y++) {
                if (cacheArray[x][y] == 0) {
                    cacheArray[x][y] = round(1, Math.min(mMaxNumber, 5));
                    addList.add(new Point(x, y));
                    Log.i(TAG, "-----新生成-------------:[" + x + "," + y + "] = " + cacheArray[x][y]);
                }
            }
        }
    }

    /**
     * 找出与指定位置数字相同且相连的所有块
     *
     * @param x      指定位置的x坐标
     * @param y      指定位置的y坐标
     * @param number 指定位置的数字
     * @author yuwenbo
     * @date [ 2017/3/22 18:04 ]
     */
    private void findRelatedBlocksArray(int x, int y, int number) {
        if (number == 0) {
            return;
        }
        if (x < mSize - 1 && cacheArray[x + 1][y] == number) {
            cacheArray[x + 1][y] = 0;
            findRelatedBlocksArray(x + 1, y, number);
        }
        if (x > 0 && cacheArray[x - 1][y] == number) {
            cacheArray[x - 1][y] = 0;
            findRelatedBlocksArray(x - 1, y, number);
        }
        if (y < mSize - 1 && cacheArray[x][y + 1] == number) {
            cacheArray[x][y + 1] = 0;
            findRelatedBlocksArray(x, y + 1, number);
        }
        if (y > 0 && cacheArray[x][y - 1] == number) {
            cacheArray[x][y - 1] = 0;
            findRelatedBlocksArray(x, y - 1, number);
        }
    }

    /**
     * 将{@link #findRelatedBlocksArray}选中的块突出显示
     *
     * @author yuwenbo
     * @date [ 2017/3/23 16:42 ]
     */
    private synchronized void selectRelatedBlocks() {
        selectedBlocksList.clear();
        for (int x = 0; x < mSize; x++) {
            for (int y = 0; y < mSize; y++) {
                if (cacheArray[x][y] == 0) {
                    BlockView blockView = (BlockView) getChildAt(x * mSize + y);
                    selectedBlocksList.add(blockView);
                    blockView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_700));
                    startAnimZoomIn(blockView, 150);
                }
            }
        }
    }

    /**
     * 取消{@link #selectRelatedBlocks}选中的块的选中状态
     *
     * @author yuwenbo
     * @date [ 2017/3/23 16:51 ]
     */
    private synchronized void clearSelectedBlocks() {
        for (BlockView blockView : selectedBlocksList) {
            if (blockView.getVisibility() == View.INVISIBLE) {
                blockView.setVisibility(View.VISIBLE);
            }
            blockView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_200));
            if (!isMerge) {
                startAnimZoomOut(blockView, 150);
            }
        }
        selectedBlocksList.clear();
    }

    /**
     * 随机生成一个大于等于start，小于等于end 的整数
     *
     * @author yuwenbo
     * @date [ 2017/3/22 11:07 ]
     */
    private int round(int start, int end) {
        if (end < start) {
            return -1;
        } else {
            int random;
            if (end <= 3) {
                random = (int) (start + Math.random() * (end - start + 1));
            } else {
                if (Math.random() > 0.9) {
                    //10%的概率大于3
                    random = (int) (4 + Math.random() * (end - 3));
                } else {
                    random = (int) (1 + Math.random() * 3);
                }
            }
            return random;
        }
    }

    /**
     * 为矩阵重新赋值
     *
     * @author yuwenbo
     * @date [ 2017/3/23 18:04 ]
     */
    private void setNumbers() {
        for (int x = 0; x < mSize; x++) {
            for (int y = 0; y < mSize; y++) {
                BlockView view = (BlockView) getChildAt(x * mSize + y);
                view.setNumber(cacheArray[x][y]);
            }
        }
    }

    /**
     * 根据块在布局中的下标坐标获取其位置矩阵
     *
     * @param x x方向的下标
     * @param y y方向的下标
     * @return 相对于父布局的位置矩阵
     * @author yuwenbo
     * @date [ 2017/3/31 14:10 ]
     */
    private Rect getBlockRect(int x, int y) {
        int top = x * (cWidth + mBlockPadding) + mBlockPadding + locationRect.top;
        int bottom = top + cWidth;
        int left = y * (cWidth + mBlockPadding) + mBlockPadding + locationRect.left;
        int right = left + cWidth;
        return new Rect(left, top, right, bottom);
    }

    /**
     * 放大的动画
     *
     * @param blockView
     * @author yuwenbo
     * @date [ 2017/3/24 13:13 ]
     */
    private void startAnimZoomIn(final BlockView blockView, long duration) {
        float scale = (cWidth + (float) mBlockPadding) / cWidth;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, scale, 1f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(duration);
        blockView.startAnimation(scaleAnimation);
    }

    /**
     * 缩小的动画
     *
     * @param blockView
     * @author yuwenbo
     * @date [ 2017/3/24 13:13 ]
     */
    private void startAnimZoomOut(final BlockView blockView, long duration) {
        float scale = cWidth / (cWidth + (float) mBlockPadding / 2);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, scale, 1f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(duration);
        scaleAnimation.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                Rect rect = getBlockRect(blockView.getPoint().x, blockView.getPoint().y);
                blockView.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        });
        blockView.startAnimation(scaleAnimation);
    }

    /**
     * 开始合并消失的动画
     *
     * @param duration 动画持续时间
     * @author yuwenbo
     * @date [ 2017/3/24 13:14 ]
     */
    private void startAnimMerge(long duration) {
        for (int row = 0; row < mSize; row++) {
            for (int rol = 0; rol < mSize; rol++) {
                if (cacheArray[row][rol] == 0) {
                    final BlockView blockView = (BlockView) getChildAt(row * mSize + rol);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0, 1f, 0,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(duration);
                    scaleAnimation.setAnimationListener(new AnimListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            blockView.setVisibility(View.INVISIBLE);
                            Log.i(TAG, "-----onAnimationEnd----------------:[" + blockView.getPoint().x + "," + blockView.getPoint().y + "] = " + blockView.getNumber() + "  ——>  "
                                    + cacheArray[blockView.getPoint().x][blockView.getPoint().y]);
                            blockView.setNumber(cacheArray[blockView.getPoint().x][blockView.getPoint().y]);
                        }
                    });
                    blockView.startAnimation(scaleAnimation);
                }
            }
        }
    }

    /**
     * 移动下落的动画
     *
     * @param duration
     * @author yuwenbo
     * @date [ 2017/3/31 10:54 ]
     */
    private void startAnimMove(long duration) {
        for (MoveBlock move : moveList) {
            final BlockView blockView = (BlockView) getChildAt(move.getY() * mSize + move.getX());
            float toYDelta = (move.getToY() - move.getY()) * (mBlockPadding + cWidth);
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, toYDelta);
            animation.setDuration(duration);
            animation.setAnimationListener(new AnimListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    blockView.setNumber(cacheArray[blockView.getPoint().x][blockView.getPoint().y]);
                    blockView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                    blockView.setVisibility(View.VISIBLE);
                }
            });
            blockView.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof BlockView) {
            final BlockView blockView = (BlockView) v;
            Point point = blockView.getPoint();
            if (selectedBlocksList.isEmpty()) {
                isMerge = false;
                findRelatedBlocksArray(point.x, point.y, blockView.getNumber());
                selectRelatedBlocks();
            } else {
                if (selectedBlocksList.contains(blockView)) {
                    isMerge = true;
                    startAnimMerge(150);
                    mergeRelatedBlocks(blockView);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //setNumbers();
                            startAnimMove(150);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    resetCacheArray(cacheArray, mainArray);
                                    clearSelectedBlocks();
                                }
                            }, 160);
                        }
                    }, 160);
                } else {
                    isMerge = false;
                    clearSelectedBlocks();
                    resetCacheArray(mainArray, cacheArray);
                    findRelatedBlocksArray(point.x, point.y, blockView.getNumber());
                    selectRelatedBlocks();
                }
            }
        }
    }


    /**
     * 将数组src的值复制给pos
     */
    private void resetCacheArray(int[][] src, int[][] pos) {
        for (int x = 0; x < mSize; x++) {
            System.arraycopy(src[x], 0, pos[x], 0, mSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (locationRect != null) {
            Log.i(TAG, "-------------onDraw------------");
            Paint paint = new Paint();
            int strokeWidth = Math.min(Math.min(getPaddingLeft(), getPaddingRight()), Math.min(getPaddingTop(), getPaddingBottom()));
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mBoxColor);
            canvas.drawRect(locationRect, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            canvas.drawRect(locationRect, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG, "-------------onSizeChanged------------");
        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        mWidth = Math.min(width, height);
        cWidth = (mWidth - (mSize + 1) * mBlockPadding) / mSize;
        if (locationRect == null && mWidth > 0) {
            locationRect = new Rect((w - mWidth) / 2, (h - mWidth) / 2, (w + mWidth) / 2, (h + mWidth) / 2);
            initBlockView();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "-------------onInterceptTouchEvent------------");
        if (ev.getPointerCount() > 1) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
        //int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        //int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出所有的childView的宽和高
        //measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 在使用自定义view时，用了wrap_content。那么在onMeasure中就要调用setMeasuredDimension，来指定view的宽高。
        //setMeasuredDimension(sizeWidth, sizeHeight);
        // 如果使用的fill_parent或者一个具体的dp值。那么直接使用super.onMeasure即可
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
