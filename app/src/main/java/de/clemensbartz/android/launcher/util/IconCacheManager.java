package de.clemensbartz.android.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;

public class IconCacheManager {
    private static IconCacheManager instance;
    private final Context context;
    private final LruCache<String, byte[]> iconCache;

    private IconCacheManager(Context context) {
        this.context = context.getApplicationContext();
        
        // 计算缓存大小：假设每个图标平均10KB，缓存500个图标约5MB
        int maxCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8); // 1/8内存用于图标缓存
        maxCacheSize = Math.min(maxCacheSize, 5 * 1024); // 最大5MB
        
        this.iconCache = new LruCache<String, byte[]>(maxCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length / 1024; // 返回KB
            }
        };
    }

    public static synchronized IconCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new IconCacheManager(context);
        }
        return instance;
    }

    public String createKey(String packageName, String className) {
        return packageName + "/" + className;
    }

    public byte[] getIconBytes(String packageName, String className) {
        String key = createKey(packageName, className);
        
        // 1. 先从缓存获取
        byte[] cached = iconCache.get(key);
        if (cached != null) {
            return cached;
        }

        // 2. 从PackageManager加载并缓存
        try {
            PackageManager pm = context.getPackageManager();
            Drawable drawable = pm.getActivityIcon(new ComponentName(packageName, className));
            if (drawable != null) {
                Bitmap bitmap = drawableToBitmap(drawable);
                
                // 压缩为WebP格式存储
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 85, baos);
                byte[] iconBytes = baos.toByteArray();
                
                // 回收bitmap
                bitmap.recycle();
                
                // 存入缓存
                iconCache.put(key, iconBytes);
                return iconBytes;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // 图标不存在
        }

        return null;
    }

    public Bitmap createBitmapFromBytes(byte[] iconBytes) {
        if (iconBytes == null) return null;
        return BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
    }

    public void clearCache() {
        iconCache.evictAll();
    }

    public int getCacheSize() {
        return iconCache.size();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        
        if (width <= 0 || height <= 0) {
            width = height = 96; // 默认大小
        }
        
        // 限制最大尺寸以节省内存
        int maxSize = 144;
        if (width > maxSize || height > maxSize) {
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            width = Math.round(width * scale);
            height = Math.round(height * scale);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}