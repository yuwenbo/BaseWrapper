package usage.ywb.wrapper.modules.home.model

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import usage.ywb.wrapper.api.ILoginApi
import usage.ywb.wrapper.common.Key
import usage.ywb.wrapper.modules.home.contract.LoginContract
import usage.ywb.wrapper.modules.home.entity.Domain
import usage.ywb.wrapper.modules.home.entity.Login
import usage.ywb.wrapper.modules.home.entity.User
import usage.ywb.wrapper.mvp.model.BaseModel
import usage.ywb.wrapper.mvp.net.ConvertObserver
import usage.ywb.wrapper.net.OkHttpManager

/**
 * Model层实现登录
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
class LoginModel : BaseModel(), LoginContract.LoginModel {
    override fun login(user: User?, observer: DefaultObserver<Login>?) {
        val iLogin = OkHttpManager.createApi(ILoginApi::class.java)
        iLogin.login(user!!.userName, user.password)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer!!)
    }

    override fun getDomain(username: String?, observer: ConvertObserver<Domain>?) {
        val iLogin = OkHttpManager.createApi(ILoginApi::class.java, Key.PROTOCOL + Key.HOST)
        iLogin.getDomain(username)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer!!)
    }
}