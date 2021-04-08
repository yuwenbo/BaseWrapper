package usage.ywb.wrapper.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import java.io.ByteArrayOutputStream;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/11/16 ]
 */
public class DrawableConverter {


    /**
     * drawable转byte[]
     *
     * @param drawable
     * @return
     */
    public static byte[] drawableToBytes(final Drawable drawable) {
        if (drawable != null) {
            final Bitmap bitmap = drawableToBitmap(drawable);
            return BitmapToBytes(bitmap);
        } else {
            return null;
        }
    }

    /**
     * byte[]转drawable
     *
     * @param b
     * @return Drawable或null
     */
    @SuppressWarnings("deprecation")
    public static Drawable bytesToDrawable(final byte[] b) {
        if (b != null) {
            final Bitmap bitmap = BytesToBitmap(b);
            return new BitmapDrawable(bitmap);
        }
        return null;
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return Bitmap或null
     */
    public static Bitmap drawableToBitmap(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            final Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 将图片bitmap转换为byte[],不会对图片进行压缩
     *
     * @return byte[]或 null
     */
    public static byte[] BitmapToBytes(final Bitmap bm) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * 将字节数据byte[]转换为图片 bitmap,如果方法为null,也会返回null
     *
     * @return Bitmap或null
     */
    public static Bitmap BytesToBitmap(final byte[] b) {
        if (b == null) {
            return null;
        }
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}
