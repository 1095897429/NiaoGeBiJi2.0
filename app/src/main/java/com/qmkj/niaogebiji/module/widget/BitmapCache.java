package com.qmkj.niaogebiji.module.widget;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-03
 * 描述:
 */
public class BitmapCache {

    private static BitmapCache cache;
    private LruCache<String, Bitmap> mMemoryCache;

    public static BitmapCache getInstance() {
        if (cache == null) {
            cache = new BitmapCache();
        }
        return cache;
    }

    private BitmapCache() {
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;

//        if (cacheSize > (maxMemory / 3))
//            cacheSize = (maxMemory / 3);

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;

            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (!oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
            KLog.d("tag","长度是 " + mMemoryCache.size());
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Bitmap对象的实例
     *
     * @param picPath 图片url
     * @return Bitmap
     */
    public Bitmap getBitmap(String picPath) {
        Bitmap bmp = getBitmapFromMemCache(picPath);

        if (bmp == null) {
            //从网络下载图片,并将其添加到mMemoryCache中去
            //..........................
            if (bmp != null){
                addBitmapToMemoryCache(picPath, bmp);
            }
        }
        return bmp;
    }
}
