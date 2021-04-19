package usage.ywb.wrapper.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView是使用LayoutManager来布局自己的子view的，它并不知道LayoutManager如何放置子view。
 * 因此RecyclerView不知道到底该把AnimationParameter应用到list上还是grid上，而默认是list。
 * 为了修复这个问题我们需要一个自定义的RecyclerView，让它知道GridLayoutManager的存在
 *
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/15 ]
 */
public class GridRecyclerView extends RecyclerView {

    public GridRecyclerView(Context context) {
        super(context);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params,
                                                   int index, int count) {
        final LayoutManager layoutManager = getLayoutManager();
        if (getAdapter() != null && layoutManager instanceof GridLayoutManager) {

            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                // If there are no animation parameters, create new once and attach them to
                // the LayoutParams.
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            // Next we are updating the parameters

            // Set the number of items in the RecyclerView and the index of this item
            animationParams.count = count;
            animationParams.index = index;

            // Calculate the number of columns and rows in the grid
            final int columns = ((GridLayoutManager) layoutManager).getSpanCount();
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            // Calculate the column/row position in the grid
            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            // Proceed as normal if using another type of LayoutManager
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }
}