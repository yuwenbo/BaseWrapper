package usage.ywb.wrapper.mvp.common.hook;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import usage.ywb.wrapper.mvp.utils.FieldUtil;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/11/23 ]
 */
public class HCallback implements Handler.Callback {

    /**
     * API29之前的版本
     */
    public static final int LAUNCH_ACTIVITY = 100;
    /**
     * API29及以后的版本
     */
    public static final int EXECUTE_TRANSACTION = 159;

    private Handler mHandler;

    public HCallback(Handler handler) {
        mHandler = handler;
    }

    @Override
    public boolean handleMessage(@NotNull Message msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (msg.what == EXECUTE_TRANSACTION) {
                restoreIntent28(msg.obj);
            }
        } else {
            if (msg.what == LAUNCH_ACTIVITY) {
                restoreIntent(msg.obj);
            }
        }
        mHandler.handleMessage(msg);
        return true;
    }

    private void restoreIntent28(Object obj) {
        try {
            //获取ClientTransaction中的mActivityCallbacks集合
            Object object = FieldUtil.getField(obj.getClass(), obj, "mActivityCallbacks");
            if (object instanceof List) {
                List list = (List) object;
                if (!list.isEmpty()) {
                    //得到集合中的LaunchActivityItem
                    Object o = list.get(0);
                    //获取LaunchActivityItem中的mIntent
                    Class<?> LaunchActivityItemClazz = Class.forName("android.app.servertransaction.LaunchActivityItem");
                    Intent intent = (Intent) FieldUtil.getField(LaunchActivityItemClazz, o, "mIntent");
                    //得到我们设置的class 替换进去
                    if (intent.getParcelableExtra(HookHelper.TARGET_INTENT) != null) {
                        Intent target = intent.getParcelableExtra(HookHelper.TARGET_INTENT);
                        if (target != null && target.getComponent() != null) {
                            intent.setComponent(target.getComponent());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreIntent(Object obj) {
        try {
            //获取ActivityClientRecord中的intent
            Intent intent = (Intent) FieldUtil.getField(obj.getClass(), obj, "intent");
            Intent target = intent.getParcelableExtra(HookHelper.TARGET_INTENT);
            intent.setComponent(target.getComponent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
