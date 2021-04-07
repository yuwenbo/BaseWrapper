package usage.ywb.demo.game2048;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author yuwenbo
 *         <p>
 *         卡片对象
 */
public class CardView extends FrameLayout {

    private final TextView textView;

    /**
     * @param context
     */
    public CardView(final Context context, final int size) {
        super(context);
        textView = new TextView(getContext());
        final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        params.setMargins(20, 20, 0, 0);
        textView.setTextSize(size);
        textView.setGravity(Gravity.CENTER);
        addView(textView);

    }

    /**
     * 卡片所绑定的数字
     */
    private int num = 0;

    /**
     * @return the num
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(final int num) {
        this.num = num;
        if (num == 0) {
            textView.setText("");
            textView.setBackgroundColor(Color.MAGENTA);
        } else {
            textView.setText("" + num);
            if (num <= 4) {
                textView.setBackgroundColor(Color.WHITE);
            } else if (num <= 32) {
                textView.setBackgroundColor(Color.parseColor("#ffb200"));
            } else if (num <= 128) {
                textView.setBackgroundColor(Color.GREEN);
            } else if (num <= 1024) {
                textView.setBackgroundColor(Color.YELLOW);
            } else if (num <= 8192) {
                textView.setBackgroundColor(Color.parseColor("#006dff"));
            } else if (num <= 32768) {
                textView.setBackgroundColor(Color.parseColor("#9c00ff"));
            } else {
                textView.setBackgroundColor(Color.RED);
            }
        }
    }

    public TextView getTextView() {
        return textView;
    }
}
