package usage.ywb.wrapper.pluggable.proxy;

import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import usage.ywb.wrapper.pluggable.core.PluginHelper;

/**
 * @author yuwenbo
 * @version [ V.2.9.1  2020/8/12 ]
 */
public class IAMSProxy implements InvocationHandler {

    private Object mActivityTaskManager;

    public IAMSProxy(Object activityTaskManager) {
        this.mActivityTaskManager = activityTaskManager;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Intent intent = (Intent) args[index];
                Intent subIntent = new Intent();
                subIntent.setClassName(PluginHelper.getInstance().getHostBaseContext().getPackageName(), ProxyActivity.class.getName());//3
                subIntent.putExtra(PluginHelper.TARGET_INTENT, intent);
                args[index] = subIntent;
            } else {
                return method.invoke(o, args);
            }
        }
        return method.invoke(mActivityTaskManager, args);
    }


}
