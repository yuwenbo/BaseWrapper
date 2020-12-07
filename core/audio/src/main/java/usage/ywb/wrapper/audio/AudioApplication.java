package usage.ywb.wrapper.audio;

import android.content.Context;

import com.didi.virtualapk.PluginManager;

import usage.ywb.wrapper.mvp.BaseApplication;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/11/30 ]
 */
public class AudioApplication extends BaseApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //美团virtualapk
        PluginManager.getInstance(base).init();
    }
}
