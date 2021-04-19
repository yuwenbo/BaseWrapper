package usage.ywb.wrapper.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

/**
 * @author Kingdee.ywb
 * @version [ V.2.3.3  2018/11/5 ]
 */
public abstract class ExpandableGridAdapter extends BaseExpandableListAdapter {

    private ExpandableListView.OnChildClickListener onChildClickListener;

    private int horizontalSpacing;
    private int verticalSpacing;

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public void setOnChildClickListener(ExpandableListView.OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    @Override
    public int getGroupCount() {
        return getGridGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Deprecated
    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Deprecated
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Deprecated
    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Deprecated
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return getGridGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        GridAdapter gridAdapter;
        if (convertView == null) {
            convertView = new GridView(parent.getContext());
            gridAdapter = new GridAdapter(groupPosition);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            convertView.setVerticalScrollBarEnabled(false);
            ((GridView) convertView).setHorizontalSpacing(horizontalSpacing);
            ((GridView) convertView).setVerticalSpacing(verticalSpacing);
            ((GridView) convertView).setAdapter(gridAdapter);
        } else {
            gridAdapter = (GridAdapter) ((GridView) convertView).getAdapter();
            gridAdapter.setGridGroupPosition(groupPosition);
            gridAdapter.notifyDataSetChanged();
        }
        ((GridView) convertView).setNumColumns(getNumColumns(groupPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public abstract int getGridGroupCount();

    public abstract int getGridChildCount(int gridGroupPosition);

    public abstract View getGridGroupView(int gridGroupPosition, boolean isExpanded, View convertView, ViewGroup parent);

    public abstract View getGridChildView(int gridGroupPosition, int gridChildPosition, View convertView, ViewGroup parent);

    public abstract int getNumColumns(int gridGroupPosition);


    private class GridView extends android.widget.GridView implements AdapterView.OnItemClickListener {

        private GridAdapter mAdapter;

        public GridView(Context context) {
            this(context, null);
        }

        public GridView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setOnItemClickListener(this);
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            if (adapter instanceof GridAdapter) {
                mAdapter = (GridAdapter) adapter;
            }
            super.setAdapter(adapter);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (onChildClickListener != null && mAdapter != null) {
                onChildClickListener.onChildClick(null, view, mAdapter.getGridGroupPosition(), position, id);
            }
        }
    }


    private class GridAdapter extends BaseAdapter {

        private int gridGroupPosition;

        GridAdapter(int gridGroupPosition) {
            this.gridGroupPosition = gridGroupPosition;
        }

        public int getGridGroupPosition() {
            return gridGroupPosition;
        }

        void setGridGroupPosition(int gridGroupPosition) {
            this.gridGroupPosition = gridGroupPosition;
        }

        @Override
        public int getCount() {
            return getGridChildCount(gridGroupPosition);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getGridChildView(gridGroupPosition, position, convertView, parent);
        }

    }
}
