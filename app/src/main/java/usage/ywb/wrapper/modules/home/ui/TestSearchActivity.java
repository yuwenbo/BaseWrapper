package usage.ywb.wrapper.modules.home.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import usage.ywb.wrapper.R;
import usage.ywb.wrapper.base.activity.BaseSearchActivity;
import usage.ywb.wrapper.commom.widgets.SearchLayout;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
public class TestSearchActivity extends BaseSearchActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_serach);
    }

    @Override
    protected void setSearchView(SearchLayout searchLayout) {
        super.setSearchView(searchLayout);
        searchLayout.setSearchHint("请输入搜索内容");
    }

    @Override
    public void onSearchClick(TextView view) {
        super.onSearchClick(view);
        LinkedHashMap linkedHashMap;
        HashMap hashMap;
    }


}
