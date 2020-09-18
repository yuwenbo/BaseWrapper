// Copyright (c) 1998-2015 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2015-XX-XX, frank.yu, creation
// ============================================================================

package usage.ywb.wrapper.audio.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import usage.ywb.wrapper.audio.entity.AudioEntity;

/**
 * @author yuwenbo DATE:2015.05.25
 */
public class AudioData {

    private final static String ID = MediaStore.Audio.Media._ID;

    private final static String NAME = MediaStore.Audio.Media.DISPLAY_NAME;

    private final static String SIZE = MediaStore.Audio.Media.SIZE;

    private final static String TIME = MediaStore.Audio.Media.DURATION;

    private final static String ALBUM = MediaStore.Audio.Media.ALBUM;// 专辑

    private final static String ARTIST = MediaStore.Audio.Media.ARTIST;// 艺术家

    private final static String DATA = MediaStore.Audio.Media.DATA;

    private final static List<AudioEntity> list = new ArrayList<AudioEntity>();

    /**
     * 获取列表
     * 
     * @return
     */
    public static List<AudioEntity> getAudiosList() {
        return list;
    }

    /**
     * 获取游标
     * 
     * @param context
     * @return
     */
    private static Cursor getCursor(final Context context) {
        ContentResolver resolver = context.getContentResolver();
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // 查询的字段
        final String[] projection = {
                ID, NAME, SIZE, TIME, DATA, ALBUM, ARTIST
        };
        // 排序
        final String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " asc";
        return resolver.query(uri, projection, null, null, sortOrder);
    }

    /**
     * 初始化音频文件列表
     * 
     * @param context
     * @return
     */
    public static List<AudioEntity> initAudiosList(final Context context) {
        list.clear();
        Cursor cursor = getCursor(context);
        if (cursor != null) {
            final int indexId = cursor.getColumnIndex(ID);
            final int indexName = cursor.getColumnIndex(NAME);
            final int indexSize = cursor.getColumnIndex(SIZE);
            final int indexData = cursor.getColumnIndex(DATA);
            final int indexTime = cursor.getColumnIndex(TIME);
            final int indexAlbum = cursor.getColumnIndex(ALBUM);
            final int indexArtist = cursor.getColumnIndex(ARTIST);
            AudioEntity music;
            while (cursor.moveToNext()) {
                music = new AudioEntity();
                music.setId(cursor.getString(indexId));
                music.setData(cursor.getString(indexData));
                music.setName(cursor.getString(indexName));
                music.setSize(cursor.getLong(indexSize));
                music.setTime(cursor.getLong(indexTime));
                music.setAlbum(cursor.getString(indexAlbum));
                music.setArtist(cursor.getString(indexArtist));
                list.add(music);
            }
            cursor.close();
        }
        return list;
    }

}
