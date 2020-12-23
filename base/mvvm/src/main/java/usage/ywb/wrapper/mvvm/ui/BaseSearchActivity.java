package usage.ywb.wrapper.mvvm.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import usage.ywb.wrapper.mvvm.R;
import usage.ywb.wrapper.mvvm.utils.StatusBarUtil;
import usage.ywb.wrapper.mvvm.widgets.SearchLayout;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/5/14 ]
 */
public abstract class BaseSearchActivity<VDB extends ViewDataBinding> extends BaseActivity<VDB> implements SearchLayout.OnSearchClickListener {


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
        super.onDestroy();
    }

}
