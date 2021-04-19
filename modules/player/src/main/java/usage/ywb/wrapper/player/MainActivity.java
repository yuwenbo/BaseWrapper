package usage.ywb.wrapper.player;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.concurrent.ThreadPoolExecutor;

import butterknife.OnClick;
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/29 ]
 */
public class MainActivity extends BaseWrapperActivity {


    ThreadPoolExecutor e;


    class RR implements Runnable{

        @Override
        public void run() {

        }
    }

    RR rr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Thread thread = new Thread(new RR());
        thread.start();
        thread.run();
        e.execute(rr);
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
