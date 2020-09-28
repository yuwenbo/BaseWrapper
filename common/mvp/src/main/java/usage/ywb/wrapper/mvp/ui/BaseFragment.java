package usage.ywb.wrapper.mvp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import usage.ywb.wrapper.mvp.presenter.IBasePresenter;
import usage.ywb.wrapper.mvp.proxy.IPresenterProxy;
import usage.ywb.wrapper.mvp.proxy.PresenterProxyImpl;

/**
 * 通过{@link IBasePresenter}，来实现View层对Presenter的依赖，同时做了内存泄漏的预防处理。
 * <p>
 * 为所有Presenter字段添加注解{@link usage.ywb.wrapper.mvp.presenter.InjectPresenter}实现Presenter实例注入
 *
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public abstract class BaseFragment extends Fragment implements IBaseView {


    private LoadingDialog loadingDialog;

    private IPresenterProxy mPresenterProxy;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterProxy = new PresenterProxyImpl(this);
        mPresenterProxy.bindPresenter();
    }

    @Override
    public void showLoading(String msg) {
        if (getContext() == null) {
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
        }
        if (!TextUtils.isEmpty(msg)) {
            loadingDialog.setMessage(msg);
        }
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void hideError() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void hideEmpty() {

    }

    @Override
    public void showShortToast(String msg) {
        if (getContext() == null) {
            return;
        }
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLongToast(String msg) {
        if (getContext() == null) {
            return;
        }
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        mPresenterProxy.unbindPresenter();
        super.onDestroy();
    }


}
