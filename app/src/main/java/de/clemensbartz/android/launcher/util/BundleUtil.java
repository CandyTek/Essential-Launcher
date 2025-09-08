package de.clemensbartz.android.launcher.util;

import android.os.Bundle;

/* loaded from: classes.dex */
public final class BundleUtil {
    private BundleUtil() {
    }

    public static Bundle getWidgetOptionsBundle(int i, int i2, int i3, int i4) {
        Bundle bundle = new Bundle(4);
        bundle.putInt("appWidgetMinWidth", i);
        bundle.putInt("appWidgetMaxWidth", i3);
        bundle.putInt("appWidgetMinHeight", i2);
        bundle.putInt("appWidgetMaxHeight", i4);
        return bundle;
    }
}
