package de.clemensbartz.android.launcher.util;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Build;
import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.R;

/* loaded from: classes.dex */
public final class ThemeUtil {
    private ThemeUtil() {
    }

    public static void setTheme(Activity activity) {
        switch (Build.VERSION.SDK_INT) {
            case 17:
            case 18:
            case 19:
            case BuildConfig.VERSION_CODE /* 20 */:
                activity.setTheme(R.style.API17ActivityStyle);
                break;
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                activity.setTheme(R.style.API21ActivityStyle);
                break;
            case 28:
                activity.setTheme(R.style.API28ActivityStyle);
                break;
        }
    }

    public static int getActionBarHeight(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null.");
        }
        TypedArray obtainStyledAttributes = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int dimension = (int) obtainStyledAttributes.getDimension(0, 0.0f);
        obtainStyledAttributes.recycle();
        return dimension;
    }
}
