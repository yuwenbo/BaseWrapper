package usage.ywb.wrapper.player;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import butterknife.OnClick;
import usage.ywb.wrapper.mvp.ui.base.activity.BaseWrapperActivity;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/29 ]
 */
public class MainActivity extends BaseWrapperActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }

    @OnClick(R.id.audio_btn)
    protected void onClickAudio() {
        Intent intent = new Intent(this, usage.ywb.wrapper.audio.ui.activity.MainActivity.class);
        startActivity(intent);
    }

}
