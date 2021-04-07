package usage.ywb.wrapper.mvvm.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class DecimalUtil {


    public static final int DEFAULT_PRECISION = 2;


    public static double doubleScale(double value) {
        return doubleScale(value, DEFAULT_PRECISION);
    }

    /**
     * 指定的double的小数位
     *
     * @param value
     * @param digit 小数位
     * @return
     */
    public static double doubleScale(double value, int digit) {
        if (Double.isNaN(value)) {
            return 0;
        }
        String valueString = doubleScaleString(value, digit);
        try {
            return Double.parseDouble(valueString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //指定的double的小数位(直接截断)
    public static double doubleScaleDown(Double value, int digit) {
        if (value == null) {
            return 0;
        } else {
            return doubleScaleDown(value.doubleValue(), digit);
        }
    }

    //指定的double的小数位(直接截断)
    public static double doubleScaleDown(double value, int digit) {
        if (Double.isNaN(value)) {
            return 0;
        }
        String valueString = doubleScaleStringDown(value, digit);
        try {
            return Double.parseDouble(valueString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //金额添加千分位符（超过两位小数直接截断，默认四舍五入）
    public static String doubleSymbol(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }

    //金额添加千分位符（超过两位小数直接截断，不四舍五入）
    public static String doubleSymbolDown(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static String doubleSymbolDown(Double value) {
        if (value == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static String doubleSymbolDown(Double value, int digit) {
        if (value == null) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < digit; i++) {
            if (i == 0) {
                str.append(".");
            }
            str.append("0");
        }
        DecimalFormat df;
        if (str.length() > 0) {
            df = new DecimalFormat("#,##0" + str);
        } else {
            df = new DecimalFormat("#,##0" + str);
        }
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static String doubleSymbol(Double value, int digit) {
        if (value == null) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < digit; i++) {
            if (i == 0) {
                str.append(".");
            }
            str.append("0");
        }
        DecimalFormat df;
        if (str.length() > 0) {
            df = new DecimalFormat("#,##0" + str);
        } else {
            df = new DecimalFormat("#,##0" + str);
        }
        return df.format(value);
    }


    public static String doubleSymbol1(Double value) {
        if (value == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }

    //浮点型转String，保留两位小数（默认四舍五入）
    public static String doubleScaleString(double value) {
        return doubleScaleString(value, DEFAULT_PRECISION);
    }

    //浮点型转String，保留两位小数(截断显示)
    public static String doubleScaleStringDown(double value) {
        return doubleScaleStringDown(value, DEFAULT_PRECISION);
    }

    public static String doubleScaleStringDown(Double value) {
        return doubleScaleStringDown(value, DEFAULT_PRECISION);
    }

    //浮点型转String，保留两位小数
    public static String doubleScaleString1(Double value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb;

        sb = new StringBuilder("0.");
        for (int i = 0; i < DEFAULT_PRECISION; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(value);
    }

    public static String doubleScaleString(double value, int digit) {
        StringBuilder sb;
        if (digit == 0) {
            sb = new StringBuilder("0");
        } else {
            sb = new StringBuilder("0.");
            for (int i = 0; i < digit; i++) {
                sb.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(value);
    }

    /**
     * double 转String （截断显示）
     *
     * @param value
     * @param digit
     * @return
     */
    public static String doubleScaleStringDown(double value, int digit) {
        StringBuilder sb;
        if (digit == 0) {
            sb = new StringBuilder("0");
        } else {
            sb = new StringBuilder("0.");
            for (int i = 0; i < digit; i++) {
                sb.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static String doubleScaleStringDown(Double value, int digit) {
        if (value == null) {
            return null;
        }
        StringBuilder sb;
        if (digit == 0) {
            sb = new StringBuilder("0");
        } else {
            sb = new StringBuilder("0.");
            for (int i = 0; i < digit; i++) {
                sb.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static String filterPrecision(String valueString, int precision) {
        if (precision == 0 || valueString.length() == 0) {
            return valueString;
        }
        String[] splitArray = valueString.split("\\.");
        if (splitArray.length > 1) {
            String dotValue = splitArray[1];
            if (dotValue != null && dotValue.length() > precision) {
                return splitArray[0] + "." + dotValue.substring(0, dotValue.length() - 1);
            }
        }
        return valueString;
    }

    //四舍五入
    public static int roundHalfUp(double num) {
        BigDecimal b = new BigDecimal(num);
        b = b.setScale(0, BigDecimal.ROUND_HALF_UP);//四舍五入
        return b.intValue();
    }

    /**
     * 提供精確的小數位四捨五入處理。
     * 提供精确的小数位四舍五入处理
     *
     * @param v     需要四捨五入的數位
     * @param scale 小數點後保留幾位
     * @return 四捨五入後的結果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
