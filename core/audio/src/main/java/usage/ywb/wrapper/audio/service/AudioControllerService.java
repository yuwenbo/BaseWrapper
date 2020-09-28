package usage.ywb.wrapper.audio.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * @author frank.yu
 *
 * DATE:2015.05.28
 */
public class AudioControllerService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("AudioService", getApplicationInfo().processName);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new AudioController(this).asBinder();
    }


}
