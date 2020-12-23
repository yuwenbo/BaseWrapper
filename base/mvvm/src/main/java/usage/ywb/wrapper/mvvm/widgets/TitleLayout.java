package usage.ywb.wrapper.mvvm.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import usage.ywb.wrapper.mvvm.R;


/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
public final class TitleLayout extends RelativeLayout implements View.OnClickListener {


    private TextView mLeftView;

    private TextView mTitleView;

    private List<TextView> mRightViewList;

    private OnTitleClickListener mOnTitleClickListener;

    private int mHeight;
    private int childPadding;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.title_layout);
    }

    public TitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeight = getResources().getDimensionPixelSize(R.dimen.title_height);
        childPadding = getResources().getDimensionPixelSize(R.dimen.normal_margin);
    }

    public void setOnTitleClickListener(OnTitleClickListener mOnTitleClickListener) {
        this.mOnTitleClickListener = mOnTitleClickListener;
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
        if (mLeftView == null) {
            addView(initLeftView());
        }
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
    }

    public void setLeftViewDrawable(Drawable drawable) {
        if (mLeftView == null) {
            addView(initLeftView());
        }
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }


    public void setTitleViewText(@StringRes int id) {
        if (mTitleView == null) {
            addView(initTitleView());
        }
        mTitleView.setText(getResources().getString(id));
    }

    public void setTitleViewText(CharSequence text) {
        if (mTitleView == null) {
            addView(initTitleView());
        }
        mTitleView.setText(text);
    }


    public void addRightViewText(@StringRes int id) {
        addRightView().setText(getResources().getString(id));
    }

    public void addRightViewText(CharSequence text) {
        addRightView().setText(text);
    }

    public void addRightViewDrawable(@DrawableRes int id) {
        addRightView().setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0);
    }

    public void addRightViewDrawable(Drawable drawable) {
        addRightView().setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    private TextView addRightView() {
        if (mRightViewList == null) {
            mRightViewList = new ArrayList<>();
        }
        TextView rightView = initRightView();
        addView(rightView);
        mRightViewList.add(rightView);
        LayoutParams params = (LayoutParams) rightView.getLayoutParams();
        if (mRightViewList.size() == 1) {
            params.addRule(ALIGN_PARENT_RIGHT);
        } else {
            TextView leftmostView = mRightViewList.get(mRightViewList.size() - 2);
            params.addRule(LEFT_OF, leftmostView.getId());
        }
        rightView.setLayoutParams(params);
        return rightView;
    }

    private View initLeftView() {
        mLeftView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mHeight);
        mLeftView.setLayoutParams(params);
        mLeftView.setId(View.generateViewId());
        mLeftView.setGravity(Gravity.CENTER);
        mLeftView.setPadding(childPadding, 0, childPadding, 0);
        mLeftView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        if (mOnTitleClickListener != null) {
            mLeftView.setOnClickListener(this);
        }
        return mLeftView;
    }

    private View initTitleView() {
        mTitleView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mHeight);
        params.addRule(CENTER_HORIZONTAL);
        mTitleView.setLayoutParams(params);
        mTitleView.setId(View.generateViewId());
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        if (mOnTitleClickListener != null) {
            mTitleView.setOnClickListener(this);
        }
        return mTitleView;
    }

    private TextView initRightView() {
        TextView rightView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, mHeight);
        rightView.setLayoutParams(params);
        rightView.setId(View.generateViewId());
        rightView.setGravity(Gravity.CENTER);
        rightView.setPadding(0, 0, childPadding, 0);
        rightView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        if (mOnTitleClickListener != null) {
            rightView.setOnClickListener(this);
        }
        return rightView;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView && mOnTitleClickListener != null) {
            TextView view = (TextView) v;
            if (v == mLeftView) {
                mOnTitleClickListener.onLeftClick(view);
            } else if (v == mTitleView) {
                mOnTitleClickListener.onTitleClick(view);
            } else {
                int index;
                if (mRightViewList != null && (index = mRightViewList.indexOf(view)) != -1) {
                    mOnTitleClickListener.onRightClick(view, index);
                }
            }
        }
    }

    public interface OnTitleClickListener {
        void onLeftClick(TextView view);

        void onTitleClick(TextView view);

        void onRightClick(TextView view, int index);
    }

}
