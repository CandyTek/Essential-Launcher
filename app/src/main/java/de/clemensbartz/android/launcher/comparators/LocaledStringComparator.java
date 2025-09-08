package de.clemensbartz.android.launcher.comparators;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/* loaded from: classes.dex */
public final class LocaledStringComparator implements Comparator<String> {
    private final Locale locale;

    public LocaledStringComparator(Locale locale) {
        this.locale = locale;
    }

    @Override // java.util.Comparator
    public int compare(String str, String str2) {
        Locale locale = this.locale;
        if (locale != null) {
            return Collator.getInstance(locale).compare(str, str2);
        }
        return str.compareTo(str2);
    }
}
