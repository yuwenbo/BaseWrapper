package usage.ywb.wrapper.mvp.net;

import usage.ywb.wrapper.mvp.common.entity.ConvertEntity;
import usage.ywb.wrapper.mvp.ui.IBaseView;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2019/12/12 ]
 */
public abstract class ConvertObserver<E> extends DefaultObserver<ConvertEntity<E>> {


    public ConvertObserver(IBaseView baseView) {
        super(baseView);
    }

    @Override
    public void onSucceed(ConvertEntity<E> eConvertEntity, Object tag) {
        convert(eConvertEntity.resultData, tag);
    }

    public void convert(E e, Object tag){
        baseView.hideLoading();
    }

}
