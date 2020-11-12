package usage.ywb.wrapper.audio.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import usage.ywb.wrapper.mvp.utils.LogUtils;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/21 ]
 */
public class AudioReceiver extends BroadcastReceiver {

    public static final String ACTION_CURRENT_POSITION = "usage.ywb.wrapper.audio.ACTION_CURRENT_POSITION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_CURRENT_POSITION.equals(intent.getAction())) {
            int position = intent.getIntExtra("CurrentPosition", 0);
            LogUtils.i(ACTION_CURRENT_POSITION, position + "");
        }
    }

}
