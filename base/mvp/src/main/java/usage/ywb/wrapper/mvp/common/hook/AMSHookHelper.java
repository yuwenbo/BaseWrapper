package usage.ywb.wrapper.mvp.common.hook;

import android.content.ComponentName;
import android.os.Build;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import usage.ywb.wrapper.mvp.BaseApplication;
import usage.ywb.wrapper.mvp.common.activity.ProxyActivity;
import usage.ywb.wrapper.mvp.utils.FieldUtil;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/11/23 ]
 */
public class AMSHookHelper {

    public static final String TARGET_INTENT = "target_intent";

    public static void hookAMS() throws Exception {
        Object defaultSingleton;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Class<?> activityTaskManageClazz = Class.forName("android.app.ActivityTaskManager");
            //获取activityManager中的IActivityManagerSingleton字段
            defaultSingleton = FieldUtil.getField(activityTaskManageClazz, null, "IActivityTaskManagerSingleton");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//1
            Class<?> activityManageClazz = Class.forName("android.app.ActivityManager");
            //获取activityManager中的IActivityManagerSingleton字段
            defaultSingleton = FieldUtil.getField(activityManageClazz, null, "IActivityManagerSingleton");
        } else {
            Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");
            //获取ActivityManagerNative中的gDefault字段
            defaultSingleton = FieldUtil.getField(activityManagerNativeClazz, null, "gDefault");
        }
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        Field mInstanceField = FieldUtil.getField(singletonClazz, "mInstance");//2
        Class<?> iActivityManagerClazz;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            iActivityManagerClazz = Class.forName("android.app.IActivityTaskManager");
            Method getMethod = singletonClazz.getMethod("get");
            getMethod.setAccessible(true);
            getMethod.invoke(defaultSingleton);
        } else {//1
            iActivityManagerClazz = Class.forName("android.app.IActivityManager");
        }
        Object iActivityManager = mInstanceField.get(defaultSingleton);//3

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityManagerClazz}, new IAMSProxy(iActivityManager));
        mInstanceField.set(defaultSingleton, proxy);
    }

    public static void hookHandler() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object currentActivityThread = FieldUtil.getField(activityThreadClass, null, "sCurrentActivityThread");//1
        Field mHField = FieldUtil.getField(activityThreadClass, "mH");//2
        Handler mH = (Handler) mHField.get(currentActivityThread);//3
        FieldUtil.setField(Handler.class, mH, "mCallback", new HCallback(mH));

        Field sPackageManagerFiled = FieldUtil.getField(activityThreadClass, "sPackageManager");//2
        Object sPackageManager = sPackageManagerFiled.get(null);
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                sPackageManager.getClass().getInterfaces(),
                new PackageManagerHandler(sPackageManager));

        sPackageManagerFiled.set(currentActivityThread, proxyInstance);
    }

    /**
     * 解决AppCompatActivity无法hook的问题
     */
    private static class PackageManagerHandler implements InvocationHandler {
        private Object IPackageManager;

        public PackageManagerHandler(Object IPackageManager) {
            this.IPackageManager = IPackageManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getActivityInfo".equals(method.getName())) {
                args[0] = new ComponentName(BaseApplication.getApplication().getPackageName(), ProxyActivity.class.getName());
            }
            return method.invoke(IPackageManager, args);
        }
    }

}

