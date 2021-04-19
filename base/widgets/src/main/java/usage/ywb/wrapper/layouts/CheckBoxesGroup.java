package usage.ywb.wrapper.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选可取消选中
 *
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class CheckBoxesGroup extends LinearLayout {

    public CheckBoxesGroup(Context context) {
        this(context, null);
    }

    public CheckBoxesGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxesGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        init();
    }


    // holds the checked id; the selection is empty by default
    private List<Integer> mCheckedIds = new ArrayList<>();
    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private CheckBoxesGroup.OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        int index = 0, len = mCheckedIds.size();
        for (; index < len; index++) {
            int id = mCheckedIds.get(index);
            setCheckedId(id);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof CheckBox) {
            final CheckBox button = (CheckBox) child;
            if (button.isChecked()) {
                setCheckedId(button.getId());
            }
        }
        super.addView(child, index, params);
    }

    public void setOnCheckedChangeListener(CheckBoxesGroup.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedRadioButtonIds()
     * @see #clearCheck()
     */
    public void check(@IdRes int id) {
        if (mCheckedIds != null) {
            if (id != -1 && (mCheckedIds.contains(id))) {
                return;
            }
        }
        if (id != -1) {
            setCheckedStateForView(id, true);
        }
        setCheckedId(id);
    }

    private void setCheckedId(@IdRes int id) {
        if (!mCheckedIds.contains(id)) {
            mCheckedIds.add(id);
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, id);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView instanceof CheckBox) {
            ((CheckBox) checkedView).setChecked(checked);
        }
    }

    /**
     * @return the unique id of the selected radio button in this group
     * @see #check(int)
     * @see #clearCheck()
     */
    public List<Integer> getCheckedRadioButtonIds() {
        return mCheckedIds;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedRadioButtonIds()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedRadioButtonIds()
     */
    public void clearCheck() {
        check(NO_ID);
    }

    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == CheckBoxesGroup.this && child instanceof CheckBox) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }
                ((CheckBox) child).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == CheckBoxesGroup.this && child instanceof CheckBox) {
                ((CheckBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(CheckBoxesGroup group, @IdRes int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (mCheckedIds.contains(buttonView.getId())) {
                setCheckedStateForView(buttonView.getId(), isChecked);
                if (!isChecked) {
                    mCheckedIds.remove((Integer) buttonView.getId());
                }
            }
            mProtectFromCheckedChange = false;
        }
    }


}
