package usage.ywb.wrapper.audio.ui.activity;

import java.util.List;
import java.util.Timer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import usage.ywb.wrapper.audio.IAudioInterface;
import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.ui.fragment.AudiosListFragment;
import usage.ywb.wrapper.audio.ui.view.MarqueeTextView;
import usage.ywb.wrapper.audio.service.AudioService;

import usage.ywb.wrapper.audio.utils.AudioData;

/**
 * @author frank.yu
 * @version 2015.05.25
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

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
     * 文件列表
     */
    private List<AudioEntity> audiosList;
    /**
     * 当前实体
     */
    private AudioEntity entity;

    private IAudioInterface iAudio;

    private ServiceConnection serviceConnection;

    private Timer timer;
    private int position = -1;
    private int progress = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", getApplicationInfo().processName);
        initView();
        fragmentLists = new AudiosListFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initPlayer();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            initPlayer();
        }
    }

    /**
     * 加载音乐播放器主界面控件
     */
    private void initView() {
        progressBar = findViewById(R.id.music_progress);
        playingNameMtv = findViewById(R.id.music_playing_name);
        timeCurrentTv = findViewById(R.id.music_current_time);
        timeTotalTv = findViewById(R.id.music_total_time);
        musicForwardIv = findViewById(R.id.iv_music_forward);
        musicPauseIv = findViewById(R.id.iv_music_pause);
        musicNextIv = findViewById(R.id.iv_music_next);
        playingNameMtv.setOnClickListener(this);
        musicForwardIv.setOnClickListener(this);
        musicPauseIv.setOnClickListener(this);
        musicNextIv.setOnClickListener(this);
    }

    private void initPlayer() {
        audiosList = AudioData.initAudiosList(this);
        getSupportFragmentManager().beginTransaction().add(R.id.music_viewpager, fragmentLists).commit();
        final Intent intent = new Intent();
        intent.setClass(this, AudioService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                iAudio = IAudioInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {
                if (iAudio != null) {
                    try {
                        iAudio.release();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
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
            try {
                setPlayerData();
                iAudio.setResource(entity);
                iAudio.prepare();
                iAudio.play();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void stop() {
        if (iAudio != null) {
            try {
                iAudio.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }

    /**
     * 重置音乐播放器界面
     */
    private void setPlayerData() {
        fragmentLists.setCurrPosition(position);
        playingNameMtv.setText(entity.name);
        timeTotalTv.setText(DateFormat.format("mm:ss", entity.time));
        timeCurrentTv.setText(DateFormat.format("mm:ss", progress));
        progressBar.setMax((int) entity.time);
        progressBar.setProgress(progress);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_music_forward:
                toForwardMusic();
                break;
            case R.id.iv_music_pause:
                if (position == -1) {
                    position = 0;
                }
                try {
                    if (iAudio.isPlaying()) {
                        stop();
                    } else {
                        play();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
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
            default:
                break;
        }
    }

    /**
     * 下一曲
     */
    private void toNextMusic() {
        position += 1;
        if (position > audiosList.size() - 1) {
            position = 0;
        }
        play();
    }

    /**
     * 上一曲
     */
    private void toForwardMusic() {
        position -= 1;
        if (position < 0) {
            position = audiosList.size() - 1;
        }
        play();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("MainActivity", "申请读取存储卡权限-------Activity");
        boolean ok = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                ok = false;
                break;
            }
        }
        if (ok) {
            initPlayer();
        } else {
            Toast.makeText(this, "无法取得权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 后退键事件监听
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
        AlertDialog.Builder alertBuilder;
        alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Sure you want to exit the application?");
        alertBuilder.setTitle("Prompt");
        alertBuilder.setPositiveButton("Ensure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                MainActivity.super.onBackPressed();
            }
        });
        alertBuilder.setNegativeButton("Cancel", null);
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }


}