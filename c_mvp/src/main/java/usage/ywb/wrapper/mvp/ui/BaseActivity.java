package usage.ywb.wrapper.mvp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import usage.ywb.wrapper.mvp.presenter.IBasePresenter;
import usage.ywb.wrapper.mvp.proxy.IPresenterProxy;
import usage.ywb.wrapper.mvp.proxy.PresenterProxyImpl;


/**
 * 通过{@link IBasePresenter}，来实现View层对Presenter的依赖，同时做了内存泄漏的预防处理。
 * <p>
 * 为所有Presenter字段添加注解{@link usage.ywb.wrapper.mvp.presenter.InjectPresenter}实现Presenter实例注入
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/14 ]
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseView, IDelegate {


    private LoadingDialog loadingDialog;

    private IPresenterProxy mPresenterProxy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterProxy = new PresenterProxyImpl(this);
        mPresenterProxy.bindPresenter();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initContentView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initContentView();
    }

    /**
     * 在调用setContentView之后执行
     */
    protected void initContentView(){

    }

    @Override
    public void showLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        mPresenterProxy.unbindPresenter();
        super.onDestroy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Activity> T getActivity() {
        return (T) this;
    }


}
