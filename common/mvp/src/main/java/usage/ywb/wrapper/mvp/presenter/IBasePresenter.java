package usage.ywb.wrapper.mvp.presenter;


import usage.ywb.wrapper.mvp.ui.IBaseView;

/**
 * 所有Presenter的基类接口
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
public interface IBasePresenter<V extends IBaseView> {

    /**
     * 判断 presenter 是否与 view 建立联系，防止出现内存泄露状况
     *
     * @return {@code true}: 联系已建立 {@code false}: 联系已断开
     */
    boolean isViewAttach();

    /**
     * presenter 与 view 建立关联
     *
     * @param view UI
     */
    void attachView(V view);

    /**
     * 断开 presenter 与 view 直接的联系
     */
    void detachView();

}
