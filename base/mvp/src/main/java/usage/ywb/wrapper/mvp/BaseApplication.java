package usage.ywb.wrapper.mvp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/29 ]
 */
public class BaseApplication extends Application {

    private static Application application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
        //MultiDexf分包初始化，必须最先初始化
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
    }

    public static Application getApplication() {
        return application;
    }

}
