package usage.ywb.wrapper;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import usage.ywb.wrapper.mvp.BaseApplication;
import usage.ywb.wrapper.pluggable.core.PluginHelper;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/05/13 ]
 */
public class MyApp extends BaseApplication {

    private static final String TAG = "MyApp";


    @Override
    protected void attachBaseContext(Context base) {
        //记录冷启动的开始时间点
        Log.i(TAG, String.valueOf(SystemClock.currentThreadTimeMillis()));
        super.attachBaseContext(base);
        /*----------------------------------------------------------------------------------------*/
        PluginHelper.getInstance().init(base);
    }


}
