package usage.ywb.wrapper.video.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author frank.yu
 */
public class ThumbnailTask extends AsyncTask<String, Void, Bitmap> {

    private WeakReference<ImageView> reference;

    public ThumbnailTask(final ImageView imageView) {
        reference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(final String... params) {
        final String path = params[0];

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        Bitmap bitmap = media.getFrameAtTime();
        media.release();

//        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100,
//                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        BitmapCache.getInstance().put(path, bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(final Bitmap result) {
        super.onPostExecute(result);
        if (result != null && reference.get() != null) {
            reference.get().setImageBitmap(result);
        }
    }

}
