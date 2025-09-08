package de.clemensbartz.android.launcher.comparators;

import android.content.Context;
import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.LocaleUtil;
import java.io.Serializable;
import java.util.Comparator;

/* loaded from: classes.dex */
public final class ApplicationModelComparator implements Comparator<ApplicationModel>, Serializable {
    private final LocaledStringComparator localedStringComparator;

    public ApplicationModelComparator(Context context) {
        this.localedStringComparator = new LocaledStringComparator(LocaleUtil.getLocale(context));
    }

    @Override // java.util.Comparator
    public int compare(ApplicationModel applicationModel, ApplicationModel applicationModel2) {
        String str = BuildConfig.FLAVOR;
        String str2 = (applicationModel == null || applicationModel.label == null) ? BuildConfig.FLAVOR : applicationModel.label;
        if (applicationModel2 != null && applicationModel2.label != null) {
            str = applicationModel2.label;
        }
        return this.localedStringComparator.compare(str2, str);
    }
}
