package usage.ywb.wrapper.modules.home.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import usage.ywb.wrapper.R;
import usage.ywb.wrapper.mvp.ui.base.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;
import usage.ywb.wrapper.modules.home.contract.LoginContract;
import usage.ywb.wrapper.modules.home.entity.Domain;
import usage.ywb.wrapper.modules.home.entity.User;
import usage.ywb.wrapper.modules.home.presenter.LoginPresenter;
import usage.ywb.wrapper.mvp.presenter.InjectPresenter;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
public class LoginActivity extends BaseWrapperActivity implements LoginContract.LoginView<User> {


    @BindView(R.id.login_username_et)
    EditText usernameEt;

    @InjectPresenter
    LoginPresenter loginPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void setTitleView(TitleLayout titleLayout) {
        super.setTitleView(titleLayout);
        titleLayout.setLeftViewText("back");
        titleLayout.setTitleViewText("登录");
    }

    @Override
    public void onLeftClick(TextView view) {
        super.onLeftClick(view);
        finish();
    }

    @OnClick(R.id.login_btn)
    protected void onClickLogin(){
        loginPresenter.getDomain(usernameEt.getText().toString());
    }

    @Override
    public void onLoginSucceed(User user) {

    }

    @Override
    public void onLoginFailure() {

    }

    @Override
    public void onDomain(Domain domain) {

    }
}
