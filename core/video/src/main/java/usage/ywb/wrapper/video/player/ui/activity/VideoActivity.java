package usage.ywb.wrapper.video.player.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.player.model.VideoData;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.utils.Constants;
import usage.ywb.wrapper.video.player.ui.view.MarqueeTextView;

/**
 * 使用{@link VideoView}实现视频播放
 *
 * @author ywb
 */
@SuppressLint("ClickableViewAccessibility")
public class VideoActivity extends AppCompatActivity implements OnTouchListener,
        OnClickListener, OnLongClickListener {

    /**
     * 主视频视图
     */
    private VideoView videoView;
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
    /**
     * 当前播放进度
     */
    private int progress;
    private boolean isPlaying = true;
    private boolean isLock = false;
    private boolean isFast = false;

    /**
     * 面板显示时间，过后自动隐藏
     */
    private int panelShowingTime = 0;

    /**
     * 快速播放的增量
     */
    private long increment = 1000;

    /**
     * 面板默认显示时间，过后自动隐藏
     */
    private static final int SHOW_TIME = 5;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity_video);
        videoIndex = getIntent().getIntExtra(Constants.VIDEO_INDEX, 0);
        initView();
        initAnimation();
        initData();
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what < 0) {
                    setPanelGone();
                } else {
                    videoCurrTime.setText(DateFormat.format("mm:ss", progress));
                    videoSeek.setProgress(progress);
                    if (msg.what > 0) {
                        videoView.seekTo(progress);
                    }
                }
            }
        };
        initTimer();
    }

    /**
     * 播放时间计时器
     */
    private Timer timer;

    private void initTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isFast) {
                    Log.i("VideoActivity", "panelShowingTime : " + panelShowingTime + " S");
                    progress = videoView.getCurrentPosition();
                    if (panelShowingTime >= SHOW_TIME) {
                        mHandler.sendEmptyMessage(-1);
                    } else {
                        panelShowingTime++;
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 播放面板显示与隐藏动画
     */
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
        Log.d("VideoActivity", "VideoActivity-------initData-------");
        videoEntity = VideoData.getLocalVideosList(this).get(videoIndex);
        videoTitle.setText(videoEntity.getName());
        videoTotalTime.setText(DateFormat.format("mm:ss", videoEntity.getTime()));
        videoSeek.setMax((int) videoEntity.getTime());
        videoView.setVideoURI(Uri.parse(videoEntity.getUri()));
        videoView.start();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        videoView = findViewById(R.id.video_main);
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

        videoView.setOnTouchListener(this);
        videoCancel.setOnClickListener(this);
        videoForward.setOnClickListener(this);
        videoPlay.setOnClickListener(this);
        videoNext.setOnClickListener(this);
        videoLock.setOnClickListener(this);

        videoForward.setOnLongClickListener(this);
        videoNext.setOnLongClickListener(this);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoIndex = videoIndex++ < VideoData.getLocalVideosList(VideoActivity.this).size() ? videoIndex : 0;
                reset();
                initData();
            }
        });

        videoSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                videoView.seekTo(progress);
                if (!videoView.isPlaying()) {
                    videoView.start();
                }
                panelShowingTime = 0;
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
                panelShowingTime = 0;
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                VideoActivity.this.progress = progress;
                if (fromUser) {
                    panelShowingTime = 0;
                    videoView.seekTo(progress);
                }
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        panelShowingTime = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isLock) {
                videoLock.setVisibility(View.VISIBLE);
            } else {
                if (layoutTitle.getVisibility() == View.GONE) {
                    setPanelVisible();
                } else {
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
        panelShowingTime = 0;
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
            videoView.stopPlayback();
            finish();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } else if (id == R.id.video_forward) {
            Log.i("VideoActivity", "onClick-----------video_forward");
            if (videoIndex == 0) {
                Toast.makeText(this, "已经是第一条了", Toast.LENGTH_SHORT).show();
            } else {
                reset();
                videoIndex--;
                initData();
            }
        } else if (id == R.id.video_play) {
            if (isPlaying) {
                videoView.pause();
                isPlaying = false;
                videoPlay.setImageResource(R.drawable.ic_action_play);
            } else {
                videoView.start();
                isPlaying = true;
                videoPlay.setImageResource(R.drawable.ic_action_stop);
            }
        } else if (id == R.id.video_next) {
            Log.i("VideoActivity", "onClick-------------video_next");
            if (videoIndex == VideoData.getLocalVideosList(this).size() - 1) {
                Toast.makeText(this, "已经是最后一条了", Toast.LENGTH_SHORT).show();
            } else {
                reset();
                videoIndex++;
                initData();
            }
        }
    }

    private void reset() {
        isFast = false;
        panelShowingTime = 0;
        progress = 0;
        increment = 1000;
        videoView.stopPlayback();
    }

    /**
     * 快退
     */
    private void fastForward(final View view) {
        if (isFast) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress -= increment;
                    increment += 1000;
                    if (progress > 0 && view.isPressed()) {
                        mHandler.sendEmptyMessage(progress);
                        fastForward(view);
                    } else {
                        increment = 1000;
                        isFast = false;
                    }
                }
            }, 300);
        }
    }

    /**
     * 快进
     */
    private void fastNext(final View view) {
        if (isFast) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress += increment;
                    increment += 1000;
                    if (progress < videoEntity.getTime() && view.isPressed()) {
                        mHandler.sendEmptyMessage(progress);
                        fastNext(view);
                    } else {
                        increment = 1000;
                        isFast = false;
                    }
                }
            }, 300);
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        panelShowingTime = 0;
        int id = v.getId();
        if (id == R.id.video_forward) {
            Log.i("VideoActivity", "onLongClick----------video_next");
            isFast = true;
            fastForward(v);
        } else if (id == R.id.video_next) {
            Log.i("VideoActivity", "onLongClick----------video_next");
            isFast = true;
            fastNext(v);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
        videoView.seekTo(progress);
    }

    @Override
    protected void onPause() {
        videoView.pause();
        videoView.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

}
