package usage.ywb.wrapper.container;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

/**
 * @author ywb
 * @version [ V.1.0.0  2018/4/8 ]
 */
public class PinnedHeaderExpandableListView extends ExpandableListView implements AbsListView.OnScrollListener{

    private static final String TAG = PinnedHeaderExpandableListView.class.getSimpleName();

    /**
     * 显示在顶部的头，指示器
     */
    private LinearLayout headerLayout;

    /**
     * 记录当前列表中显示在屏幕最顶部的第一个Item所属的组
     */
    private int currGroup;

    /**
     * 组的高度
     */
    private int groupHeight = 0;

    public PinnedHeaderExpandableListView(Context context) {
        this(context, null);
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final long packedPosition = getExpandableListPosition(firstVisibleItem);

        currGroup = ExpandableListView.getPackedPositionGroup(packedPosition);

        // 为指示器显示正确的selector
        if (isGroupExpanded(currGroup)) {
            headerLayout.setVisibility(View.VISIBLE);
        } else {
            headerLayout.setVisibility(View.GONE);
        }

        int showHeight = groupHeight;
        // 屏幕显示的第二个item在ListView中的整体下标的位置
        final int secondVisiblePosition = pointToPosition(0, groupHeight);
        if (secondVisiblePosition == AdapterView.INVALID_POSITION) {
            return;
        }
        final long packedPosition2 = getExpandableListPosition(secondVisiblePosition);
        final int currGroup2 = ExpandableListView.getPackedPositionGroup(packedPosition2);

        Log.i(TAG, "2-PackedPositionGroup: " + currGroup2);

        if (currGroup2 != currGroup) {
            final View viewNext = getChildAt(secondVisiblePosition - getFirstVisiblePosition());
            showHeight = viewNext.getTop();
        }

        // 更新组的位置，实现退出拉入的效果
        final MarginLayoutParams layoutParams = (MarginLayoutParams) headerLayout.getLayoutParams();
        layoutParams.topMargin = -(groupHeight - showHeight)-1;
        headerLayout.setLayoutParams(layoutParams);
    }

}
