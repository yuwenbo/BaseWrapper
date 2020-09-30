package usage.ywb.wrapper.modules.home.presenter;


import usage.ywb.wrapper.common.GlobalCache;
import usage.ywb.wrapper.modules.home.contract.LoginContract;
import usage.ywb.wrapper.modules.home.entity.Domain;
import usage.ywb.wrapper.modules.home.entity.Login;
import usage.ywb.wrapper.modules.home.entity.User;
import usage.ywb.wrapper.modules.home.model.LoginModel;
import usage.ywb.wrapper.mvp.net.ConvertObserver;
import usage.ywb.wrapper.mvp.net.DefaultObserver;
import usage.ywb.wrapper.mvp.presenter.BasePresenter;

/**
 * 登录的Presenter，实现对Model的直接调用
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
public class LoginPresenter extends BasePresenter<LoginContract.LoginView<Login>, LoginModel> implements LoginContract.LoginPresenter {

    public LoginPresenter() {

    }

    @Override
    public void login(User user) {
        getView().showLoading("正在登录中...");
        getModel().login(user, new DefaultObserver<Login>(getView()) {
            @Override
            public void onSucceed(Login login, Object tag) {
                super.onSucceed(login, tag);
                GlobalCache.setLoginRunningUser(login.resultData);
                GlobalCache.setToken(login.token);
                getView().onLoginSucceed(login);
            }

            @Override
            public void onFailure(int code, String msg, Object tag) {
                super.onFailure(code, msg, tag);
                getView().onLoginFailure();
            }
        });
    }


    @Override
    public void getDomain(String username) {
        getView().showLoading("正在获取域名...");
        getModel().getDomain(username, new ConvertObserver<Domain>(getView()) {
            @Override
            public void convert(Domain domain, Object tag) {
                super.convert(domain, tag);
                GlobalCache.setDomain(domain.domain);
                getView().onDomain(domain);
            }
        });
    }

}
