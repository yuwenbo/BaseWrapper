package usage.ywb.wrapper.chinesechess;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import usage.ywb.wrapper.chinesechess.ChessPieces;
import usage.ywb.wrapper.chinesechess.R;


/**
 * 棋子View
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/13 ]
 */
public class ChessPiecesView extends androidx.appcompat.widget.AppCompatTextView implements View.OnClickListener {

    /**
     * 棋子的绑定对象
     */
    private ChessPieces chessPieces;

    public ChessPieces getChessPieces() {
        return chessPieces;
    }

    public ChessPiecesView(Context context) {
        this(context, null);
    }

    public ChessPiecesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessPiecesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setTextColor(getResources().getColor(R.color.white));
        setGravity(Gravity.CENTER);
    }

    /**
     * @param chessPieces 棋子
     */
    public void setChessPieces(ChessPieces chessPieces) {
        this.chessPieces = chessPieces;
        if (chessPieces.getCamp() == ChessPieces.CAMP_RED) {
            setBackground(getResources().getDrawable(R.drawable.chess_red));
        } else {
            setBackground(getResources().getDrawable(R.drawable.chess_blue));
        }
        setText(chessPieces.getName());
    }

    @Override
    public void onClick(View v) {
        // 只有当获取到焦点后再点击才会回调
        if (isFocused()) {
            clearFocus();
        }
    }

}
