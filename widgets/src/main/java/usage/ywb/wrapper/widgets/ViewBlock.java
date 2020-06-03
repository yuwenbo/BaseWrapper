package usage.ywb.wrapper.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/4/9 ]
 */
public abstract class ViewBlock extends View {

    private WeakReference<View> mInflatedViewRef;

    private int mLayoutResource;

    protected abstract int getLayoutResource();

    public ViewBlock(Context context, int mLayoutResource) {
        super(context);
        this.mLayoutResource = mLayoutResource;
    }

    public ViewBlock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutResource = getLayoutResource();
    }

    @Override
    public void setVisibility(int visibility) {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
                if (visibility == GONE) {
                    onPause();
                } else {
                    onStart();
                }
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
                onStart();
            }
        }
    }

    @Override
    public int getVisibility() {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                return view.getVisibility();
            }
        }
        return GONE;
    }

    protected void onStart() {
    }

    protected void onPause() {
    }

    public View inflate() {
        final ViewParent viewParent = getParent();
        if (viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final View view = inflateViewNoAdd(parent);
                replaceSelfWithView(view, parent);

                mInflatedViewRef = new WeakReference<>(view);
                onInflated(view);
                return view;
            } else {
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }

    private void replaceSelfWithView(View view, ViewGroup parent) {
        final int index = parent.indexOfChild(this);
        parent.removeViewInLayout(this);

        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams);
        } else {
            parent.addView(view, index);
        }
    }

    private View inflateViewNoAdd(ViewGroup parent) {
        final LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(mLayoutResource, parent, false);
        if (view.getId() == NO_ID && getId() != NO_ID) {
            view.setId(getId());
        }
        return view;
    }

    protected void onInflated(View view) {

    }

}
