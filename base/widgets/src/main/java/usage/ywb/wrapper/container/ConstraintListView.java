package usage.ywb.wrapper.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import usage.ywb.wrapper.widgets.R;


/**
 * Created by Administrator on 2017/10/25.
 */
public class ConstraintListView extends ListView {

    private static final int[] ATTRS = new int[]{android.R.attr.maxHeight};

    private float mMaxHeight;

    public ConstraintListView(Context context) {
        this(context, null);
    }

    public ConstraintListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public ConstraintListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, ATTRS);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == android.R.attr.maxHeight) {
                //获得布局中限制的最大高度
                mMaxHeight = array.getDimension(type, -1);
            }
        }
        array.recycle();
    }


    /**
     * 基于Item的高度，计算出ListView的高度，并设置
     */
    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, this);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = totalHeight + (getDividerHeight() * (listAdapter.getCount() - 1));
        setLayoutParams(params);
    }

    /**
     * 设置listView高度跟随子内容变化（Item高度固定）
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            //获取lv本身高度
            int specSize = MeasureSpec.getSize(heightMeasureSpec);
            //限制高度小于lv高度,设置为限制高度
            if (mMaxHeight <= specSize) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(mMaxHeight).intValue(), MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }
}
