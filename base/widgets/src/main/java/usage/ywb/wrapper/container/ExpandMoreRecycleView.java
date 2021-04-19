package usage.ywb.wrapper.container;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import usage.ywb.wrapper.widgets.R;

/**
 * 一个带有更多按钮的网格布局
 *
 * @author Kingdee.ywb
 * @version [ V.2.7.9  2020/3/31 ]
 */
public class ExpandMoreRecycleView extends RecyclerView {

    private int row = 3;
    private int span = 3;

    private GridLayoutManager layoutManager;

    public ExpandMoreRecycleView(Context context) {
        this(context, null);
    }

    public ExpandMoreRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandMoreRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layoutManager = new GridLayoutManager(context, span);
        setLayoutManager(layoutManager);
    }

    public void setRow(int row) {
        if (getAdapter() instanceof ExpandMoreAdapter) {
            ExpandMoreAdapter moreAdapter = (ExpandMoreAdapter) getAdapter();
            moreAdapter.setRow(row);
            if (this.row != row) {
                getAdapter().notifyDataSetChanged();
            }
        }
        this.row = row;
    }

    public void setSpan(int span) {
        if (getAdapter() instanceof ExpandMoreAdapter) {
            ExpandMoreAdapter moreAdapter = (ExpandMoreAdapter) getAdapter();
            moreAdapter.setSpan(span);
            if (this.span != span) {
                getAdapter().notifyDataSetChanged();
            }
        }
        this.span = span;
        layoutManager.setSpanCount(span);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof ExpandMoreAdapter) {
            ExpandMoreAdapter moreAdapter = (ExpandMoreAdapter) adapter;
            moreAdapter.setSpan(span);
            moreAdapter.setRow(row);
        }
        super.setAdapter(adapter);
    }

    public abstract static class ExpandMoreAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

        protected List<T> mData;
        private boolean isExpand = false;

        protected int row = 3;
        protected int span = 3;

        private void setRow(int row) {
            this.row = row;
        }

        private void setSpan(int span) {
            this.span = span;
        }

        private static final int TYPE_NORMAL = 0;
        private static final int TYPE_MORE = 1;

        public void setData(List<T> mData) {
            this.mData = mData;
        }

        public void setExpand(boolean expand) {
            isExpand = expand;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < getItemCount() - 1) {
                return TYPE_NORMAL;
            }
            if (position == getItemCount() - 1) {
                if (mData.size() > position + 1) {
                    return TYPE_MORE;
                } else {
                    return TYPE_NORMAL;
                }
            }
            return TYPE_NORMAL;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_MORE) {
                return onCreateMoreHolder(parent);
            }
            return onCreateItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == TYPE_MORE) {
                onBindMoreHolder(holder, position);
            } else if (type == TYPE_NORMAL) {
                onBindItemHolder(holder, null, position);
            }
        }

        public abstract ViewHolder onCreateItemHolder(@NonNull ViewGroup parent);

        public abstract ViewHolder onCreateMoreHolder(@NonNull ViewGroup parent);

        public abstract void onBindItemHolder(@NonNull RecyclerView.ViewHolder holder, T item, int position);

        public abstract void onBindMoreHolder(@NonNull RecyclerView.ViewHolder holder, int position);

        public T getItem(int position) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            if (mData == null) {
                return 0;
            }
            int size = mData.size();
            return size <= row * span ? size : isExpand ? size : row * span;
        }

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView;
        }
    }

    public static class MoreHolder extends RecyclerView.ViewHolder {

        public TextView moreTv;

        public MoreHolder(View itemView) {
            super(itemView);
            moreTv = (TextView) itemView;
        }
    }


}
