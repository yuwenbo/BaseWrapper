package usage.ywb.wrapper.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class IndexesView extends View {

    public IndexesView(Context context) {
        super(context);
    }

    public IndexesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 触摸时间
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    // 26个字母+“#”
    public String[] labels = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private static final int MAX_LENGTH = 27;

    private int choose; // 选中
    private Paint paint = new Paint();
    private TextView mTextDialog;

    int singleHeight;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setLabels(@NonNull String[] labels) {
        this.labels = labels;
        postInvalidate();
    }

    public void setChoose(int choose) {
        this.choose = choose;
        postInvalidate();
    }

    private int px2Dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获得焦点改变背景颜色
        int height = getHeight(); // 获取对应高度
        int width = getWidth();   // 获取对应宽度
        int length = labels.length;
        singleHeight = height / MAX_LENGTH;  // 获取每一个字母的高度
        for (int i = 0; i < labels.length; i++) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(px2Dp(getContext(), 10));

            //选中状态״̬
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半
            float xPos = (width >> 1) - paint.measureText(labels[i]) / 2;
            float yPos;
            if (length <= MAX_LENGTH) {
                yPos = ((height - singleHeight * length) >> 1) + singleHeight * i + (singleHeight >> 1);
            } else {
                yPos = singleHeight * i + singleHeight;
            }
            canvas.drawText(labels[i], xPos, yPos, paint);
            paint.reset();  // 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY(); // 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        int length = labels.length;
        int c;
        if (length <= MAX_LENGTH) {
            c = (int) ((y - (getHeight() - singleHeight * length) / 2) / singleHeight);
        } else {
            c = (int) (y / getHeight() * length);
        }
        switch (action) {
            case MotionEvent.ACTION_UP:
//                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < labels.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(labels[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(labels[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;

    }

    public int getPositionForSection(String letters) {
        if (labels == null || labels.length == 0) {
            return -1;
        }
        char letter = letters.charAt(0);
        int length = labels.length;
        for (int i = 0; i < length; i++) {
            char firstChar = labels[i].toUpperCase().charAt(0);
            if (firstChar == letter) {
                return i;
            }
        }
        return -1;
    }


    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}
