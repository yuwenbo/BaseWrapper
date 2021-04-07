package usage.ywb.demo.gamemerge10;

import android.content.Context;
import android.graphics.Point;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;



/**
 * @author yuwenbo
 * @version [ v1.0.0, 2017/3/21 ]
 */
public class BlockView extends androidx.appcompat.widget.AppCompatTextView {


    private int number;
    private Point position;

    public BlockView(Context context) {
        this(context, null);
    }

    public BlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setGravity(Gravity.CENTER);
    }

    public int getNumber() {
        return number;
    }

    public Point getPoint() {
        return position;
    }

    /**
     * @param x
     * @param y
     * @author yuwenbo
     * @date [ 2017/3/21 17:01 ]
     */
    public void setPosition(int x, int y) {
        if (position == null) {
            position = new Point(x, y);
        } else {
            position.set(x, y);
        }
    }

    /**
     * @param number
     * @author yuwenbo
     * @date [ 2017/3/21 16:58 ]
     */
    public void setNumber(int number) {
        this.number = number;
        setText(String.valueOf(number));
        int color;
        switch (number){
            case 0:color = R.color.game10_0;break;
            case 1:color = R.color.game10_1;break;
            case 2:color = R.color.game10_2;break;
            case 3:color = R.color.game10_3;break;
            case 4:color = R.color.game10_4;break;
            case 5:color = R.color.game10_5;break;
            case 6:color = R.color.game10_6;break;
            case 7:color = R.color.game10_7;break;
            case 8:color = R.color.game10_8;break;
            case 9:color = R.color.game10_9;break;
            case 10:color = R.color.game10_10;break;
            case 11:color = R.color.game10_11;break;
            case 12:color = R.color.game10_12;break;
            default:color = R.color.black;break;
        }
        setTextColor(ContextCompat.getColor(getContext(), color));
    }


}
