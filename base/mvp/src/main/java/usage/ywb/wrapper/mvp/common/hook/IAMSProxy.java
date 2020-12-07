package usage.ywb.wrapper.mvp.common.hook;

import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import usage.ywb.wrapper.mvp.BaseApplication;
import usage.ywb.wrapper.mvp.common.activity.ProxyActivity;

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
        if (method.getName().equals("startActivity")) {//1
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Intent intent = (Intent) args[index];
                Intent subIntent = new Intent();//2
                subIntent.setClassName(BaseApplication.getApplication().getPackageName(), ProxyActivity.class.getName());//3
                subIntent.putExtra(HookHelper.TARGET_INTENT, intent);//4
                args[index] = subIntent;//5
            } else {
                return method.invoke(o, args);
            }
        }
        return method.invoke(mActivityTaskManager, args);
    }


}
