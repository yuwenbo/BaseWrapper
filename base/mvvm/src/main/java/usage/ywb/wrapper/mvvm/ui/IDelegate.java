package usage.ywb.wrapper.mvvm.ui;

import android.app.Activity;

import androidx.databinding.ViewDataBinding;

/**
 * 这是Activity和Fragment需要实现的接口，
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/14 ]
 */
public interface IDelegate {

    <T extends Activity> T getActivity();

    /**
     * 显示正在加载中
     */
    void showLoading(String msg);

    /**
     * 隐藏正在加载中
     */
    void hideLoading();

    /**
     * 显示错误信息
     */
    void showError();

    /**
     * 隐藏错误信息
     */
    void hideError();

    /**
     * 显示空页面
     */
    void showEmpty();

    /**
     * 隐藏空页面元素
     */
    void hideEmpty();

    void showShortToast(String msg);

    void showLongToast(String msg);

}
