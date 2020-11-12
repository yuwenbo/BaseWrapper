package usage.ywb.wrapper.mvp.common.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yuwenbo
 * @version [ V.2.9.1  2020/8/12 ]
 */
public class HookStartActivityUtil {

    /**
     * Description:
     * <p>
     * 对于 源码中的某个类 如果现实 {hide}，就表示只能系统去new ，如果自己我们想要创建对象，只能通过下边方式获取
     * Class<?> amnClass = Class.forName("android.app.ActivityManagerNative") ; forName("该类最上边的包名 + 类名")
     */


    private Context mContext;
    private Class<?> mProxyClass;
    private final String EXTER_ORIGIN_INTENT = "EXTER_ORIGIN_INTENT";

    public HookStartActivityUtil(Context context, Class<?> proxyClass) {
        this.mContext = context.getApplicationContext();  // 防止内存泄露
        this.mProxyClass = proxyClass;
    }


    public void hookLaunchActivity() throws Exception {
        // 1：获取ActivityThread的实例；
        @SuppressLint("PrivateApi") Class<?> atClass = Class.forName("android.app.ActivityThread");
        // 获取ActivityThread中的属性
        Field scatThread = atClass.getDeclaredField("sCurrentActivityThread");
        scatThread.setAccessible(true);
        Object sCurrentActivityThread = scatThread.get(null);  // 静态的可以传递 null

        // 2：获取ActivityThread中的mH；
        Field mHField = atClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Object mHandler = mHField.get(sCurrentActivityThread);

        // 3：hook  使用 handleLaunchActivity
        // 给handler设置 CallBack回调，也通过反射
        Class<?> handlerClass = Class.forName("android.os.Handler");
        Field mCallBackField = handlerClass.getDeclaredField("mCallback");
        mCallBackField.setAccessible(true);

        mCallBackField.set(mHandler, new HandlerCallBack());

    }


    private class HandlerCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            // 每发一次消息，都会执行一次这个CallBack方法
            if (msg.what == 100) {  // 根据Handler源码可知
                handleLaunchMessage(msg);
            }
            return false;
        }
    }


    /**
     * 开始启动创建Activity拦截
     */
    private void handleLaunchMessage(Message msg) {

        try {
            Object record = msg.obj;
            // 1. 从ActivityClientRecord中获取过安检的 intent
            Field intentField = record.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent safeIntent = (Intent) intentField.get(record);

            // 2. 获取到过安检的intent之后 ，从safeIntent中获取原来的 originIntent
            assert safeIntent != null;
            Intent originIntent = safeIntent.getParcelableExtra(EXTER_ORIGIN_INTENT);
            // 3. 重新设置回去
            if (originIntent != null) {
                intentField.set(record, originIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("PrivateApi")
    public void hookStartActivity() throws Exception {

        // 1>：获取 ActivityManagerNative里面的 gDefault；
        Class<?> amnClass = Class.forName("android.app.ActivityManager");
        // 通过 ActivityManagerNative 类 获取 gDefault属性
        Field gDefaultField = amnClass.getDeclaredField("getService");
        gDefaultField.setAccessible(true);  // 设置权限
        Object gDefault = gDefaultField.get(null);

        // 2>：获取gDefault中的 mInstance属性；
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        Object iamInstance = mInstanceField.get(gDefault);


        Class<?> iamClass = Class.forName("android.app.IActivityTaskManager");
        iamInstance = Proxy.newProxyInstance(HookStartActivityUtil.class.getClassLoader(),
                new Class[]{iamClass},
                // InvocationHandler：必须有一个执行者，就是谁去执行这个方法
                new StartActivityInvocationHandler(iamInstance));

        // 3>：重新指定
        mInstanceField.set(gDefault, iamInstance);
    }


    private class StartActivityInvocationHandler implements InvocationHandler {

        // 这个才是方法的执行者
        private Object mObject;

        // 通过构造方法把mObject传递进来
        public StartActivityInvocationHandler(Object object) {
            this.mObject = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // 在这里可以 hook到 IActivityManager中所有的方法
            Log.e("TAG", method.getName());

            // 替换intent ，过AndroidManifest.xml 检测
            if (method.getName().equals("startActivity")) {
                // 1. 首先获取原来的intent
                Intent originIntent = (Intent) args[2];
                // 2. 创建一个安全的intent
                Intent safeIntent = new Intent(mContext, mProxyClass);
                // 3. 替换第二个参数
                args[2] = safeIntent;
                // 4. 绑定原来的Intent
                safeIntent.putExtra(EXTER_ORIGIN_INTENT, originIntent);

            }

            return method.invoke(mObject, args);
        }
    }

}
