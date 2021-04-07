package usage.ywb.wrapper.gamemerge10;

import android.os.Bundle;

import usage.ywb.wrapper.gamemerge10.R;
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;


/**
 * @author yuwenbo
 * @version [ v1.0.0, 2017/3/17 ]
 */
public class Game10Activity extends BaseWrapperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game10);
    }

    @Override
    protected void setTitleView(TitleLayout titleLayout) {
        super.setTitleView(titleLayout);
        titleLayout.setTitleViewText("合10");
    }
}
