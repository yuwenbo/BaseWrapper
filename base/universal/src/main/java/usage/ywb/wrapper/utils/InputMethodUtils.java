package usage.ywb.wrapper.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2020/5/14 ]
 */
public class InputMethodUtils {

    /**
     * 如果输入法在窗口上已经显示，则隐藏，反之则显示
     */
    public static void showOrHide(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 调用隐藏系统默认的输入法
     */
    public static void hide(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 强制隐藏键盘
     *
     * @param view 接受软键盘输入的视图
     */
    public static void hide(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    /**
     * 强制显示键盘
     *
     * @param view 接受软键盘输入的视图
     */
    public static void show(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//SHOW_FORCED表示强制显示
        }
    }

    /**
     * @return 输入法打开的状态
     */
    public static boolean isShowing(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.isActive();//isOpen若返回true，则表示输入法打开
    }

    /**
     * @return 输入法打开的状态
     */
    private boolean isInputMethodShowing(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        //获取当前屏幕内容的高度
        int screenHeight = decorView.getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0;
    }

    /**
     * @return 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
