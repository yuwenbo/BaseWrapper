package usage.ywb.wrapper.video.player.ui.activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.player.model.VideoData;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.utils.Constants;
import usage.ywb.wrapper.video.player.ui.view.MarqueeTextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 使用{@link MediaPlayer}实现视频播放
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/12/6 ]
 */
@SuppressLint("ClickableViewAccessibility")
public class MediaActivity extends AppCompatActivity implements View.OnTouchListener,
        View.OnClickListener, View.OnLongClickListener, SurfaceHolder.Callback {

    /**
     *
     */
    private MediaPlayer mediaPlayer;

    /**
     * 主视频视图
     */
    private SurfaceView surfaceView;

    /**
     * 视频标题
     */
    private MarqueeTextView videoTitle;
    private ImageButton videoPlay;
    private ImageButton videoLock;
    /**
     * 当前播放时间
     */
    private TextView videoCurrTime;
    /**
     * 当前播放进度
     */
    private SeekBar videoSeek;
    /**
     * 当前视频总时长
     */
    private TextView videoTotalTime;
    /**
     * 标题栏
     */
    private FrameLayout layoutTitle;
    /**
     * 控制栏
     */
    private LinearLayout layoutControl;
    /**
     * 视屏相关数据
     */
    private VideoEntity videoEntity;
    private int videoIndex = -1;

    private Handler mHandler;
    private int progress;
    private boolean isPlaying = true;
    private boolean isLock = false;

    /**
     * 面板显示时间
     */
    private int showTime = 0;

    /**
     * 面板默认显示时间，过后自动隐藏
     */
    private static final int SHOW_TIME = 5;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        videoIndex = getIntent().getIntExtra(Constants.VIDEO_INDEX, 0);
        initView();
        initAnimation();
        initTimer();
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                videoCurrTime.setText(DateFormat.format("mm:ss", progress));
                videoSeek.setProgress(msg.what);
                if (msg.what < 0) {
                    setPanelGone();
                }
            }
        };
    }

    private Timer timer;

    private void initTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progress = mediaPlayer.getCurrentPosition();
                if (showTime < SHOW_TIME) {
                    showTime++;
                    mHandler.sendEmptyMessage(1);
                } else if (showTime == SHOW_TIME) {
                    showTime++;
                    mHandler.sendEmptyMessage(-1);
                } else {
                    mHandler.sendEmptyMessage(1);
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private TranslateAnimation hideTop, hideButtom;
    private TranslateAnimation showTop, showButtom;

    private void initAnimation() {
        hideTop = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1);
        hideButtom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1);
        showTop = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0);
        showButtom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        showTop.setDuration(500);
        showButtom.setDuration(500);
        hideTop.setDuration(1000);
        hideButtom.setDuration(1000);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Log.d("MediaActivity", "MediaActivity-------initData-------");
        videoEntity = VideoData.getLocalVideosList(this).get(videoIndex);
        videoTitle.setText(videoEntity.getName());
        videoTotalTime.setText(DateFormat.format("mm:ss", videoEntity.getTime()));
        videoSeek.setMax((int) videoEntity.getTime());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(videoEntity.getUri()));
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        mediaPlayer = new MediaPlayer();
        surfaceView = findViewById(R.id.surfaceView);
        videoTitle = findViewById(R.id.video_play_name);
        ImageButton videoCancel = findViewById(R.id.video_cancal);
        videoCurrTime = findViewById(R.id.video_current_time);
        videoSeek = findViewById(R.id.video_seek);
        videoTotalTime = findViewById(R.id.video_total_time);
        layoutTitle = findViewById(R.id.video_top);
        layoutControl = findViewById(R.id.video_buttom);
        ImageButton videoForward = findViewById(R.id.video_forward);
        videoPlay = findViewById(R.id.video_play);
        ImageButton videoNext = findViewById(R.id.video_next);
        videoLock = findViewById(R.id.video_lock);

        surfaceView.setOnTouchListener(this);
        surfaceView.getHolder().addCallback(this);
        videoCancel.setOnClickListener(this);
        videoForward.setOnClickListener(this);
        videoPlay.setOnClickListener(this);
        videoNext.setOnClickListener(this);
        videoLock.setOnClickListener(this);

        videoForward.setOnLongClickListener(this);
        videoNext.setOnLongClickListener(this);

        videoSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                mediaPlayer.seekTo(progress);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                MediaActivity.this.progress = progress;
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showTime = 0;
            if (isLock) {
                videoLock.setVisibility(View.VISIBLE);
            } else {
                if (layoutTitle.getVisibility() == View.GONE) {
                    setPanelVisible();
                } else {
                    showTime = SHOW_TIME + 1;
                    setPanelGone();
                }
            }
        }
        return false;
    }

    /**
     * 设置控制板和标题版隐藏
     */
    private void setPanelGone() {
        if (layoutTitle.getVisibility() == View.VISIBLE) {
            layoutTitle.startAnimation(hideTop);
            layoutControl.startAnimation(hideButtom);
            layoutTitle.setVisibility(View.GONE);
            layoutControl.setVisibility(View.GONE);
        }
        videoLock.setVisibility(View.GONE);
    }

    /**
     * 设置控制板和标题版显示
     */
    private void setPanelVisible() {
        layoutTitle.startAnimation(showTop);
        layoutControl.startAnimation(showButtom);
        videoLock.setVisibility(View.VISIBLE);
        layoutTitle.setVisibility(View.VISIBLE);
        layoutControl.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(final View v) {
        showTime = 0;
        int id = v.getId();
        if (id == R.id.video_lock) {
            if (isLock) {
                videoLock.setImageResource(R.drawable.ic_action_open);
                setPanelVisible();
                isLock = false;
            } else {
                videoLock.setImageResource(R.drawable.ic_action_lock);
                setPanelGone();
                isLock = true;
            }
        } else if (id == R.id.video_cancal) {
            mediaPlayer.stop();
            finish();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } else if (id == R.id.video_forward) {
            if (videoIndex == 0) {
                Toast.makeText(this, "已经是第一条了", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.pause();
                progress = 0;
                videoIndex--;
                initData();
            }
        } else if (id == R.id.video_play) {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                videoPlay.setImageResource(R.drawable.ic_action_play);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                videoPlay.setImageResource(R.drawable.ic_action_stop);
            }
        } else if (id == R.id.video_next) {
            if (videoIndex == VideoData.getLocalVideosList(this).size() - 1) {
                Toast.makeText(this, "已经是最后一条了", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.pause();
                progress = 0;
                videoIndex++;
                initData();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.seekTo(progress);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(final View v) {
        int id = v.getId();
        if (id == R.id.video_forward) {
            final Timer timer1 = new Timer();
            final TimerTask task1 = new TimerTask() {
                int schedule = 1000;

                @Override
                public void run() {
                    progress -= schedule;
                    schedule += 1000;
                    if (progress < 0 || !v.isPressed()) {
                        timer1.cancel();
                    } else {
                        mHandler.sendEmptyMessage(progress);
                        mediaPlayer.seekTo(progress);
                    }
                }
            };
            timer1.schedule(task1, 0, 300);
        } else if (id == R.id.video_next) {
            final Timer timer2 = new Timer();
            final TimerTask task2 = new TimerTask() {
                int schedule = 1000;

                @Override
                public void run() {
                    progress += schedule;
                    schedule += 1000;
                    if (progress > videoEntity.getTime() || !v.isPressed()) {
                        timer2.cancel();
                    } else {
                        mHandler.sendEmptyMessage(progress);
                        mediaPlayer.seekTo(progress);
                    }
                }
            };
            timer2.schedule(task2, 0, 300);
        }
        return false;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initData();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
