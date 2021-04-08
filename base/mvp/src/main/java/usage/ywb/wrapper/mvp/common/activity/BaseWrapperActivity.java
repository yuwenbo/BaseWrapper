package usage.ywb.wrapper.mvp.common.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;
import usage.ywb.wrapper.mvp.R;
import usage.ywb.wrapper.mvp.ui.BaseActivity;
import usage.ywb.wrapper.utils.StatusBarUtil;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
public class BaseWrapperActivity extends BaseActivity implements TitleLayout.OnTitleClickListener {

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);//设置状态栏透明
    }

    protected void setTitleView(TitleLayout titleLayout) {
        titleLayout.setOnTitleClickListener(this);
    }

    @Override
    protected void initContentView() {
        unbinder = ButterKnife.bind(this);
        TitleLayout titleLayout = findViewById(R.id.title_layout);
        if (titleLayout != null) {
            titleLayout.post(() -> {
                setTitleView(titleLayout);
            });
        }
    }

    @Override
    public void onLeftClick(TextView view) {
        finish();
    }

    @Override
    public void onTitleClick(TextView view) {

    }

    @Override
    public void onRightClick(TextView view, int index) {

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

}
