package usage.ywb.wrapper.audio.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

import usage.ywb.wrapper.audio.IAudioController;
import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.ui.activity.MainActivity;


/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/18 ]
 */
public class AudioController extends IAudioController.Stub {

    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "channel_name";

    private Context context;

    private Notification notification;
    private NotificationManager manager;

    //耳机插拔的系统广播
    private BroadcastReceiver receiver;
    private AudioReceiver audioReceiver;

    private MediaPlayer mediaPlayer;
    private AudioEntity entity;

    private static final int NOTIFICATION_AUDIO_ID = 1;


    public AudioController(Context context) {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }

    private void initNotification(AudioEntity entity) {
        final Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_playing))
                    .setSmallIcon(R.drawable.ic_music_logo)
                    .setContentTitle(entity.name)
                    .setContentText(entity.artist)
                    .setTicker(entity.album)
                    .setOngoing(true)//设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载，同步操作，主动网络连接)
                    .setAutoCancel(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_playing))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(entity.name)
                    .setContentText(entity.artist)
                    .setTicker(entity.album)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .build();
        }
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 注册监听耳机拔出音乐暂停
     */
    private void initHeadset() {
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    final String action = intent.getAction();
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            manager.cancel(NOTIFICATION_AUDIO_ID);
                        }
                    }
                }
            };
        }
        context.registerReceiver(receiver, filter);
    }

    private void setAudioReceiver() {
        Intent intent = new Intent(context, AudioReceiver.class);

        context.sendBroadcast(intent);
    }

    @Override
    public void setResource(AudioEntity entity) throws RemoteException {
        this.entity = entity;
        if (entity != null) {
            initNotification(entity);
            notification.flags = Notification.FLAG_NO_CLEAR;// 点击清除的时候不清除
        }
    }

    @Override
    public void prepare() throws RemoteException {
        try {
            initHeadset();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Uri.parse(entity.data).getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play() throws RemoteException {
        mediaPlayer.start();
        if (notification != null) {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            manager.notify(NOTIFICATION_AUDIO_ID, notification);
        }
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seek(int progress) throws RemoteException {
        mediaPlayer.seekTo(progress);
    }

    @Override
    public void stop() throws RemoteException {
        mediaPlayer.pause();
        if (notification != null) {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.cancel(NOTIFICATION_AUDIO_ID);
        }
    }

    @Override
    public void release() throws RemoteException {
        mediaPlayer.stop();
        mediaPlayer.release();
        if (notification != null) {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.cancel(NOTIFICATION_AUDIO_ID);
        }
        context.unregisterReceiver(receiver);
    }


}
