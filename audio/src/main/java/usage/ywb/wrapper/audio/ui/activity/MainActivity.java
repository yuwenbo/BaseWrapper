package usage.ywb.wrapper.audio.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.ui.adapter.AudioPagerAdapter;
import usage.ywb.wrapper.audio.ui.fragment.AudiosListFragment;
import usage.ywb.wrapper.audio.utils.Constants;
import usage.ywb.wrapper.audio.ui.view.MarqueeTextView;
import usage.ywb.wrapper.audio.service.AudioService;

import usage.ywb.wrapper.audio.utils.AudioData;

/**
 * @author frank.yu
 * @version 2015.05.25
 */
public class MainActivity extends AppCompatActivity implements OnClickListener,
        OnLongClickListener, AudioService.OnMusicChangeListener {

    /**
     * 主页面控制
     **/
    private ViewPager mViewPager;
    /**
     * 页面适配器
     */
    private AudioPagerAdapter pagerAdapter;
    /**
     * 页面列表
     */
    private List<Fragment> fragments;
    /**
     * 页面标题列表
     */
    private List<String> titles;
    /**
     * 列表页
     */
    private AudiosListFragment fragmentLists;
    /**
     * 进度条
     */
    private ProgressBar progressBar;
    /**
     * 名字
     */
    private MarqueeTextView playingNameMtv;
    /**
     * 当前播放时间
     */
    private TextView timeCurrentTv;
    /**
     * 总时间
     */
    private TextView timeTotalTv;
    /**
     * 上一个
     */
    private ImageView musicForwardIv;
    /**
     * 暂停
     */
    private ImageView musicPauseIv;
    /**
     * 下一个
     */
    private ImageView musicNextIv;
    /**
     * 播放模式
     */
    private ImageView playModeIv;

    /**
     * 退出提示框
     */
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertBuilder;

    /**
     * 文件列表
     */
    private List<AudioEntity> audiosList;
    /**
     * 当前实体
     */
    private AudioEntity entity;

    private Handler handler;
    private AudioService.AudioBinder binder;
    private ServiceConnection serviceConnection;
    private Timer timer;
    private int position = -1;
    private int progress = 0;
    private static final int FAST_BACK = -1;
    private static final int FAST_NEXT = -2;
    private static final int ACTION_BACK = -3;
    private static final int ACTION_NEXT = -4;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private int playingMode = Constants.MODE_CYCLE_LIST;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", getApplicationInfo().processName);
        initView();
        initViewPager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                audiosList = AudioData.initAudiosList(this);
                initPreferences();
                initPlayer();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            audiosList = AudioData.initAudiosList(this);
            initPreferences();
            initPlayer();
        }
    }


    /**
     * 初始化主页滑动页面
     */
    private void initViewPager() {
        fragmentLists = new AudiosListFragment();
        fragments = new ArrayList<Fragment>();
        fragments.add(fragmentLists);
        titles = new ArrayList<String>();
        titles.add(getResources().getString(R.string.page_list));
        titles.add(getResources().getString(R.string.page_play));
        titles.add(getResources().getString(R.string.page_online));
        pagerAdapter = new AudioPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(pagerAdapter);
    }


    private void initPreferences() {
        preferences = getSharedPreferences(Constants.SHARE_MUSIC, MODE_PRIVATE);
        editor = preferences.edit();
        playingMode = preferences.getInt(Constants.KEY_CURR_MODE, Constants.MODE_CYCLE_LIST);
        position = preferences.getInt(Constants.KEY_CURR_POSITION, -1);
        progress = preferences.getInt(Constants.KEY_CURR_PROGRESS, 0);
        onChoiceMode();
        if (position > -1 && position < audiosList.size()) {
            entity = audiosList.get(position);
            setPlayerData();
        } else {
            if (audiosList.size() > 0) {
                entity = audiosList.get(0);
            }
        }
    }


    private void initPlayer() {
        final Intent intent = new Intent();
//        intent.setAction(Constants.MUSIC_SERVICE);
        intent.setClass(this, AudioService.class);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FAST_NEXT:
                        musicForwardIv.setEnabled(true);
                        break;
                    case FAST_BACK:
                        musicNextIv.setEnabled(true);
                        break;
                    case ACTION_BACK:
                        toForwardMusic();
                        break;
                    case ACTION_NEXT:
                        toNextMusic();
                        break;
                    default:
                        progress = msg.what;
                        timeCurrentTv.setText(DateFormat.format("mm:ss", progress));
                        progressBar.setProgress(progress);
                        break;
                }
            }
        };
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                binder = (AudioService.AudioBinder) service;
                binder.setOnMusicChangeListener(MainActivity.this);
                binder.setHandler(handler);
                binder.seek(progress);
                binder.setEntity(entity);
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {

            }
        };
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        timer = new Timer();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 播放音乐
     */
    public void play() {
        if (position != -1) {
            entity = audiosList.get(position);
            binder.setEntity(entity);
            setPlayerData();
            MediaPlayer mediaPlayer = binder.prepare();
            binder.playing();
        }
    }

    /**
     * 重置音乐播放器界面
     */
    private void setPlayerData() {
        fragmentLists.setCurrPosition(position);
        playingNameMtv.setText(entity.getName());
        timeTotalTv.setText(DateFormat.format("mm:ss", entity.getTime()));
        timeCurrentTv.setText(DateFormat.format("mm:ss", progress));
        progressBar.setMax((int) entity.getTime());
        progressBar.setProgress(progress);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_music_forward:
                toForwardMusic();
                break;
            case R.id.iv_music_pause:
                if (position != -1) {
                    binder.toPlaying();
                }
                break;
            case R.id.iv_music_next:
                toNextMusic();
                break;
            case R.id.music_playing_name:
                if (position != -1) {
                    fragmentLists.setCurrPosition(position);
                }
                break;
            case R.id.iv_play_mode:
                if (playingMode == 3) {
                    playingMode = 0;
                } else {
                    playingMode++;
                }
                onChoiceMode();
                break;
            default:
                break;
        }
    }


    /**
     * 选择播放模式
     */
    private void onChoiceMode() {
        switch (playingMode) {
            case Constants.MODE_CYCLE_LIST:
                playModeIv.setImageResource(R.drawable.play_icn_loop);
                break;
            case Constants.MODE_CYCLE_ONE:
                playModeIv.setImageResource(R.drawable.play_icn_one);
                break;
            case Constants.MODE_ORDER_LIST:
                playModeIv.setImageResource(R.drawable.play_icn_order);
                break;
            case Constants.MODE_RANDOM:
                playModeIv.setImageResource(R.drawable.play_icn_shuffle);
                break;
            default:
                break;
        }

    }

    /**
     * 播放模式
     */
    private void onPlayMode() {
        switch (playingMode) {
            case Constants.MODE_CYCLE_LIST:
                toNextMusic();
                break;
            case Constants.MODE_CYCLE_ONE:
                play();
                break;
            case Constants.MODE_ORDER_LIST:
                onPlayingList();
                break;
            case Constants.MODE_RANDOM:
                onPlayingRandom();
                break;
            default:
                break;
        }
    }

    /**
     * 随机播放模式
     */
    private void onPlayingRandom() {
        position = (int) Math.round(Math.random() * (audiosList.size() - 1));
        play();
    }

    /**
     * 列表顺序播放不循环
     */
    private void onPlayingList() {
        position += 1;
        if (position > audiosList.size() - 1) {
            return;
        } else {
            play();
        }
    }

    /**
     * 下一曲
     */
    private void toNextMusic() {
        if (playingMode == Constants.MODE_RANDOM) {
            onPlayingRandom();
        } else {
            position += 1;
            if (position > audiosList.size() - 1) {
                position = 0;
            }
            play();
        }
    }

    /**
     * 上一曲
     */
    private void toForwardMusic() {
        if (playingMode == Constants.MODE_RANDOM) {
            onPlayingRandom();
        } else {
            position -= 1;
            if (position < 0) {
                position = audiosList.size() - 1;
            }
            play();
        }
    }

    /**
     * 加载音乐播放器主界面控件
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.music_viewpager);
        progressBar = (ProgressBar) findViewById(R.id.music_progress);
        playingNameMtv = (MarqueeTextView) findViewById(R.id.music_playing_name);
        timeCurrentTv = (TextView) findViewById(R.id.music_current_time);
        timeTotalTv = (TextView) findViewById(R.id.music_total_time);
        musicForwardIv = (ImageView) findViewById(R.id.iv_music_forward);
        musicPauseIv = (ImageView) findViewById(R.id.iv_music_pause);
        musicNextIv = (ImageView) findViewById(R.id.iv_music_next);
        playModeIv = (ImageView) findViewById(R.id.iv_play_mode);
        playingNameMtv.setOnClickListener(this);
        musicForwardIv.setOnClickListener(this);
        musicPauseIv.setOnClickListener(this);
        musicNextIv.setOnClickListener(this);
        musicForwardIv.setOnLongClickListener(this);
        musicNextIv.setOnLongClickListener(this);
        playModeIv.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Log.i("MainActivity","申请读取存储卡权限-------Activity");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                audiosList = AudioData.initAudiosList(this);
                fragmentLists.notifyDataSetChanged();
                initPreferences();
                initPlayer();
            } else {
                Toast.makeText(this, "无法取得权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 后退键事件监听
     */
    @Override
    public void onBackPressed() {
        if (alertBuilder == null) {
            alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Sure you want to exit the application?");
            alertBuilder.setTitle("Prompt");
            alertBuilder.setPositiveButton("Ensure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    finish();
                }
            });
            alertBuilder.setNegativeButton("Cancel", null);
        }
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onLongClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_music_forward:
                toFastBack(v);
                break;
            case R.id.iv_music_next:
                toFastNext(v);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (binder != null) {
            binder.onRestart();
        }
    }

    @Override
    protected void onStop() {
        if (binder != null) {
            binder.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        editor.putInt(Constants.KEY_CURR_POSITION, position);
        editor.putInt(Constants.KEY_CURR_PROGRESS, progress);
        editor.putInt(Constants.KEY_CURR_MODE, playingMode);
        editor.commit();
        unbindService(serviceConnection);
        super.onDestroy();
    }

    /**
     * 快进
     */
    private void toFastNext(final View v) {
        progress = progressBar.getProgress();
        musicForwardIv.setEnabled(false);
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (v.isPressed()) {
                    progress += 5000;
                    if (progress < entity.getTime()) {
                        binder.setFast(true);
                        handler.sendEmptyMessage(progress);
                    } else {
                        progress = 0;
                        handler.sendEmptyMessage(ACTION_NEXT);
                        binder.setFast(false);
                        cancel();
                    }
                } else {
                    handler.sendEmptyMessage(FAST_NEXT);
                    if (progress < entity.getTime()) {
                        binder.seek(progress);
                    }
                    binder.setFast(false);
                    cancel();
                }
            }
        };
        timer.schedule(task, 0, 300);
    }

    /**
     * 快退
     */
    private void toFastBack(final View v) {
        progress = progressBar.getProgress();
        musicNextIv.setEnabled(false);
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (v.isPressed()) {
                    progress -= 5000;
                    if (progress > 0) {
                        binder.setFast(true);
                        handler.sendEmptyMessage(progress);
                    } else {
                        progress = 0;
                        handler.sendEmptyMessage(ACTION_BACK);
                        binder.setFast(false);
                        cancel();
                    }
                } else {
                    handler.sendEmptyMessage(FAST_BACK);
                    if (progress > 0) {
                        binder.seek(progress);
                    }
                    binder.setFast(false);
                    cancel();
                }
            }
        };
        timer.schedule(task, 0, 300);
    }


    @Override
    public void onChange(final MediaPlayer mediaPlayer, final int status) {
        switch (status) {
            case AudioService.OnMusicChangeListener.STATUS_COMPLETED:
                onPlayMode();
                break;
            case AudioService.OnMusicChangeListener.STATUS_PLAYING:
                musicPauseIv.setImageResource(R.drawable.btn_pause);
                break;
            case AudioService.OnMusicChangeListener.STATUS_STOP:
                musicPauseIv.setImageResource(R.drawable.btn_play);
                break;
            default:
                break;
        }
    }


}