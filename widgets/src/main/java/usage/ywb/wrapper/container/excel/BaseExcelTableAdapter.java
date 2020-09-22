package usage.ywb.wrapper.container.excel;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import usage.ywb.wrapper.container.recycleview.RecycleViewDivider;

/**
 * Excel表格适配器基类
 *
 * @param <LD> 左侧列表单项数据对象
 * @param <TD> 顶部列表单项数据对象
 * @param <RD> 右侧cell单项数据对象
 * @param <LH> 左侧列表Item的ViewHolder
 * @param <TH> 顶部列表Item的ViewHolder
 * @param <RH> 右侧列表cell的ViewHolder
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public abstract class BaseExcelTableAdapter<LD, TD, RD, LH extends RecyclerView.ViewHolder,
        TH extends RecyclerView.ViewHolder, RH extends RecyclerView.ViewHolder> {

    private SparseIntArray topItemWidthArray;

    protected Context context;

    private LeftAdapter leftAdapter;
    private TopAdapter topAdapter;
    private RightAdapter rightAdapter;

    private List<LD> leftList;
    private List<TD> topList;
    private List<List<RD>> rightList;

    /**
     * 右侧一个单元格（Cell）的高度
     */
    private int itemHeight;

    /**
     * Excel竖直方向已滑动的距离
     */
    private int scrollY;


    public BaseExcelTableAdapter(Context context) {
        this.context = context;
        topItemWidthArray = new SparseIntArray();
        leftAdapter = new LeftAdapter();
        topAdapter = new TopAdapter();
        rightAdapter = new RightAdapter();
    }

    public void setDataList(List<LD> leftList, List<TD> topList, List<List<RD>> rightList) {
        this.leftList = leftList;
        this.topList = topList;
        this.rightList = rightList;
    }

    /**
     * 刷新时重新结算竖直列的宽度
     */
    public void notifyDataAndResizeSetChanged() {
        topItemWidthArray.clear();
        if (leftAdapter != null) {
            leftAdapter.notifyDataSetChanged();
        }
        if (topAdapter != null) {
            topAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        if (leftAdapter != null) {
            leftAdapter.notifyDataSetChanged();
        }
        if (topAdapter != null) {
            topAdapter.notifyDataSetChanged();
        }
        if (rightAdapter != null) {
            rightAdapter.notifyDataSetChanged();
        }
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public LeftAdapter getLeftAdapter() {
        return leftAdapter;
    }

    public TopAdapter getTopAdapter() {
        return topAdapter;
    }

    public RightAdapter getRightAdapter() {
        return rightAdapter;
    }

    class LeftAdapter extends RecyclerView.Adapter<LH> {

        @NonNull
        @Override
        public LH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("LeftAdapter", "onCreateViewHolder:");
            return onCreateLeftViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull LH holder, int position) {
            onBindLeftViewHolder(holder, leftList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return leftList == null ? 0 : leftList.size();
        }
    }

    class TopAdapter extends RecyclerView.Adapter<TH> {

        @NonNull
        @Override
        public TH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("TopAdapter", "onCreateViewHolder:");
            return onCreateTopViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull final TH holder, int position) {
            onBindTopViewHolder(holder, topList.get(position), position);
            if (topItemWidthArray.get(position) == 0) {
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = holder.getAdapterPosition();
                        topItemWidthArray.put(position, holder.itemView.getWidth());
                        rightAdapter.notifyItemChanged(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return topList == null ? 0 : topList.size();
        }
    }

    /**
     * 右侧每一竖直列的适配器
     */
    class RightAdapter extends RecyclerView.Adapter<RightViewHolder> {

        RecyclerView.OnScrollListener onScrollListener;

        public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
            this.onScrollListener = onScrollListener;
        }

        @NonNull
        @Override
        public RightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("RightAdapter", "onCreateViewHolder:");
            // 每一列又是一个列表
            RecyclerView recyclerView = new RecyclerView(context);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recyclerView.setNestedScrollingEnabled(false);
            if (onScrollListener != null) {
                recyclerView.addOnScrollListener(onScrollListener);
            }
            recyclerView.addItemDecoration(new RecycleViewDivider(context, RecyclerView.HORIZONTAL));
            recyclerView.setAdapter(new ItemAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    context, RecyclerView.VERTICAL, false));
            return new RightViewHolder(recyclerView);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull RightViewHolder holder, int position) {
            RecyclerView recyclerView = ((RecyclerView) holder.itemView);
            Log.i("RightAdapter", "onBindViewHolder：" + position);
            if (topItemWidthArray.get(position) != 0) {
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.width = topItemWidthArray.get(position);
                recyclerView.setLayoutParams(params);
            }

            ItemAdapter adapter = (ItemAdapter) holder.recyclerView.getAdapter();
            adapter.setItemList(rightList.get(position));
            adapter.setRowPosition(position);

//            ExcelTableView.fastScrollVertical(recyclerView, scrollY);
        }

        @Override
        public int getItemCount() {
            return rightList == null ? 0 : rightList.size();
        }

    }

    class ItemAdapter extends RecyclerView.Adapter<RH> {

        int rowPosition = 0;

        List<RD> itemList;

        public void setItemList(List<RD> itemList) {
            this.itemList = itemList;
        }

        void setRowPosition(int position) {
            this.rowPosition = position;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("ItemAdapter", "onCreateViewHolder:");
            return onCreateCellViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RH holder, int position) {
            onBindCellViewHolder(holder, rightList.get(rowPosition).get(position),
                    rowPosition, position);
        }

        @Override
        public int getItemCount() {
            return itemList == null ? 0 : itemList.size();
        }
    }

    class RightViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        RightViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView;
        }
    }

    protected abstract LH onCreateLeftViewHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract TH onCreateTopViewHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract RH onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract void onBindLeftViewHolder(LH holder, LD item, int position);

    protected abstract void onBindTopViewHolder(TH holder, TD item, int position);

    protected abstract void onBindCellViewHolder(RH holder, RD item, int horizontalIndex, int verticalIndex);


}
