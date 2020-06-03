package usage.ywb.wrapper.mvp.proxy;


import usage.ywb.wrapper.mvp.presenter.IBasePresenter;
import usage.ywb.wrapper.mvp.presenter.InjectPresenter;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2019/11/28 ]
 */
public interface IPresenterProxy {

    /**
     * 为所有Presenter注入实例，Presenter必须是{@link IBasePresenter}的子类或实现类
     * 并建立Presenter与View层绑定关系
     *
     * @see InjectPresenter
     */
    void bindPresenter();

    /**
     * 解除View与Presenter的绑定关系
     */
    void unbindPresenter();

}
