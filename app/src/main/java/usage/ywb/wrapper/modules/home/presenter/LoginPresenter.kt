package usage.ywb.wrapper.modules.home.presenter

import usage.ywb.wrapper.common.GlobalCache
import usage.ywb.wrapper.modules.home.contract.LoginContract
import usage.ywb.wrapper.modules.home.contract.LoginContract.LoginView
import usage.ywb.wrapper.modules.home.entity.Domain
import usage.ywb.wrapper.modules.home.entity.Login
import usage.ywb.wrapper.modules.home.entity.User
import usage.ywb.wrapper.modules.home.model.LoginModel
import usage.ywb.wrapper.mvp.net.ConvertObserver
import usage.ywb.wrapper.mvp.net.DefaultObserver
import usage.ywb.wrapper.mvp.presenter.BasePresenter

/**
 * 登录的Presenter，实现对Model的直接调用
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
class LoginPresenter : BasePresenter<LoginView<Login>, LoginModel>(), LoginContract.LoginPresenter {
    override fun login(user: User?) {
        view!!.showLoading("正在登录中...")
        model!!.login(user, object : DefaultObserver<Login>(view) {
            override fun onSucceed(login: Login, tag: Any) {
                super.onSucceed(login, tag)
                GlobalCache.loginRunningUser = login.resultData
                GlobalCache.token = login.token
                view!!.onLoginSucceed(login)
            }

            override fun onFailure(code: Int, msg: String, tag: Any) {
                super.onFailure(code, msg, tag)
                view!!.onLoginFailure()
            }
        })
    }

    override fun getDomain(username: String?) {
        view!!.showLoading("正在获取域名...")
        model!!.getDomain(username, object : ConvertObserver<Domain>(view) {
            override fun convert(domain: Domain, tag: Any) {
                super.convert(domain, tag)
                GlobalCache.domain = domain.domain
                view!!.onDomain(domain)
            }
        })
    }
}