package usage.ywb.wrapper.watchclock;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;


/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/5 ]
 */
public class ClockActivity extends BaseWrapperActivity {

    private ClockPoints clockPoint;
    private ClockSeconds clockSeconds;

    private TextView timeTv;

    private Handler handler;

    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initTimeText();
        initAnimation();
        initTimer();

    }


    private void initAnimation(){
        animator = new ValueAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                clockSeconds.setTime(value);
            }
        });
    }

    private void initTimer(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                clockPoint.setTime(hours, minute, second);
//                clockSeconds.setTime(second);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void initTimeText(){
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Calendar calendar = Calendar.getInstance();
                    String time = format.format(calendar.getTime());
                    timeTv.setText(time);
                    if(animator != null){
                        if(animator.isRunning()){
                            animator.end();
                        }
                        int second = calendar.get(Calendar.SECOND);
                        animator.setFloatValues(second, second + 1);
                        animator.setDuration(1000);
                        animator.start();
                    }
                }
            }
        };
    }

    private void initView(){
        setContentView(R.layout.activity_clock_main);
        clockPoint = (ClockPoints) findViewById(R.id.cp_clock_point);
        clockSeconds = (ClockSeconds) findViewById(R.id.cs_clock_seconds);
        timeTv = (TextView) findViewById(R.id.tv_clock_time);

    }


    @Override
    protected void setTitleView(TitleLayout titleLayout) {
        super.setTitleView(titleLayout);
        titleLayout.setTitleViewText("钟表");
    }
}
