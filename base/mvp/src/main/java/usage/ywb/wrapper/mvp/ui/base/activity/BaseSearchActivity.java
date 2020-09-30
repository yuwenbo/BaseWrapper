package usage.ywb.wrapper.mvp.ui.base.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import usage.ywb.wrapper.mvp.R;
import usage.ywb.wrapper.mvp.utils.StatusBarUtil;
import usage.ywb.wrapper.mvp.widgets.SearchLayout;
import usage.ywb.wrapper.mvp.ui.BaseActivity;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/5/14 ]
 */
public class BaseSearchActivity extends BaseActivity implements SearchLayout.OnSearchClickListener {

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);//设置状态栏透明
    }

    protected void setSearchView(SearchLayout searchLayout) {
        searchLayout.setOnSearchClickListener(this);
        searchLayout.setLeftViewText("返回");
        searchLayout.setRightViewText("搜索");
    }


    @Override
    protected void initContentView() {
        unbinder = ButterKnife.bind(this);
        SearchLayout searchLayout = findViewById(R.id.search_layout);
        if (searchLayout != null) {
            searchLayout.post(() -> {
                setSearchView(searchLayout);
            });
        }
    }

    @Override
    public void onLeftClick(TextView view) {
        finish();
    }

    @Override
    public void onSearchClick(TextView view) {

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

}
