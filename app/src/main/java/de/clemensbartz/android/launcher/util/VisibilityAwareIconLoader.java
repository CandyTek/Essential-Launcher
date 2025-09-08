package de.clemensbartz.android.launcher.util;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.models.ApplicationModel;

public class VisibilityAwareIconLoader {
    
    public static void loadIcon(ApplicationModel applicationModel, ImageView imageView, Drawable defaultDrawable) {
        // 首先设置默认图标
        imageView.setImageDrawable(defaultDrawable);
        
        // 检查应用信息是否完整
        if (applicationModel.packageName == null || applicationModel.className == null) {
            return;
        }
        
        // 异步加载图标
        new LoadIconTask(imageView, applicationModel, defaultDrawable).execute();
    }

    private static class LoadIconTask extends AsyncTask<Void, Void, byte[]> {
        private final WeakReference<ImageView> imageViewRef;
        private final ApplicationModel applicationModel;
        private final Drawable defaultDrawable;

        LoadIconTask(ImageView imageView, ApplicationModel applicationModel, Drawable defaultDrawable) {
            this.imageViewRef = new WeakReference<>(imageView);
            this.applicationModel = applicationModel;
            this.defaultDrawable = defaultDrawable;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            ImageView imageView = imageViewRef.get();
            if (imageView == null) return null;
            
            IconCacheManager cacheManager = IconCacheManager.getInstance(imageView.getContext());
            return cacheManager.getIconBytes(applicationModel.packageName, applicationModel.className);
        }

        @Override
        protected void onPostExecute(byte[] iconBytes) {
            ImageView imageView = imageViewRef.get();
            if (imageView == null) return;

            // 使用ViewVisibilityManager进行精确的可见性检测
            if (!ViewVisibilityManager.isImageViewVisible(imageView)) {
                return;
            }

            Drawable drawable = defaultDrawable;
            
            if (iconBytes != null) {
                IconCacheManager cacheManager = IconCacheManager.getInstance(imageView.getContext());
                Bitmap bitmap = cacheManager.createBitmapFromBytes(iconBytes);
                
                if (bitmap != null) {
                    drawable = new BitmapDrawable(imageView.getContext().getResources(), bitmap);
                }
            }

            // 添加涟漪效果（API 21+）
            if (Build.VERSION.SDK_INT >= 21 && drawable != null) {
                drawable = new RippleDrawable(ColorStateList.valueOf(-7829368), drawable, null);
            }

            imageView.setImageDrawable(drawable);
        }
    }

    // 添加清理方法，用于ListView item回收时调用
    public static void onImageViewRecycled(ImageView imageView) {
        ViewVisibilityManager.onImageViewRecycled(imageView);
    }
    
    // 获取当前可见的ImageView数量（用于调试）
    public static int getVisibleImageViewCount() {
        return ViewVisibilityManager.getVisibleImageViewCount();
    }
}