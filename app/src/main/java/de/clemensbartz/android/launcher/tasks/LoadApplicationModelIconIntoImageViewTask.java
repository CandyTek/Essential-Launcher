package de.clemensbartz.android.launcher.tasks;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class LoadApplicationModelIconIntoImageViewTask extends AsyncTask<Integer, Integer, Drawable> {
    private final ApplicationModel applicationModel;
    private final Drawable defaultDrawable;
    private final WeakReference<ImageView> imageViewWeakReference;
    private final PackageManager packageManager;

    public LoadApplicationModelIconIntoImageViewTask(ImageView imageView, ApplicationModel applicationModel, PackageManager packageManager, Drawable drawable) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.applicationModel = applicationModel;
        this.packageManager = packageManager;
        this.defaultDrawable = drawable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Drawable doInBackground(Integer... numArr) {
        if (this.applicationModel.packageName != null && this.applicationModel.className != null) {
            try {
                return this.packageManager.getActivityIcon(new ComponentName(this.applicationModel.packageName, this.applicationModel.className));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Drawable drawable) {
        ImageView imageView = this.imageViewWeakReference.get();
        if (imageView != null) {
            if (drawable == null) {
                drawable = this.defaultDrawable;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                drawable = new RippleDrawable(ColorStateList.valueOf(-7829368), drawable, null);
            }
            imageView.setImageDrawable(drawable);
        }
    }
}
