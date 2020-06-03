package usage.ywb.wrapper.container.recycleview.base;


/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
