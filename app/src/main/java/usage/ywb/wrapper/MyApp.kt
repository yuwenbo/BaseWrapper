package usage.ywb.wrapper

import android.content.Context
import android.os.SystemClock
import android.util.Log
import usage.ywb.wrapper.mvp.BaseApplication

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/05/13 ]
 */
class MyApp : BaseApplication() {
    override fun attachBaseContext(base: Context) {
        //记录冷启动的开始时间点
        //Log.i(TAG, SystemClock.currentThreadTimeMillis().toString())
        super.attachBaseContext(base)
    }

}