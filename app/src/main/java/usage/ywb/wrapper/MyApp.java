package usage.ywb.wrapper;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import usage.ywb.wrapper.mvp.ui.base.activity.HookStartActivityUtil;
import usage.ywb.wrapper.mvp.ui.base.activity.ProxyActivity;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/05/13 ]
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        HookStartActivityUtil hookStartActivityUtil = new HookStartActivityUtil(this, ProxyActivity.class);
        try {
            hookStartActivityUtil.hookStartActivity();
            hookStartActivityUtil.hookLaunchActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //记录冷启动的开始时间点
        Log.i(TAG, String.valueOf(SystemClock.currentThreadTimeMillis()));
    }

}
