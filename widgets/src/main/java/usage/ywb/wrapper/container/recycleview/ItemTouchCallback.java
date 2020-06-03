package usage.ywb.wrapper.container.recycleview;

import android.os.Build;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class ItemTouchCallback extends ItemTouchHelper.SimpleCallback {

    private RecyclerView.Adapter adapter;
    private List mData;

    public ItemTouchCallback(RecyclerView.Adapter adapter, List mData) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0);
        this.adapter = adapter;
        this.mData = mData;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFrlg = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFrlg, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //侧滑删除可以使用；
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 长按选中Item的时候开始调用
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.itemView.setElevation(20);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 手指松开的时候还原View
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.itemView.setElevation(0);
        }
        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }


}
