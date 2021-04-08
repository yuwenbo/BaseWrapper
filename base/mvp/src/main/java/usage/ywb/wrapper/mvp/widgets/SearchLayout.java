package usage.ywb.wrapper.mvp.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import usage.ywb.wrapper.mvp.R;
import usage.ywb.wrapper.utils.InputMethodUtils;


/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/5/14 ]
 */
public class SearchLayout extends RelativeLayout implements View.OnClickListener, TextView.OnEditorActionListener {

    private TextView mLeftView;

    private EditText mSearchView;

    private TextView mRightView;

    private int mHeight;
    private int mSearchHeight;
    private int childPadding;

    private final int LEFT_ID;
    private final int SEARCH_ID;
    private final int RIGHT_ID;

    private OnSearchClickListener onSearchClickListener;

    public SearchLayout(Context context) {
        this(context, null);
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.search_layout);
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeight = getResources().getDimensionPixelSize(R.dimen.title_height);
        mSearchHeight = getResources().getDimensionPixelSize(R.dimen.search_height);
        childPadding = getResources().getDimensionPixelSize(R.dimen.normal_margin);
        LEFT_ID = View.generateViewId();
        SEARCH_ID = View.generateViewId();
        RIGHT_ID = View.generateViewId();
        addView(initLeftView());
        addView(initRightView());
        addView(initSearchView());
    }

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onSearchClickListener != null) {
            int id = v.getId();
            if (id == LEFT_ID) {
                onSearchClickListener.onLeftClick((TextView) v);
            } else if (id == RIGHT_ID) {
                InputMethodUtils.hide(getContext(), mSearchView);
                onSearchClickListener.onSearchClick((TextView) v);
            }
        }
    }

    public void setLeftViewText(@StringRes int id) {
        if (mLeftView == null) {
            addView(initLeftView());
        }
        mLeftView.setText(getResources().getString(id));
    }

    public void setLeftViewText(CharSequence text) {
        if (mLeftView == null) {
            addView(initLeftView());
        }
        mLeftView.setText(text);
    }

    public void setLeftViewDrawable(@DrawableRes int id) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
    }

    public void setLeftViewDrawable(Drawable drawable) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setSearchText(@StringRes int id) {
        mSearchView.setText(getResources().getString(id));
    }

    public void setSearchText(CharSequence text) {
        mSearchView.setText(text);
    }

    public void setSearchHint(@StringRes int id) {
        mSearchView.setHint(getResources().getString(id));
    }

    public void setSearchHint(CharSequence text) {
        mSearchView.setHint(text);
    }

    public void setRightViewDrawable(@DrawableRes int id) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
    }

    public void setRightViewDrawable(Drawable drawable) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }


    public void setRightViewText(@StringRes int id) {
        mRightView.setText(getResources().getString(id));
    }

    public void setRightViewText(CharSequence text) {
        mRightView.setText(text);
    }

    private View initLeftView() {
        mLeftView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mHeight);
        mLeftView.setLayoutParams(params);
        mLeftView.setId(LEFT_ID);
        mLeftView.setGravity(Gravity.CENTER);
        mLeftView.setPadding(childPadding, 0, 0, 0);
        mLeftView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mLeftView.setOnClickListener(this);
        return mLeftView;
    }

    private View initSearchView() {
        mSearchView = new EditText(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mSearchHeight);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(RIGHT_OF, LEFT_ID);
        params.addRule(LEFT_OF, RIGHT_ID);
        params.leftMargin = childPadding;
        params.rightMargin = childPadding;
        params.topMargin = (mHeight - mSearchHeight) / 2;
        mSearchView.setLayoutParams(params);
        mSearchView.setId(SEARCH_ID);
        mSearchView.setGravity(Gravity.CENTER_VERTICAL);
        mSearchView.setOnEditorActionListener(this);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        mSearchView.setPadding(childPadding, 0, childPadding, 0);
        mSearchView.setSingleLine();
        mSearchView.setTextSize(14);
        return mSearchView;
    }

    private TextView initRightView() {
        mRightView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mHeight);
        params.addRule(ALIGN_PARENT_RIGHT);
        mRightView.setLayoutParams(params);
        mRightView.setId(RIGHT_ID);
        mRightView.setGravity(Gravity.CENTER);
        mRightView.setPadding(0, 0, childPadding, 0);
        mRightView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mRightView.setOnClickListener(this);
        return mRightView;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            InputMethodUtils.hide(getContext(), v);
            if (onSearchClickListener != null) {
                onSearchClickListener.onSearchClick(v);
            }
        }
        return false;
    }

    public interface OnSearchClickListener {
        void onLeftClick(TextView view);

        void onSearchClick(TextView view);
    }

}
