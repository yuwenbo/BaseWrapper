package usage.ywb.wrapper.audio.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.ui.activity.MainActivity;

/**
 * @author frank.yu
 *
 * DATE:2015.05.28
 */
public class AudioService extends Service implements OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private Timer timer;
    private TimerTask task;
    private Handler handler;
    private boolean isFast = false;

    private Notification.Builder notifiBuilder;
    private Notification notification;
    private NotificationManager notifiManager;
    private static final int NOTIFICATION_ID_1 = 1;

    private OnMusicChangeListener onMusicChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("AudioService", getApplicationInfo().processName);

        initPlayer();
        initNotification();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new AudioBinder();
    }

    /**
     * 加载播放器
     */
    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        initHeadset();
        timer = new Timer();
    }

    /**
     * 注册监听耳机拔出音乐暂停
     */
    private void initHeadset() {
        filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        task.cancel();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        notifiManager.cancel(NOTIFICATION_ID_1);
                        if (onMusicChangeListener != null) {
                            onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_STOP);
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
    }


    public class AudioBinder extends Binder {

        private AudioEntity entity;


        public void setEntity(final AudioEntity entity) {
            this.entity = entity;
            notifiBuilder.setContentTitle(entity.getName());
            notifiBuilder.setContentText(entity.getArtist());
            // 每次更新必须创建
            notification = notifiBuilder.build();
        }

        public MediaPlayer prepare(){
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(Uri.parse(entity.getData()).getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mediaPlayer;
        }


        /**
         * @param onMusicChangeListener
         *            the onMusicChangeListener to set
         */
        public void setOnMusicChangeListener(final OnMusicChangeListener onMusicChangeListener) {
            AudioService.this.onMusicChangeListener = onMusicChangeListener;
        }

        /**
         * 是否正在快进或快退
         *
         * @param isFast
         */
        public void setFast(final boolean isFast) {
            AudioService.this.isFast = isFast;
        }

        /**
         * 设置Handler，用于异步更新主UI中的播放进度
         *
         * @param handler
         */
        public void setHandler(final Handler handler) {
            AudioService.this.handler = handler;
        }

        /**
         * 设置播放位置
         *
         * @param progress
         */
        public void seek(final int progress) {
            mediaPlayer.seekTo(progress);
        }

        /**
         * 暂停或播放
         */
        public void toPlaying() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                task.cancel();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notifiManager.cancel(NOTIFICATION_ID_1);
                if (onMusicChangeListener != null) {
                    onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_STOP);
                }
            } else {
                mediaPlayer.start();
                onRestart();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notifiManager.notify(NOTIFICATION_ID_1, notification);
                if (onMusicChangeListener != null) {
                    onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_PLAYING);
                }
            }
        }

        public void onStop(){
            if(task != null){
                task.cancel();
            }
        }

        public void onRestart(){
            task = new TimerTask() {
                @Override
                public void run() {
                    if (!isFast && handler != null) {
                        handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }

        /**
         * 播放音乐
         */
        public void playing() {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notifiManager.notify(NOTIFICATION_ID_1, notification);
            if (onMusicChangeListener != null) {
                onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_INIT);
            }
            mediaPlayer.start();
            if (onMusicChangeListener != null) {
                onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_PLAYING);
            }
            if(task != null){
                task.cancel();
            }
            onRestart();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task!=null){
            task.cancel();
            timer.cancel();
        }
        if(notification != null){
            mediaPlayer.stop();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notifiManager.cancel(NOTIFICATION_ID_1);
            mediaPlayer.release();
        }
        if (onMusicChangeListener != null) {
            onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_STOP);
        }
        unregisterReceiver(receiver);
    }

    /**
     * 初始化通知栏
     */
    private void initNotification() {
        final Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent updateIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notifiBuilder = new Notification.Builder(this);
        notifiBuilder.setSmallIcon(R.drawable.actionbar_music_selected);
        notifiBuilder.setAutoCancel(true);
        notifiBuilder.setContentIntent(updateIntent);
        notifiBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_playing));
        notifiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    /**
     * 监听音乐状态改变
     *
     * @author frank.yu
     */
    public interface OnMusicChangeListener {

        /**
         * 正在播放
         */
        public static final int STATUS_INIT = -1;
        /**
         * 正在播放
         */
        public static final int STATUS_PLAYING = 1;
        /**
         * 播放停止
         */
        public static final int STATUS_STOP = 3;
        /**
         * 播放完成
         */
        public static final int STATUS_COMPLETED = 4;
        /**
         * 状态发生改变
         *
         * @param status
         */
        public void onChange(final MediaPlayer mediaPlayer, final int status);

    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        if (onMusicChangeListener != null) {
            onMusicChangeListener.onChange(mediaPlayer, OnMusicChangeListener.STATUS_COMPLETED);
        }
    }

}
