package de.clemensbartz.android.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.models.ApplicationModel;

public class SimpleIconLoader {
    private static final String TAG = SimpleIconLoader.class.getSimpleName();
    private static SimpleIconLoader instance;
    private final LruCache<String, byte[]> iconCache;
    private final Context context;

    private SimpleIconLoader(Context context) {
        this.context = context.getApplicationContext();
        
        // 计算缓存大小：最大5MB用于图标缓存
        int maxCacheSize = Math.min(5 * 1024, (int) (Runtime.getRuntime().maxMemory() / 1024 / 8));
        
        this.iconCache = new LruCache<String, byte[]>(maxCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length / 1024; // 返回KB
            }
        };
    }

    public static SimpleIconLoader getInstance(Context context) {
        if (instance == null) {
            synchronized (SimpleIconLoader.class) {
                if (instance == null) {
                    instance = new SimpleIconLoader(context);
                }
            }
        }
        return instance;
    }

    public static void loadIcon(ApplicationModel applicationModel, ImageView imageView, Drawable defaultDrawable) {
        // 设置默认图标
        // imageView.setImageDrawable(defaultDrawable);
        imageView.setImageDrawable(null);
        
        if (applicationModel.packageName == null || applicationModel.className == null) {
            return;
        }
        
        // 异步加载图标
        new IconLoadTask(imageView, applicationModel, defaultDrawable).execute();
    }

    private static class IconLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewRef;
        private final ApplicationModel applicationModel;
        private final Drawable defaultDrawable;

        IconLoadTask(ImageView imageView, ApplicationModel applicationModel, Drawable defaultDrawable) {
            this.imageViewRef = new WeakReference<>(imageView);
            this.applicationModel = applicationModel;
            this.defaultDrawable = defaultDrawable;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            ImageView imageView = imageViewRef.get();
            if (imageView == null) return null;

            SimpleIconLoader loader = getInstance(imageView.getContext());
            String key = applicationModel.packageName + "/" + applicationModel.className;

            // 1. 先从缓存获取bytes
            byte[] iconBytes = loader.iconCache.get(key);
            if (iconBytes != null) {
                return BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
            }

            // 2. 从系统加载图标
            try {
                PackageManager pm = loader.context.getPackageManager();
                Drawable drawable = pm.getActivityIcon(new ComponentName(applicationModel.packageName, applicationModel.className));
                if (drawable != null) {
                    Bitmap bitmap = drawableToBitmap(drawable);
                    
                    // 压缩为WebP存入缓存
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 85, baos);
                    byte[] compressed = baos.toByteArray();
                    loader.iconCache.put(key, compressed);
                    
                    return bitmap;
                }
            } catch (PackageManager.NameNotFoundException e) {
                // 图标不存在
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewRef.get();
            if (imageView == null) return;


            // 先尝试释放旧的 Drawable 占用的 Bitmap
            Drawable oldDrawable = imageView.getDrawable();
            // imageView.setImageDrawable(null);
            if (oldDrawable instanceof BitmapDrawable) {
                Bitmap oldBitmap = ((BitmapDrawable) oldDrawable).getBitmap();
                if (oldBitmap != null && !oldBitmap.isRecycled()) {
                    oldBitmap.recycle();  // ⚠️ 必须确保不会再用到
                    Log.e(TAG,"onPostExecute: 回收");
                }
            }

            Drawable drawable;
            if (bitmap != null) {
                drawable = new BitmapDrawable(imageView.getContext().getResources(), bitmap);
            } else {
                drawable = defaultDrawable;
            }

            // 添加涟漪效果（API 21+）
            // if (Build.VERSION.SDK_INT >= 21 && drawable != null) {
            //     drawable = new RippleDrawable(ColorStateList.valueOf(-7829368), drawable, null);
            // }

            imageView.setImageDrawable(drawable);
        }

        private static Bitmap drawableToBitmap(Drawable drawable) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            
            if (width <= 0 || height <= 0) {
                width = height = 96; // 默认大小
            }
            
            // 限制最大尺寸节省内存
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

    // 清除缓存
    public void clearCache() {
        iconCache.evictAll();
    }

    // 获取缓存大小（调试用）
    public int getCacheSize() {
        return iconCache.size();
    }
}
