package de.clemensbartz.android.launcher.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.clemensbartz.android.launcher.models.ApplicationModel;

public class PicassoIconLoader {
    private static Picasso picassoInstance;
    
    public static void initialize(Context context) {
        if (picassoInstance == null) {
            picassoInstance = new Picasso.Builder(context)
                    .addRequestHandler(new AppIconRequestHandler(context))
                    .build();
        }
    }
    
    public static void loadIcon(ApplicationModel applicationModel, ImageView imageView, 
                              Drawable defaultDrawable) {
        if (picassoInstance == null) {
            initialize(imageView.getContext());
        }
        
        // Set default drawable first
        imageView.setImageDrawable(defaultDrawable);
        
        if (applicationModel.packageName != null && applicationModel.className != null) {
            String uri = AppIconRequestHandler.createUri(applicationModel.packageName, 
                    applicationModel.className);
            
            picassoInstance.load(uri)
                    .placeholder(defaultDrawable)
                    .error(defaultDrawable)
                    .into(new IconTarget(imageView, defaultDrawable));
        }
    }
    
    private static class IconTarget implements Target {
        private final ImageView imageView;
        private final Drawable defaultDrawable;
        
        IconTarget(ImageView imageView, Drawable defaultDrawable) {
            this.imageView = imageView;
            this.defaultDrawable = defaultDrawable;
        }
        
        @Override
        public void onBitmapLoaded(android.graphics.Bitmap bitmap, Picasso.LoadedFrom from) {
            if (imageView != null && bitmap != null) {
                Drawable drawable = new android.graphics.drawable.BitmapDrawable(
                        imageView.getContext().getResources(), bitmap);
                
                // Add ripple effect on API 21+
                if (Build.VERSION.SDK_INT >= 21) {
                    drawable = new RippleDrawable(ColorStateList.valueOf(-7829368), 
                            drawable, null);
                }
                
                imageView.setImageDrawable(drawable);
            }
        }
        
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (imageView != null) {
                Drawable drawable = errorDrawable != null ? errorDrawable : defaultDrawable;
                
                // Add ripple effect on API 21+
                if (Build.VERSION.SDK_INT >= 21) {
                    drawable = new RippleDrawable(ColorStateList.valueOf(-7829368), 
                            drawable, null);
                }
                
                imageView.setImageDrawable(drawable);
            }
        }
        
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (imageView != null) {
                imageView.setImageDrawable(placeHolderDrawable != null ? 
                        placeHolderDrawable : defaultDrawable);
            }
        }
    }
}