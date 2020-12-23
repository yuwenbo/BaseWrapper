package usage.ywb.wrapper.video.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author frank.yu
 */
public class ThumbnailTask2 {

    private static ScheduledThreadPoolExecutor executor;

    private FutureTask<Bitmap> future;
    private Handler handler;
    private WeakReference<ImageView> reference;

    public ThumbnailTask2() {
        if (executor == null) {
            synchronized (this) {
                if (executor == null) {
                    executor = new ScheduledThreadPoolExecutor(3);
                }
            }
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.obj instanceof Bitmap && reference.get() != null) {
                    reference.get().setImageBitmap((Bitmap) msg.obj);
                }
            }
        };
    }

    public ThumbnailTask2 load(String path) {
        future = new FutureTask<Bitmap>(() -> {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            Bitmap bitmap = media.getFrameAtTime();
            media.release();
            BitmapCache.getInstance().put(path, bitmap);
            return bitmap;
        }) {
            @Override
            protected void done() {
                try {
                    Message message = handler.obtainMessage();
                    message.obj = future.get();
                    handler.sendMessage(message);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return this;
    }

    public void setImageView(ImageView imageView) {
        if (future == null) {
            return;
        }
        reference = new WeakReference<>(imageView);
        executor.submit(future);
    }

}
