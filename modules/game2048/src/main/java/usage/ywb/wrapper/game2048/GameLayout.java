package usage.ywb.wrapper.game2048;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwenbo
 */
public class GameLayout extends GridLayout implements OnTouchListener {

    /**
     * 有效偏移距离
     */
    private static final float OFFSET = 30;

    /**
     * 方形矩阵的边格数
     */
    private int size = 4;

    /**
     * 标志位，用于开始游戏时判断是否已经在布局中添加了卡片（0是未添加）
     */
    public int flag = 0;

    /**
     * @param size
     */
    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param flag
     */
    public void setFlag(final int flag) {
        this.flag = flag;
    }

    /**
     * @param context
     */
    public GameLayout(final Context context) {
        super(context);
        setOnTouchListener(this);
    }

    /**
     * @param context
     * @param attrs
     */
    public GameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GameLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    /**
     * 使主布局和卡片适应屏幕
     */
    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cardWidth = (Math.min(w, h) - 20) / size;
        setColumnCount(size);
        setBackgroundColor(Color.GRAY);
        getLayoutParams().width = Math.min(w, h);
        getLayoutParams().height = Math.min(w, h);
        addCard(cardWidth);
        if(gameListener != null){
            gameListener.onPre();
        }
    }

    /**
     * 卡片数组
     */
    private final CardView[][] cards = new CardView[size][size];

    public CardView[][] getCards() {
        return cards;
    }

    /**
     * 用于存储没有添加数字的空卡片的坐标点
     */
    private final List<Point> emptyPoint = new ArrayList<Point>();
    /**
     * 卡片的宽度
     */
    private int cardWidth;

    /**
     * 开始游戏
     */
    public void startGame() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cards[x][y].setNum(0);
            }
        }
        addRandomNum();
        addRandomNum();
    }

    /**
     * 添加卡片
     *
     * @param cardWidth
     */
    private void addCard(final int cardWidth) {
        removeAllViews();
        CardView card;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                card = new CardView(getContext(), cardWidth / 12);
                addView(card, cardWidth, cardWidth);
                cards[x][y] = card;
                cards[x][y].setNum(0);
            }
        }
        flag++;
    }

    /**
     * 添加随机数
     */
    private void addRandomNum() {
        emptyPoint.clear();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (cards[x][y].getNum() < 2) {
                    emptyPoint.add(new Point(x, y));
                }
            }
        }
        final Point p = emptyPoint.remove((int) (Math.random() * emptyPoint.size()));
        cards[p.x][p.y].setNum(Math.random() > 0.2 ? 2 : 4);
        startAddAnimation(cards[p.x][p.y].getTextView());
        if (isOver()) {
            gameOver();
        }
    }

    /**
     * 游戏结束
     */
    private void gameOver() {
        new AlertDialog.Builder(getContext()).setMessage("GAME OVER")
                .setPositiveButton("ok", null)
                .create().show();
        flag = 0;
    }

    /**
     * 游戏结束的判断
     * @return
     */
    private boolean isOver() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (cards[x][y].getNum() == 0) {
                    return false;
                }
                if (x > 0 && cards[x][y].getNum() == cards[x - 1][y].getNum()) {
                    return false;
                }
                if (x < size - 1 && cards[x][y].getNum() == cards[x + 1][y].getNum()) {
                    return false;
                }
                if (y > 0 && cards[x][y].getNum() == cards[x][y - 1].getNum()) {
                    return false;
                }
                if (y < size - 1 && cards[x][y].getNum() == cards[x][y + 1].getNum()) {
                    return false;
                }
            }
        }
        return true;
    }

    private ObjectAnimator slideAnimator;

    private void startAddAnimation(TextView textView){
        ObjectAnimator addAnimatorX = ObjectAnimator.ofFloat(textView, "scaleX", 0.3f, 1);
        addAnimatorX.setDuration(200);
        ObjectAnimator addAnimatorY = ObjectAnimator.ofFloat(textView, "scaleY", 0.3f, 1);
        addAnimatorY.setDuration(200);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(addAnimatorX, addAnimatorY);
        animationSet.addListener(new AnimatorListenerAdapter(){

        });
        animationSet.start();
    }



    private float startX = 0, startY = 0;
    private float offsetX = 0, offsetY = 0;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startX = event.getX();
            startY = event.getY();
            if (isOver()) {
                gameOver();
            }
            break;
        case MotionEvent.ACTION_UP:
            offsetX = event.getX() - startX;
            offsetY = event.getY() - startY;
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > OFFSET) {
                    slideRight();
                } else if (offsetX < 0 - OFFSET) {
                    slideLeft();
                }
            } else if (Math.abs(offsetX) < Math.abs(offsetY)) {
                if (offsetY > OFFSET) {
                    slideDown();
                } else if (offsetY < 0 - OFFSET) {
                    slideUp();
                }
            }
            break;
        default:
            break;
        }
        return true;
    }

    DefaultGameListener gameListener;

    public void setGameListener(DefaultGameListener gameListener) {
        this.gameListener = gameListener;
    }

    /**
     * 左滑
     */
    private void slideLeft() {
        //是否发生了数字合并或移动
        boolean isChange = false;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                //行和列确定一个块
                for (int y1 = y + 1; y1 < size; y1++) {//当前块右边的块
                    if (cards[x][y1].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            y--;
                            isChange = true;
                        } else if (cards[x][y].getNum() == cards[x][y1].getNum()) {
                            cards[x][y].setNum(cards[x][y1].getNum() * 2);
                            cards[x][y1].setNum(0);
                            isChange = true;
                            if(gameListener != null){
                                gameListener.onMerge(cards[x][y].getNum());
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (isChange) {
            addRandomNum();
        }
    }

    /**
    *
    */
    private void slideRight() {
        boolean isChange = false;
        for (int x = 0; x < size; x++) {
            for (int y = size - 1; y >= 0; y--) {
                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cards[x][y1].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            y++;
                            isChange = true;
                        } else if (cards[x][y].getNum() == cards[x][y1].getNum()) {
                            cards[x][y].setNum(cards[x][y1].getNum() * 2);
                            cards[x][y1].setNum(0);
                            isChange = true;
                            if(gameListener != null){
                                gameListener.onMerge(cards[x][y].getNum());
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (isChange) {
            addRandomNum();
        }
    }

    /**
   *
   */
    private void slideUp() {
        boolean isChange = false;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                for (int x1 = x + 1; x1 < size; x1++) {
                    if (cards[x1][y].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            x--;
                            isChange = true;
                        } else if (cards[x][y].getNum() == cards[x1][y].getNum()) {
                            cards[x][y].setNum(cards[x1][y].getNum() * 2);
                            cards[x1][y].setNum(0);
                            isChange = true;
                            if(gameListener != null){
                                gameListener.onMerge(cards[x][y].getNum());
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (isChange) {
            addRandomNum();
        }
    }


    /**
     * @param
     * @throw
     * @author  yuwenbo
     * @date  [ 2017/3/8 ]
     */
    private void slideDown() {
        boolean isChange = false;
        for (int y = 0; y < size; y++) {
            for (int x = size - 1; x >= 0; x--) {
                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cards[x1][y].getNum() > 0) {
                        if (cards[x][y].getNum() == 0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            x++;
                            isChange = true;
                        } else if (cards[x][y].getNum() == cards[x1][y].getNum()) {
                            cards[x][y].setNum(cards[x1][y].getNum() * 2);
                            cards[x1][y].setNum(0);
                            isChange = true;
                            if(gameListener != null){
                                gameListener.onMerge(cards[x][y].getNum());
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (isChange) {
            addRandomNum();
        }
    }

    public interface GameListener {
        void onPre();
        void onMerge(int increment);
    }

}
