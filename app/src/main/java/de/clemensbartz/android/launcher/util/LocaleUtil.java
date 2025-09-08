package de.clemensbartz.android.launcher.util;

import android.content.Context;
import android.os.Build;
import java.util.Locale;

/* loaded from: classes.dex */
public final class LocaleUtil {
    private LocaleUtil() {
    }

    public static Locale getLocale(Context context) {
        if (context == null) {
            throw new NullPointerException("No context given.");
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return context.getResources().getConfiguration().getLocales().get(0);
        }
        return context.getResources().getConfiguration().locale;
    }
}
