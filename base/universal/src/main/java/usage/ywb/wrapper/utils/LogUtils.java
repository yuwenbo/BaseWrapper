package usage.ywb.wrapper.utils;

import android.util.Log;

import androidx.multidex.BuildConfig;


/**
 * @author ywb
 * @version [ V.1.0.0  2020/7/27 ]
 */
public class LogUtils {

    private static String TAG = BuildConfig.APPLICATION_ID;
    private static int LEVEL = Log.VERBOSE;

    private static boolean LOGGABLE = Log.isLoggable(TAG, Log.INFO);

    /**
     * 设置日志Tag
     *
     * @param tag
     */
    public static void setTag(String tag) {
        TAG = tag;
        LOGGABLE = Log.isLoggable(TAG, Log.VERBOSE);
    }

    /**
     * 设置日志显示级别
     *
     * @param level
     */
    public static void setLevel(int level) {
        LEVEL = level;
    }

    /**
     *
     * @param log
     */
    public static void v(String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.VERBOSE /*&& BuildConfig.LOG_OPEN*/ && LOGGABLE) {
            Log.v(TAG, log);
        }
    }
    public static void v(String tag, String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.VERBOSE /*&& BuildConfig.LOG_OPEN*/) {
            Log.v(tag, log);
        }
    }

    /**
     *
     * @param log
     */
    public static void i(String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.INFO /*&& BuildConfig.LOG_OPEN*/ && LOGGABLE) {
            Log.i(TAG, log);
        }
    }
    public static void i(String tag, String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.INFO /*&& BuildConfig.LOG_OPEN*/) {
            Log.i(tag, log);
        }
    }

    /**
     *
     * @param log
     */
    public static void d(String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.DEBUG /*&& BuildConfig.LOG_OPEN*/ && LOGGABLE) {
            Log.d(TAG, log);
        }
    }
    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.DEBUG /*&& BuildConfig.LOG_OPEN*/) {
            Log.d(tag, log);
        }
    }

    /**
     *
     * @param log
     */
    public static void w(String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.WARN /*&& BuildConfig.LOG_OPEN*/ && LOGGABLE) {
            Log.w(TAG, log);
        }
    }
    public static void w(String tag, String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.WARN /*&& BuildConfig.LOG_OPEN*/) {
            Log.w(tag, log);
        }
    }

    /**
     *
     * @param log
     */
    public static void e(String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.ERROR /*&& BuildConfig.LOG_OPEN*/ && LOGGABLE) {
            Log.e(TAG, log);
        }
    }
    public static void e(String tag, String log) {
        if (BuildConfig.DEBUG && LEVEL <= Log.ERROR /*&& BuildConfig.LOG_OPEN*/) {
            Log.e(tag, log);
        }
    }


}
