package usage.ywb.wrapper.tools;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/11/16 ]
 */
public class BitmapProcess {

    /**
     * 获取bitmap的高对比度结果
     *
     * @param initBitmap
     * @return
     */
    public static Bitmap getHighContrast(Bitmap initBitmap) {
        ColorMatrix cMatrix = new ColorMatrix();
        float[] src = new float[]{
                3, 0, 0, 0, -255,
                0, 3, 0, 0, -255,
                0, 0, 3, 0, -255,
                0, 0, 0, 1, 0};
        cMatrix.set(src);
        Bitmap bitmap = Bitmap.createBitmap(initBitmap.getWidth(),
                initBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(initBitmap, 0, 0, paint);
        return bitmap;
    }


    /**
     * 转灰（在华为荣耀6P上变成全黑）
     *
     * @param old
     * @return
     */
    public static Bitmap getGreyImage(Bitmap old) {
        int width, height;
        height = old.getHeight();
        width = old.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(old);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }


    /**
     * 转成最低饱和度（全灰白图像，）
     *
     * @param initBitmap
     * @return
     */
    public static Bitmap toGreyImage(Bitmap initBitmap) {
        ColorMatrix cMatrix = new ColorMatrix();
        float[] src = new float[]{
                0.213f, 0.715f, 0.072f, 0, 0,
                0.213f, 0.715f, 0.072f, 0, 0,
                0.213f, 0.715f, 0.072f, 0, 0,
                0, 0, 0, 1, 0};
        cMatrix.set(src);
        Bitmap bitmap = Bitmap.createBitmap(initBitmap.getWidth(),
                initBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(initBitmap, 0, 0, paint);
        return bitmap;
    }

    /**
     * 将彩色图转换为灰度图(此法逐像素转换，效率较低)
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }


}
