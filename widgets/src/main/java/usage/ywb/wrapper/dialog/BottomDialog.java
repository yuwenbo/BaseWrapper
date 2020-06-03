package usage.ywb.wrapper.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import usage.ywb.personal.widgets.R;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class BottomDialog extends BottomSheetDialog {

    private View contentView;

    public BottomDialog(@NonNull Context context) {
        this(context, R.style.BottomDialog);
    }

    public BottomDialog(@NonNull Context context, int theme) {
        super(context, theme);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        } else if (context instanceof ContextWrapper) {
            Activity activity = (Activity) ((ContextWrapper) context).getBaseContext();
            setOwnerActivity(activity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {//4.4 全透明状态栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, null);
    }

    @Override
    public void setContentView(int layoutResId) {
        View view = getLayoutInflater().inflate(layoutResId, null);
        setContentView(view, null);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        final View designBottomSheet = findViewById(R.id.design_bottom_sheet);
        if (designBottomSheet != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(designBottomSheet);
                    behavior.setHideable(false);//此处设置表示禁止BottomSheetBehavior的执行
                }
            });
        }
        contentView = view;
    }

    public View getContentView() {
        return contentView;
    }
}
