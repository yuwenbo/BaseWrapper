package usage.ywb.wrapper.modules.home.contract

import io.reactivex.observers.DefaultObserver
import usage.ywb.wrapper.modules.home.entity.Domain
import usage.ywb.wrapper.modules.home.entity.Login
import usage.ywb.wrapper.modules.home.entity.User
import usage.ywb.wrapper.mvp.net.ConvertObserver
import usage.ywb.wrapper.mvp.ui.IBaseView

/**
 * 登录的 MVP “代理” 类
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
class LoginContract {
    /**
     * 登录UI层接口
     *
     * @param <T>
    </T> */
    interface LoginView<T> : IBaseView {
        /**
         * 登陆成功的回调
         *
         * @param t 对象
         */
        fun onLoginSucceed(t: T)

        /**
         * 登录失败
         */
        fun onLoginFailure()
        fun onDomain(domain: Domain?)
    }

    /**
     * 登录Presenter层接口
     */
    interface LoginPresenter {
        /**
         * Presenter登录接口，UI与Model的桥梁
         *
         * @param user 登录用户
         */
        fun login(user: User?)

        /**
         * 获取域名
         */
        fun getDomain(username: String?)
    }

    /**
     * 登录Model层接口
     */
    interface LoginModel {
        /**
         * 登录，Model与NET直接访问的接口
         *
         * @param user      登录用户
         */
        fun login(user: User?, observer: DefaultObserver<Login>?)

        /**
         * 获取域名
         */
        fun getDomain(username: String?, observer: ConvertObserver<Domain>?)
    }
}