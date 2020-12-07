package usage.ywb.wrapper.mvp.common.hook;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;
import usage.ywb.wrapper.mvp.common.activity.ProxyActivity;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/12/1 ]
 */
public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;

    private boolean isIntentFromPlugin = false;

    public InstrumentationProxy(Instrumentation mInstrumentation) {
        this.mInstrumentation = mInstrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        List<ResolveInfo> infoList = who.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        Intent mIntent;
        if (infoList.size() == 0) {
            isIntentFromPlugin = true;
            mIntent = new Intent(who, ProxyActivity.class);
            mIntent.putExtra(HookHelper.TARGET_INTENT, intent);
        } else {
            isIntentFromPlugin = false;
            mIntent = intent;
        }
        try {
            @SuppressLint("DiscouragedPrivateApi")
            Method execMethod = mInstrumentation.getClass().getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            Object object = execMethod.invoke(mInstrumentation, who, contextThread, token,
                    target, mIntent, requestCode, options);
            if (object != null) {
                return (ActivityResult) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (intent != null) {
            Intent targetIntent = intent.getParcelableExtra(HookHelper.TARGET_INTENT);
            if (targetIntent != null && targetIntent.getComponent() != null) {
                ComponentName componentName = targetIntent.getComponent();
//                ClassLoader classLoader = PluginClassLoaderHelper.getHelper().getClassLoader("audio.apk");
                Activity activity = mInstrumentation.newActivity(cl, componentName.getClassName(), targetIntent);
                return activity;
            }
        }
        return mInstrumentation.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        injectActivity(activity);
        mInstrumentation.callActivityOnCreate(activity, icicle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        injectActivity(activity);
        mInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    protected void injectActivity(Activity activity) {

    }


}
