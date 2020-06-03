package usage.ywb.wrapper.container.recycleview;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

import usage.ywb.wrapper.container.recycleview.base.ItemViewDelegate;
import usage.ywb.wrapper.container.recycleview.base.ViewHolder;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    protected int mLayoutId;
    protected LayoutInflater mInflater;

    public CommonAdapter(final Context context, final int layoutId, List<T> datas) {
        super(context, datas);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ViewHolder holder, T t, int position);


}
