package usage.ywb.wrapper.container.recycleview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * recycleview 分隔符（垂直间隔无效）
 *
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class GridManagerDivider extends RecyclerView.ItemDecoration {

    private int spanCount; //列数
    private int horizontalSpace; //水平间隔
    private int veticalSpace; //垂直间隔
    private boolean includeEdge; //是否包含左右边缘

    public GridManagerDivider(int spanCount, int horizontalSpace, int veticalSpace, boolean includeEdge) {
        this.spanCount = spanCount;
        this.horizontalSpace = horizontalSpace;
        this.veticalSpace = veticalSpace;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        if (includeEdge) {
            //左右相加刚好是一个space
            outRect.left = horizontalSpace - column * horizontalSpace / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * horizontalSpace / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//            if (position < spanCount) { // top edge
//                outRect.top = veticalSpace;
//            }
            outRect.bottom = veticalSpace; // item bottom
        } else {
            if (column == 0) {
                outRect.left = 0;
                outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            } else if (column == spanCount - 1) {
                outRect.left = column * horizontalSpace / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = 0;
            } else {
                outRect.left = column * horizontalSpace / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            }
//            if (position < spanCount) {
//                outRect.top = veticalSpace; // item top
//            }
            outRect.bottom = veticalSpace; // item bottom
        }
    }
}
