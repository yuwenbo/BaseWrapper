package usage.ywb.wrapper.mvvm.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import usage.ywb.wrapper.mvvm.R;
import usage.ywb.wrapper.mvvm.widgets.TitleLayout;
import usage.ywb.wrapper.utils.StatusBarUtils;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
public abstract class BaseWrapperActivity<VDB extends ViewDataBinding>  extends BaseActivity<VDB> implements TitleLayout.OnTitleClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTranslucentForImageView(this, 0, null);//设置状态栏透明
    }

    protected void setTitleView(TitleLayout titleLayout) {
        titleLayout.setOnTitleClickListener(this);
    }

    @Override
    protected void initContentView() {
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
        super.onDestroy();
    }

}
