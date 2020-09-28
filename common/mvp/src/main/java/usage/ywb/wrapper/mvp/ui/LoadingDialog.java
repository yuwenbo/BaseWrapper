package usage.ywb.wrapper.mvp.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import usage.ywb.wrapper.mvp.R;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/14 ]
 */
public class LoadingDialog extends Dialog {

    private CharSequence message;//内容

    private TextView contentTv;


    public LoadingDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        contentTv = findViewById(R.id.content_tv);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (message != null) {
            contentTv.setText(message);
        }
    }
}
