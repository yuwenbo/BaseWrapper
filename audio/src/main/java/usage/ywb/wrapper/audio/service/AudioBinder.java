package usage.ywb.wrapper.audio.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;

import java.io.IOException;

import usage.ywb.wrapper.audio.IAudioInterface;
import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.ui.activity.MainActivity;

/**
 * @author yuwenbo
 * @version [ V.2.9.3  2020/9/18 ]
 */
public class AudioBinder extends IAudioInterface.Stub {

    private Context context;

    private MediaPlayer mediaPlayer;

    private Notification.Builder builder;
    private Notification notification;
    private NotificationManager manager;

    private static final int NOTIFICATION_AUDIO_ID = 1;

    private AudioEntity entity;


    public AudioBinder(Context context) {
        this.context = context;
        initNotification();
    }

    public void setEntity(final AudioEntity entity) {
        this.entity = entity;
        builder.setContentTitle(entity.getName());
        builder.setContentText(entity.getArtist());
        // 每次更新必须创建
        notification = builder.build();
    }

    private void initNotification() {
        final Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent updateIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.actionbar_music_selected);
        builder.setAutoCancel(true);
        builder.setContentIntent(updateIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_playing));
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void prepare() throws RemoteException {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Uri.parse(entity.getData()).getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play() throws RemoteException {
        mediaPlayer.pause();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.cancel(NOTIFICATION_AUDIO_ID);
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
        mediaPlayer.start();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(NOTIFICATION_AUDIO_ID, notification);
    }

    @Override
    public void release() throws RemoteException {
        if (notification != null) {
            mediaPlayer.stop();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.cancel(NOTIFICATION_AUDIO_ID);
            mediaPlayer.release();
        }
    }

}
