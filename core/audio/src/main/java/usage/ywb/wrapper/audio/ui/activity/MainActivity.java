package usage.ywb.wrapper.audio.ui.activity;

import java.util.List;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
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

import com.alibaba.android.arouter.facade.annotation.Route;

import usage.ywb.wrapper.audio.IAudioController;
import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.service.AudioReceiver;
import usage.ywb.wrapper.audio.ui.fragment.AudiosListFragment;
import usage.ywb.wrapper.audio.ui.view.MarqueeTextView;
import usage.ywb.wrapper.audio.service.AudioControllerService;

import usage.ywb.wrapper.audio.utils.AudioData;

/**
 * @author yuwenbo
 * @version 2015.05.25
 */
@Route(path = "/audio/MainActivity")
public class MainActivity extends AppCompatActivity implements OnClickListener {

    /**
     * 列表页
     */
    private AudiosListFragment listFragment;
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

    private IAudioController iAudio;

    private ServiceConnection serviceConnection;
    private AudioReceiver audioReceiver;

    private int position = -1;
    private int progress = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity_main);
        Log.i("MainActivity", getApplicationInfo().processName);
        initView();
        listFragment = new AudiosListFragment();
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
        getSupportFragmentManager().beginTransaction().add(R.id.music_viewpager, listFragment).commit();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                iAudio = IAudioController.Stub.asInterface(service);
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
        Intent intent = new Intent(this, AudioControllerService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        audioReceiver = new AudioReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                if (ACTION_CURRENT_POSITION.equals(intent.getAction())) {
                    progress = intent.getIntExtra("CurrentPosition", 0);
                    setSeekPosition();
                }
            }
        };
        registerReceiver(audioReceiver, new IntentFilter(AudioReceiver.ACTION_CURRENT_POSITION));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 播放音乐
     */
    public void start() {
        if (position != -1) {
            entity = audiosList.get(position);
            try {
                setPlayerData();
                iAudio.setResource(entity);
                iAudio.prepare();
                iAudio.start();
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
    }

    /**
     * 重置音乐播放器界面
     */
    private void setPlayerData() {
        listFragment.setCurrPosition(position);
        playingNameMtv.setText(entity.name);
        timeTotalTv.setText(DateFormat.format("mm:ss", entity.time));
        progressBar.setMax((int) entity.time);
        setSeekPosition();
    }

    private void setSeekPosition() {
        timeCurrentTv.setText(DateFormat.format("mm:ss", progress));
        progressBar.setProgress(progress);
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.iv_music_forward) {
            toForwardMusic();
        } else if (id == R.id.iv_music_pause) {
            if (position == -1) {
                position = 0;
            }
            try {
                if (iAudio.isPlaying()) {
                    iAudio.pause();
                } else {
                    iAudio.resume();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.iv_music_next) {
            toNextMusic();
        } else if (id == R.id.music_playing_name) {
            if (position != -1) {
                listFragment.setCurrPosition(position);
            }
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
        start();
    }

    /**
     * 上一曲
     */
    private void toForwardMusic() {
        position -= 1;
        if (position < 0) {
            position = audiosList.size() - 1;
        }
        start();
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

    @Override
    protected void onDestroy() {
        try {
            if (iAudio != null && !iAudio.isPlaying()) {
                iAudio.release();
                iAudio = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
        unregisterReceiver(audioReceiver);
        super.onDestroy();
    }


}