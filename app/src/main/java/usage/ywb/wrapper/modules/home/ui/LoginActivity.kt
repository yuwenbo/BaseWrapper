package usage.ywb.wrapper.modules.home.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import usage.ywb.wrapper.R
import usage.ywb.wrapper.modules.home.contract.LoginContract.LoginView
import usage.ywb.wrapper.modules.home.entity.Domain
import usage.ywb.wrapper.modules.home.entity.User
import usage.ywb.wrapper.modules.home.presenter.LoginPresenter
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity
import usage.ywb.wrapper.mvp.presenter.InjectPresenter
import usage.ywb.wrapper.mvp.widgets.TitleLayout

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
class LoginActivity : BaseWrapperActivity(), LoginView<User> {

    var usernameEt: EditText? = null

    @InjectPresenter
    var loginPresenter: LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameEt = findViewById(R.id.login_username_et)
        findViewById<Button>(R.id.login_btn).setOnClickListener {
//            loginPresenter!!.getDomain(usernameEt!!.text.toString())
            loop@ for (i in 1..10) {
                print("i = $i")
                for (j in 1..10) {
                    print("j = $j")
                    if (j == 3) continue@loop
                    print("$i-$j")
                }
            }
        }
    }

    override fun setTitleView(titleLayout: TitleLayout) {
        super.setTitleView(titleLayout)
        titleLayout.setLeftViewText("back")
        titleLayout.setTitleViewText("登录")
    }

    override fun onLeftClick(view: TextView) {
        super.onLeftClick(view)
        finish()
    }

    override fun onLoginSucceed(user: User) {}
    override fun onLoginFailure() {}
    override fun onDomain(domain: Domain?) {}
}