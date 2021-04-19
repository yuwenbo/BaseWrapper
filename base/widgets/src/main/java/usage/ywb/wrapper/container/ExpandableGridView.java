package usage.ywb.wrapper.container;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

/**
 * @author Kingdee.ywb
 * @version [ V.2.3.3  2018/11/05 ]
 */
public class ExpandableGridView extends ExpandableListView {

    private OnChildClickListener onChildClickListener;
    private ExpandableGridAdapter mAdapter;

    private int horizontalSpacing;
    private int verticalSpacing;

    public ExpandableGridView(Context context) {
        this(context, null);
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        if(mAdapter!=null){
            mAdapter.setOnChildClickListener(onChildClickListener);
        }
    }

    /**
     * This overloaded method should not be used, instead use
     * {@link #setAdapter(ExpandableGridAdapter)}.
     * <p>
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException(
                "For ExpandableGridView, use setExpandableGridAdapter(ExpandableGridAdapter) instead of " +
                        "setAdapter(ListAdapter)");
    }

    /**
     * This overloaded method should not be used, instead use
     * {@link #setAdapter(ExpandableGridAdapter)}.
     * <p>
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        throw new RuntimeException(
                "For ExpandableGridView, use setExpandableGridAdapter(ExpandableGridAdapter) instead of " +
                        "setAdapter(ExpandableListAdapter)");
    }

    /**
     * Sets the adapter that provides data to this view.
     *
     * @param adapter The adapter that provides data to this view.
     */
    public void setAdapter(ExpandableGridAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(adapter);
    }

    /**
     * This method should not be used, use {@link #getExpandableGridAdapter()}.
     */
    @Deprecated
    @Override
    public ListAdapter getAdapter() {
        return super.getAdapter();
    }

    /**
     * This method should not be used, use {@link #getExpandableGridAdapter()}.
     */
    @Deprecated
    @Override
    public ExpandableListAdapter getExpandableListAdapter() {
        return super.getExpandableListAdapter();
    }

    /**
     * Gets the adapter that provides data to this view.
     *
     * @return The adapter that provides data to this view.
     */
    public ExpandableGridAdapter getExpandableGridAdapter() {
        return mAdapter;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        if (mAdapter != null) {
            mAdapter.setHorizontalSpacing(horizontalSpacing);
        }
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
        if (mAdapter != null) {
            mAdapter.setVerticalSpacing(verticalSpacing);
        }
    }

    public void expandAll(boolean animate) {
        if (mAdapter == null) {
            return;
        }
        int count = getExpandableGridAdapter().getGroupCount();
        for (int groupPosition = 0; groupPosition < count; groupPosition++) {
            expandGroup(groupPosition, animate);
        }
    }


}
