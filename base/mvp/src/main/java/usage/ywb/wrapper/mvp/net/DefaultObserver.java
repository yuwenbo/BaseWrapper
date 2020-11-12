package usage.ywb.wrapper.mvp.net;


import usage.ywb.wrapper.mvp.common.entity.Entity;
import usage.ywb.wrapper.mvp.ui.IBaseView;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/17 ]
 */
public abstract class DefaultObserver<T extends Entity> extends io.reactivex.observers.DefaultObserver<T> {

    protected Object tag;

    protected IBaseView baseView;

    public DefaultObserver(IBaseView baseView) {
        this.baseView = baseView;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public void onNext(T t) {
        if (t.resultCode == 200 && t.result == 1) {
            onSucceed(t, tag);
        } else {
            onFailure(t.result, t.resultDesc, tag);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onFailure(0, e.getMessage(), tag);
    }

    @Override
    public void onComplete() {

    }

    public void onSucceed(T t, Object tag) {
        baseView.hideLoading();

    }

    public void onFailure(int code, String msg, Object tag) {
        baseView.hideLoading();
        baseView.showShortToast(msg);
    }

}
