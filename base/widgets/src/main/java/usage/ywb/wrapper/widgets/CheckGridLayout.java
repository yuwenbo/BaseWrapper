package usage.ywb.wrapper.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/10/16 ]
 */
public class CheckGridLayout extends ViewGroup {

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTIPLE = 0;

    private int dividerVertical = 0;
    private int dividerHorizontal = 0;
    private int columnCount = 1;

    private int mChildWidth, mChildHeight;
    private int mode = MODE_MULTIPLE;

    private List<Integer> mCheckedIds;

    private boolean isCreated = false;
    private boolean mProtectFromCheckedChange = false;


    private CheckGridLayout.OnCheckedChangeListener mOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private CheckGridLayout.PassThroughHierarchyChangeListener mPassThroughListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    public CheckGridLayout(Context context) {
        this(context, null);
    }

    public CheckGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CheckGridLayout);
        for (int i = 0, len = array.getIndexCount(); i < len; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.CheckGridLayout_android_columnCount) {
                columnCount = array.getInteger(attr, columnCount);
            } else if (attr == R.styleable.CheckGridLayout_android_dividerVertical) {
                dividerVertical = array.getDimensionPixelSize(attr, dividerVertical);
            } else if (attr == R.styleable.CheckGridLayout_android_dividerHorizontal) {
                dividerHorizontal = array.getDimensionPixelSize(attr, dividerHorizontal);
            } else if (attr == R.styleable.CheckGridLayout_checkMode) {
                mode = array.getInt(attr, MODE_MULTIPLE);
            }
        }
        array.recycle();
        init();
    }

    private void init() {
        mCheckedIds = new ArrayList<>();
        mChildOnCheckedChangeListener = new CheckGridLayout.CheckedStateTracker();
        mPassThroughListener = new CheckGridLayout.PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 根据XML文件中的请求检查相应的选择按钮
        if (!mCheckedIds.isEmpty()) {
            for (int checkId : mCheckedIds) {
                if (checkId != NO_ID) {
                    mProtectFromCheckedChange = true;
                    setCheckedStateForView(checkId, true);
                    mProtectFromCheckedChange = false;
                    setCheckedId(checkId, true);
                }
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof CheckBox) {
            final CheckBox button = (CheckBox) child;
            if (button.isChecked()) {
                if (mode == MODE_SINGLE) {
                    mProtectFromCheckedChange = true;
                    if (!mCheckedIds.isEmpty()) {
                        setCheckedStateForView(mCheckedIds.get(0), false);
                    }
                    mProtectFromCheckedChange = false;
                }
                setCheckedId(button.getId(), true);
            }
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mChildWidth = (getMeasuredWidth() - (columnCount - 1) * dividerVertical - getPaddingLeft() - getPaddingStart()) / columnCount;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingBottom() + getPaddingTop(), childView.getHeight());
            int height = MeasureSpec.getSize(childHeightMeasureSpec);
            mChildHeight = Math.max(mChildHeight, height);
        }
        if (mChildHeight <= 0) {
            mChildHeight = getResources().getDimensionPixelSize(R.dimen.small_view_height);
        }
        int rowCount = childCount / columnCount + (childCount % columnCount == 0 ? 0 : 1);
        int mHeight = rowCount * mChildHeight + (rowCount - 1) * dividerHorizontal + getPaddingTop() + getPaddingBottom();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(
                resolveSizeAndState(getMeasuredWidth(), widthMeasureSpec, 0),
                resolveSizeAndState(mHeight, heightMeasureSpec, 0));
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (isCreated) {
            return;
        }
        isCreated = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int childLeft = (i % columnCount) * (mChildWidth + dividerVertical) + getPaddingLeft();
            int childTop = (i / columnCount) * (mChildHeight + dividerHorizontal) + getPaddingTop();

            if (mChildWidth != childView.getMeasuredWidth() || mChildHeight != childView.getMeasuredHeight()) {
                childView.measure(
                        MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY));
            }
            childView.layout(childLeft, childTop, childLeft + mChildWidth, childTop + mChildHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isCreated = false;
        requestLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    public void check(@IdRes int... checkIds) {
        if (checkIds == null || checkIds.length == 0) {
            return;
        }
        if (mode == MODE_SINGLE) {
            if (checkIds.length > 1) {
                throw new RuntimeException("单选模式下不得传入多个ID");
            }
            int exCheckId = mCheckedIds.isEmpty() ? NO_ID : mCheckedIds.get(0);
            if (exCheckId != NO_ID) {
                setCheckedStateForView(exCheckId, false);
            }
            int checkId = checkIds[0];
            if (checkId != NO_ID) {
                setCheckedStateForView(checkId, true);
            }
        } else {
            for (int checkId : checkIds) {
                setCheckedStateForView(checkId, true);
            }
        }
    }

    private void setCheckedId(@IdRes int id, boolean isChecked) {
        if (mode == MODE_SINGLE) {
            mCheckedIds.clear();
            if (id != NO_ID) {
                mCheckedIds.add(id);
            }
        } else {
            if (id != NO_ID) {
                if (isChecked) {
                    if (!mCheckedIds.contains(id)) {
                        mCheckedIds.add(id);
                    }
                } else {
                    mCheckedIds.remove((Integer) id);
                }
            }
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, (CheckBox) findViewById(id), true);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView instanceof CheckBox) {
            ((CheckBox) checkedView).setChecked(checked);
        }
    }

    public void clearCheck() {
        if (mode == MODE_SINGLE) {
            check(NO_ID);
        } else {
            mProtectFromCheckedChange = true;
            for (int checkId : mCheckedIds) {
                setCheckedStateForView(checkId, false);
            }
            mProtectFromCheckedChange = false;
            mCheckedIds.clear();
        }
    }

    public List<Integer> getCheckedIds() {
        if (mCheckedIds.isEmpty()) {
            return null;
        }
        return mCheckedIds;
    }

    public List<String> getCheckTags() {
        if (mCheckedIds == null) {
            return null;
        }
        List<String> tags = new ArrayList<>();
        for (int checkId : mCheckedIds) {
            CheckBox checkBox = findViewById(checkId);
            if (checkBox != null && checkBox.getTag() instanceof String) {
                tags.add((String) checkBox.getTag());
            }
        }
        return tags;
    }

    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == CheckGridLayout.this && child instanceof CheckBox) {
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
            if (parent == CheckGridLayout.this && child instanceof CheckBox) {
                ((CheckBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 防止无限递归
            if (mProtectFromCheckedChange) {
                return;
            }
            if (mode == MODE_SINGLE) {
                mProtectFromCheckedChange = true;
                int checkId = mCheckedIds.isEmpty() ? NO_ID : mCheckedIds.get(0);
                if (buttonView.getId() == checkId) {
                    setCheckedStateForView(checkId, isChecked);
                    if (!isChecked) {
                        checkId = NO_ID;
                    }
                } else {
                    setCheckedStateForView(checkId, false);
                    checkId = buttonView.getId();
                }
                mProtectFromCheckedChange = false;
                setCheckedId(checkId, true);
            } else {
                mProtectFromCheckedChange = true;
                int checkId = buttonView.getId();
                setCheckedStateForView(checkId, isChecked);
                mProtectFromCheckedChange = false;
                setCheckedId(checkId, isChecked);
            }
        }
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CheckGridLayout parent, CheckBox checkBox, boolean isChecked);
    }

}
