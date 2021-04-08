package usage.ywb.wrapper.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/11/16 ]
 */
public class BitmapCompact {

    /**
     * 压缩图片，并返回压缩后的图片
     *
     * @param bytes    原图片数据
     * @param toWidth  想要压缩的目标宽度
     * @param toHeight 想要压缩的目标高度
     * @return
     */
    public static Bitmap getBitmap(final byte[] bytes, final int toWidth,
                                   final int toHeight) {
        final Options options = new Options();
        // 仅仅测量图片的边界
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // 为图片设置缩放比率
        options.inSampleSize = getRatio(options, toWidth, toHeight);
        // 配置更改为不是仅仅测量边界
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 压缩图片，并返回压缩后的图片
     *
     * @param bytes    原图片数据
     * @param ratio    压缩比率
     * @return
     */
    public static Bitmap getBitmap(final byte[] bytes, final int ratio) {
        final Options options = new Options();
        // 仅仅测量图片的边界
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // 为图片设置缩放比率
        options.inSampleSize = ratio;
        // 配置更改为不是仅仅测量边界
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 根据图片的边界信息和容器尺寸为图片设置压缩比率
     *
     * @param options   图片的配置信息
     * @param toWidth  压缩的目标宽度
     * @param toHeight 压缩的目标高度
     * @return
     */
    private static int getRatio(final Options options, final int toWidth,
                                final int toHeight) {
        // 获得图片的实际宽度
        final int width = options.outWidth;
        // 获得图片的实际高度
        final int height = options.outHeight;
        Log.i("TAG", "---------" + width + "," + height);

        // 压缩比率默认为1，即不压缩
        int ratio = 1;
        if (toWidth < width || toHeight < height) {
            final int ratioWidth = Math.round(width / toWidth);
            final int ratioheight = Math.round(height / toHeight);
            // 取宽高比率中的较小的作为压缩的比率
            ratio = ratioWidth < ratioheight ? ratioWidth : ratioheight;
        }
        return ratio;
    }

}
