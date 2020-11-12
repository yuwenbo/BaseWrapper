package usage.ywb.wrapper.player;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.OnClick;
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;

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
        ARouter.getInstance().build("/audio/MainActivity").navigation();
    }

    @OnClick(R.id.video_btn)
    protected void onClickVideo() {
        ARouter.getInstance().build("/video/MainActivity").navigation();
    }

    @OnClick(R.id.camera_btn)
    protected void onClickCamera() {
        ARouter.getInstance().build("/camera/MainActivity").navigation();
    }

}
