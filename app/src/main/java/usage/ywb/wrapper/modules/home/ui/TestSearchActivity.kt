package usage.ywb.wrapper.modules.home.ui

import android.os.Bundle
import android.widget.TextView
import usage.ywb.wrapper.R
import usage.ywb.wrapper.mvp.common.activity.BaseSearchActivity
import usage.ywb.wrapper.mvp.widgets.SearchLayout

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/4/29 ]
 */
class TestSearchActivity : BaseSearchActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_serach)
    }

    override fun setSearchView(searchLayout: SearchLayout) {
        super.setSearchView(searchLayout)
        searchLayout.setSearchHint("请输入搜索内容")
    }

    override fun onSearchClick(view: TextView) {
        super.onSearchClick(view)
    }
}