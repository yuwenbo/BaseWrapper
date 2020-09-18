package usage.ywb.wrapper.modules.home.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import usage.ywb.wrapper.IMyAidlInterface;


/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/14 ]
 */
public class AidlTest extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IMyAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

            }
        };
    }
}
