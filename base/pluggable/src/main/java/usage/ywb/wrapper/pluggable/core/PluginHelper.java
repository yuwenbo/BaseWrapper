package usage.ywb.wrapper.pluggable.core;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import usage.ywb.wrapper.pluggable.proxy.HCallback;
import usage.ywb.wrapper.pluggable.proxy.IAMSProxy;
import usage.ywb.wrapper.pluggable.proxy.InstrumentationProxy;
import usage.ywb.wrapper.pluggable.proxy.ProxyActivity;
import usage.ywb.wrapper.pluggable.utils.FieldUtil;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/11/23 ]
 */
public class PluginHelper {

    public static final String TARGET_INTENT = "target_intent";

    private volatile static PluginHelper hookHelper;

    private Context mHostBaseContext;

    private final Map<String, LoadedPlugin> mLoadedPluginMap;

    public static PluginHelper getInstance() {
        if (hookHelper == null) {
            synchronized (PluginHelper.class) {
                if (hookHelper == null) {
                    hookHelper = new PluginHelper();
                }
            }
        }
        return hookHelper;
    }

    public PluginHelper() {
        mLoadedPluginMap = new HashMap<>();
    }

    public void init(Context base) {
        this.mHostBaseContext = base;
        try {
            hookInstrumentation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getHostBaseContext() {
        return mHostBaseContext;
    }

    public LoadedPlugin loadPlugin(File file) {
        if (mLoadedPluginMap.containsKey(file.getPath())) {
            return mLoadedPluginMap.get(file.getPath());
        } else {
            return new LoadedPlugin(mHostBaseContext, file);
        }
    }

    public LoadedPlugin loadPlugin(String path) {
        if (mLoadedPluginMap.containsKey(path)) {
            return mLoadedPluginMap.get(path);
        } else {
            return new LoadedPlugin(mHostBaseContext, new File(path));
        }
    }

    private void hookInstrumentation() throws Exception {
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        Object mMainThread = FieldUtil.getField(contextImplClass, mHostBaseContext, "mMainThread");
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Instrumentation mInstrumentation = (Instrumentation) FieldUtil.getField(activityThreadClass, mMainThread, "mInstrumentation");
        FieldUtil.setField(activityThreadClass, mMainThread, "mInstrumentation", new InstrumentationProxy(mInstrumentation));
    }

    @Deprecated
    private void hookAMS() throws Exception {
        Object singleton;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Class<?> activityTaskManageClazz = Class.forName("android.app.ActivityTaskManager");
            //获取activityManager中的IActivityManagerSingleton字段
            singleton = FieldUtil.getField(activityTaskManageClazz, null, "IActivityTaskManagerSingleton");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Class<?> activityManageClazz = Class.forName("android.app.ActivityManager");
            //获取activityManager中的IActivityManagerSingleton字段
            singleton = FieldUtil.getField(activityManageClazz, null, "IActivityManagerSingleton");
        } else {
            Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");
            //获取ActivityManagerNative中的gDefault字段
            singleton = FieldUtil.getField(activityManagerNativeClazz, null, "gDefault");
        }
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        Field mInstanceField = FieldUtil.getField(singletonClazz, "mInstance");
        Class<?> iActivityTaskManagerClazz;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            iActivityTaskManagerClazz = Class.forName("android.app.IActivityTaskManager");
            Method getMethod = singletonClazz.getMethod("get");
            getMethod.setAccessible(true);
            getMethod.invoke(singleton);
        } else {
            iActivityTaskManagerClazz = Class.forName("android.app.IActivityManager");
        }
        Object iActivityTaskManager = mInstanceField.get(singleton);

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityTaskManagerClazz}, new IAMSProxy(iActivityTaskManager));
        mInstanceField.set(singleton, proxy);
    }

    @Deprecated
    private void hookHandler() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object currentActivityThread = FieldUtil.getField(activityThreadClass, null, "sCurrentActivityThread");
        Handler mH = (Handler) FieldUtil.getField(activityThreadClass, currentActivityThread, "mH");
        FieldUtil.setField(Handler.class, mH, "mCallback", new HCallback(mH));

        Field sPackageManagerFiled = FieldUtil.getField(activityThreadClass, "sPackageManager");
        Object sPackageManager = sPackageManagerFiled.get(null);
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                sPackageManager.getClass().getInterfaces(),
                new PackageManagerHandler(sPackageManager));

        sPackageManagerFiled.set(currentActivityThread, proxyInstance);
    }

    @Deprecated
    private void hookPackageManager() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object currentActivityThread = FieldUtil.getField(activityThreadClass, null, "sCurrentActivityThread");
        Field sPackageManagerFiled = FieldUtil.getField(activityThreadClass, "sPackageManager");
        Object sPackageManager = sPackageManagerFiled.get(null);
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                sPackageManager.getClass().getInterfaces(),
                new PackageManagerHandler(sPackageManager));
        sPackageManagerFiled.set(currentActivityThread, proxyInstance);
    }


    public void replaceClassLoader(Context context, DexClassLoader loader) {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Class clazz_LApk = Class.forName("android.app.LoadedApk");
            Object currentActivityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field field1 = activityThreadClass.getDeclaredField("mPackages");
            field1.setAccessible(true);
            Map mPackages = (Map) field1.get(currentActivityThread);
            String packageName = context.getPackageName();
            WeakReference ref = (WeakReference) mPackages.get(packageName);
            Field field2 = clazz_LApk.getDeclaredField("mClassLoader");
            field2.setAccessible(true);
            field2.set(ref.get(), loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解决AppCompatActivity无法hook的问题
     */
    @Deprecated
    private class PackageManagerHandler implements InvocationHandler {
        private Object IPackageManager;

        public PackageManagerHandler(Object IPackageManager) {
            this.IPackageManager = IPackageManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getActivityInfo".equals(method.getName())) {
                args[0] = new ComponentName(mHostBaseContext.getPackageName(), ProxyActivity.class.getName());
            }
            return method.invoke(IPackageManager, args);
        }
    }


}

