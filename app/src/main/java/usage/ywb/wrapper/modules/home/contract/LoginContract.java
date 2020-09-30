package usage.ywb.wrapper.modules.home.contract;

import io.reactivex.observers.DefaultObserver;
import usage.ywb.wrapper.modules.home.entity.Domain;
import usage.ywb.wrapper.modules.home.entity.Login;
import usage.ywb.wrapper.modules.home.entity.User;
import usage.ywb.wrapper.mvp.ui.IBaseView;
import usage.ywb.wrapper.mvp.net.ConvertObserver;

/**
 * 登录的 MVP “代理” 类
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
public class LoginContract {

    /**
     * 登录UI层接口
     *
     * @param <T>
     */
    public interface LoginView<T> extends IBaseView {

        /**
         * 登陆成功的回调
         *
         * @param t 对象
         */
        void onLoginSucceed(T t);

        /**
         * 登录失败
         */
        void onLoginFailure();

        void onDomain(Domain domain);

    }

    /**
     * 登录Presenter层接口
     */
    public interface LoginPresenter {
        /**
         * Presenter登录接口，UI与Model的桥梁
         *
         * @param user 登录用户
         */
        void login(User user);
        /**
         * 获取域名
         */
        void getDomain(String username);
    }

    /**
     * 登录Model层接口
     */
    public interface LoginModel {
        /**
         * 登录，Model与NET直接访问的接口
         *
         * @param user      登录用户
         */
        void login(User user, DefaultObserver<Login> observer);

        /**
         * 获取域名
         */
        void getDomain(String username, ConvertObserver<Domain> observer);

    }


}
