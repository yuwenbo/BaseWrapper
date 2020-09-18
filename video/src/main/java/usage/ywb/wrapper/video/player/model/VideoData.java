package usage.ywb.wrapper.video.player.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * 本地视屏数据获取类
 *
 * @author yuwenbo
 */
public class VideoData {

    /**
     * 视屏文件名，带后缀
     */
    private final static String NAME = MediaStore.Video.Media.DISPLAY_NAME;
    /**
     * 视屏文件大小（B）
     */
    private final static String SIZE = MediaStore.Video.Media.SIZE;
    /**
     * 视屏内容持续时间
     */
    private final static String TIME = MediaStore.Video.Media.DURATION;
    /**
     * 视屏
     */
    private final static String DATA = MediaStore.Video.Media.DATA;

    private final static ArrayList<VideoEntity> list = new ArrayList<VideoEntity>();


    /**
     * 获取游标
     */
    private static Cursor getCursor(final Context context) {
        ContentResolver resolver = context.getContentResolver();
        final Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        // 查询的字段
        final String[] projection = {NAME, SIZE, TIME, DATA};
        // 排序
        final String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " desc";
        return resolver.query(uri, projection, null, null, sortOrder);
    }

    /**
     * 获取本地视屏列表数据
     */
    public static ArrayList<VideoEntity> getLocalVideosList(final Context context) {
        if (list.isEmpty()) {
            return initLocalVideosList(context);
        }
        return list;
    }

    /**
     * 初始化本地视频列表数据
     */
    public static ArrayList<VideoEntity> initLocalVideosList(final Context context) {
        list.clear();
        Cursor cursor = getCursor(context);
        if (cursor == null) {
            return list;
        }
        final int indexName = cursor.getColumnIndex(NAME);
        final int indexSize = cursor.getColumnIndex(SIZE);
        final int indexData = cursor.getColumnIndex(DATA);
        final int indexTime = cursor.getColumnIndex(TIME);
        VideoEntity video;
        while (cursor.moveToNext()) {
            video = new VideoEntity();
            video.setUri(cursor.getString(indexData));
            video.setName(cursor.getString(indexName));
            video.setSize(cursor.getLong(indexSize));
            video.setTime(cursor.getLong(indexTime));
            list.add(video);
        }
        cursor.close();
        return list;
    }

}
