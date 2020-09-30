package usage.ywb.wrapper.mvp.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期转换类
 *
 * @author yuwenbo
 * @version [ V.2.1.9  2018/6/26 ]
 */
public class DateFormat {

    /*日期格式*/
    public final static String FORMAT_FILL = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_NORMAL = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DEFAULT = "yyyy-MM-dd";
    public final static String FORMAT_COMPACT = "yyyyMMdd";
    public final static String FORMAT_MONTH_FILL = "MM-dd HH:mm";
    public final static String FORMAT_MINUTE_SECOND = "HH:mm";

    public static String dateToString(long dateLong, String format) {
        Date date = new Date();
        date.setTime(dateLong);
        return dateToString(date, format);
    }

    public static String dateToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat time = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return time.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String转Date
     *
     * @param dataString 时间字符串
     * @param format     时间字符串的格式
     * @return
     */
    public static Date stringToDate(String dataString, String format) {
        if (dataString == null || dataString.length() == 0) {
            return null;
        }
        SimpleDateFormat time = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return time.parse(dataString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取上一个月时间
     *
     * @param format
     * @return
     */
    public static String getLastMonthDateToString(String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 把一种格式的日期转为其他日期格式
     *
     * @param dataString
     * @param oldFormat
     * @param newFormat
     * @return
     */
    public static String stringToString(String dataString, String oldFormat, String newFormat) {
        Date date = stringToDate(dataString, oldFormat);
        String result = dateToString(date, newFormat);
        return TextUtils.isEmpty(result) ? dataString : result;
    }

    /**
     * 把年月日时分秒的日期转为其他日期格式
     */
    public static String stringToString(String dataString, String format) {
        return stringToString(dataString, FORMAT_FILL,format);
    }

    public static String getBeginData() {
        Calendar beginDate = Calendar.getInstance();
        beginDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), beginDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return dateToString(beginDate.getTime(), FORMAT_FILL);
    }

    public static String getEndData() {
        Calendar beginDate = Calendar.getInstance();
        beginDate.set(beginDate.get(Calendar.YEAR), beginDate.get(Calendar.MONTH), beginDate.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return dateToString(beginDate.getTime(), FORMAT_FILL);
    }

    //设置日期显示样式
    public static String setDataStyle(long data) {
        if (data <= 0) {
            return null;
        }
        Calendar curCalendar = Calendar.getInstance();//当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data);
        String format;
        if (curCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {//不是今年
            format = DateFormat.FORMAT_NORMAL;
            return dateToString(calendar.getTime(), format);
        } else if ((curCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH) || curCalendar.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)
                && curCalendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) > 1)) {//不是昨天
            format = DateFormat.FORMAT_MONTH_FILL;
        } else if ((curCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && curCalendar.get(Calendar.DAY_OF_MONTH) ==
                calendar.get(Calendar.DAY_OF_MONTH))) {//今天
            format = DateFormat.FORMAT_MINUTE_SECOND;
        } else {//昨天
            return "昨天 " + dateToString(calendar.getTime(), DateFormat.FORMAT_MINUTE_SECOND);
        }
        return dateToString(calendar.getTime(), format);
    }

    /**
     * 获取当前的时间 单位：年
     *
     * @param date 过去的时间
     * @return
     */
    public static int getfromCurrentYear(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0;
        }
        long curDate = Calendar.getInstance().getTimeInMillis();
        try {
            long date1 = stringToDate(date, FORMAT_FILL).getTime();
            return (int) ((curDate - date1) / (1000 * 60 * 60 * 24) / 365);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 是不是今年
     *
     * @return
     */
    public static boolean isCurrentYear(String date, String format) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }
        Calendar curCalendar = Calendar.getInstance();//当前日期
        Calendar calendar = Calendar.getInstance();
        Date oldDate = stringToDate(date, format);
        if (oldDate == null) {
            return false;
        }
        calendar.setTime(oldDate);
        return curCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }


    public static long compareByDay(String dateString) {
        return compareByDay(stringToDate(dateString, FORMAT_FILL), Calendar.getInstance().getTime());
    }

    /**
     * 按天比较两个日期字符串的大小，并返回天的差值
     *
     * @param dateString1 被减数
     * @param dateString2 减数
     * @param format      日期格式
     * @return 差值
     */
    public static long compareByDay(@NonNull String dateString1, @NonNull String dateString2, @NonNull String format) {
        return compareByDay(stringToDate(dateString1, format), stringToDate(dateString2, format));
    }

    /**
     * 按天比较两个日期字符串的大小，并返回天的差值
     *
     * @param date1 被减数
     * @param date2 减数
     * @return 差值
     */
    public static long compareByDay(Date date1, Date date2) {
        long l1 = date1 == null ? 0 : date1.getTime();
        long l2 = date2 == null ? 0 : date2.getTime();
        return (l1 - l2) / 1000 / 60 / 60 / 24;
    }

    /**
     * 按天比较两个日期字符串的大小，并返回差值
     *
     * @param dateString1 被减数
     * @param dateString2 减数
     * @param format      时间格式
     * @return 差值
     */
    public static long compare(@NonNull String dateString1, @NonNull String dateString2, String format) {
        Date date1 = stringToDate(dateString1, format);
        Date date2 = stringToDate(dateString2, format);
        long l1 = date1 == null ? 0 : date1.getTime();
        long l2 = date2 == null ? 0 : date2.getTime();
        return l1 - l2;
    }

    //(时间格式：yyyy-MM-dd HH:mm:ss)
    public static long compare(@NonNull String dateString1, @NonNull String dateString2) {
        return compare(dateString1, dateString2, DateFormat.FORMAT_FILL);
    }

    /**
     *把秒数转为时分秒格式
     * @param phoneTime 秒数
     */
    public static String parseDate(int phoneTime) {
        if (phoneTime > 0) {
            StringBuilder builder = new StringBuilder();
            int h = phoneTime / 60 / 60;
            int m = phoneTime / 60 % 60;
            int s = phoneTime % 60;
            if (h > 0) {
                builder.append(h).append("小时");
                builder.append( m ).append("分");
            }else if (m>0){
                builder.append( m ).append("分");
            }
            builder.append(s).append("秒");
            return builder.toString();
        } else {
            return null;
        }
    }
}
