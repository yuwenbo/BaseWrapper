package usage.ywb.wrapper.audio.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/5/17 ]
 */
public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine(true);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
