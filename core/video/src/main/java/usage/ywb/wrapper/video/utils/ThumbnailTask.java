package usage.ywb.wrapper.video.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author frank.yu
 */
public class ThumbnailTask extends AsyncTask<String, Void, Bitmap> {

    private WeakReference<ImageView> reference;

    public ThumbnailTask(final String path, final ImageView imageView) {
        reference = new WeakReference<>(imageView);
        execute(path);
    }

    @Override
    protected Bitmap doInBackground(final String... params) {
        final String path = params[0];

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        Bitmap bitmap = media.getFrameAtTime();

//        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path , Thumbnails.FULL_SCREEN_KIND);
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, getWidth(), getHeight(),
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
