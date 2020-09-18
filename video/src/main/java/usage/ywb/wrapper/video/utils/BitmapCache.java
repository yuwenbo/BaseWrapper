// Copyright (c) 1998-2015 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2015-XX-XX, frank.yu, creation
// ============================================================================
package usage.ywb.wrapper.video.utils;

import android.graphics.Bitmap;
import androidx.collection.LruCache;

/**
 * 图片缓存
 *
 * @author frank.yu
 */
public class BitmapCache {

    private static LruCache<String, Bitmap> cache;

    private static BitmapCache instance;

    private BitmapCache() {

    }

    static {
        instance = new BitmapCache();
    }

    public static BitmapCache getInstance() {
        if (cache == null) {
            init();
        }
        return instance;
    }

    private static void init() {
        // 获取最大运行内存
        final long maxMemory = Runtime.getRuntime().maxMemory();
        // 缓存大小设置为最大运行内存的五分之一
        final int maxSize = (int) (maxMemory / 1024 / 5);
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(final String key, final Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    /**
     * 加入缓存
     *
     * @param key
     * @param bitmap
     */
    public Bitmap put(final String key, final Bitmap bitmap) {
        if (key == null || bitmap == null || cache == null) {
            return bitmap;
        }
        return cache.put(key, bitmap);
    }

    /**
     * 取出缓存
     *
     * @param key
     * @return
     */
    public Bitmap get(final String key) {
        if (key == null || cache == null) {
            return null;
        }
        return cache.get(key);
    }

    /**
     * 移除缓存中指定数据
     *
     * @param key
     * @return
     */
    public Bitmap remove(final String key) {
        if (key == null || cache == null) {
            return null;
        }
        return cache.remove(key);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        if(cache != null){
            for(String key : cache.snapshot().keySet()){
                cache.remove(key);
            }
            cache = null;
        }
    }

}