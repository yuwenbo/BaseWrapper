package usage.ywb.wrapper.mvp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限管理
 *
 * @author ywb
 * @version [ V.2.1.9  2018/6/27 ]
 */
public class PermissionUtils {

    /**
     * 权限请求回调接口
     */
    public interface PermissionCallbacks {
        /**
         * 授权通过
         *
         * @param requestCode 请求码
         * @param perms       已获得授权的权限
         */
        void onPermissionsGranted(int requestCode, String[] perms);

        /**
         * 拒绝授权
         *
         * @param requestCode 请求码
         * @param perms       没有获取的权限
         */
        void onPermissionsDenied(int requestCode, String[] perms);
    }

    /**
     * 是否已经拥有权限
     *
     * @param context 上下文
     * @param perms   要申请的权限数组
     * @return 当所验证的全部权限都已经授权的时候返回true，否则返回false
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     *
     * @param activity    DealedCustomerSearchActivity
     * @param requestCode 请求码
     * @param perms       申请的权限
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String... perms) {
        if (hasPermissions(activity, perms)) {
            if (activity instanceof PermissionCallbacks) {
                ((PermissionCallbacks) activity).onPermissionsGranted(requestCode, perms);
            }
        } else {
            ActivityCompat.requestPermissions(activity, perms, requestCode);
        }
    }

    /**
     * 请求权限
     *
     * @param fragment    fragment
     * @param requestCode 请求码
     * @param perms       申请的权限
     */
    public static void requestPermissions(@NonNull Fragment fragment, int requestCode, @NonNull String... perms) {
        Activity activity = fragment.getActivity();
        if(activity == null){
            Log.e("PermissionUtils", "Activity 已经和 Fragment解除绑定，无法申请权限");
            return;
        }
        if (hasPermissions(activity, perms)) {
            if (fragment instanceof PermissionCallbacks) {
                ((PermissionCallbacks) fragment).onPermissionsGranted(requestCode, perms);
            }
        } else {
            fragment.requestPermissions(perms, requestCode);
        }
    }

    /**
     * 权限回调处理
     *
     * @param requestCode  请求码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     * @param callback     权限回调
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull PermissionCallbacks callback) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (granted.size() == permissions.length) {
            callback.onPermissionsGranted(requestCode, granted.toArray(new String[granted.size()]));
        }
        if (!denied.isEmpty()) {
            callback.onPermissionsDenied(requestCode, denied.toArray(new String[denied.size()]));
        }
    }

}
