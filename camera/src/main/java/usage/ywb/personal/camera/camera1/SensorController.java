package usage.ywb.personal.camera.camera1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/8/8 ]
 */
public class SensorController implements SensorEventListener {

    private static final String TAG = SensorController.class.getSimpleName();
    private static final int DELEY_DURATION = 500;
    private static final int STATUS_NONE = 0;
    private static final int STATUS_STATIC = 1;
    private static final int STATUS_MOVE = 2;

    private int STATUE = STATUS_NONE;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int mX, mY, mZ;
    private long lastStaticStamp = 0;

    private boolean focusable = false;  //是否可以发生对焦

    private OnFocusableListener onFocusableListener;

    public SensorController(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        if (mSensorManager == null) {
            throw new IllegalStateException("不支持传感器");
        } else {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public void onStart() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    public void setOnFocusableListener(OnFocusableListener onFocusableListener) {
        this.onFocusableListener = onFocusableListener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            long stamp = Calendar.getInstance().getTimeInMillis();
            if (STATUE != STATUS_NONE) {
                int px = Math.abs(mX - x);
                int py = Math.abs(mY - y);
                int pz = Math.abs(mZ - z);
                double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.4) {
                    STATUE = STATUS_MOVE;
                } else {
                    //上一次状态是move，记录静态时间点
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp;
                        focusable = true;
                    }
                    if (focusable) {
                        if (stamp - lastStaticStamp > DELEY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            focusable = false;
                            if (onFocusableListener != null) {
                                onFocusableListener.onFocusable();
                            }
                        }
                    }
                    STATUE = STATUS_STATIC;
                }
            } else {
                lastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }


    /**
     * 是否需要重新请求对焦的监听
     */
    public interface OnFocusableListener {
        /**
         * 可以对焦
         */
        void onFocusable();
    }


}
