package de.clemensbartz.android.launcher.models;

import de.clemensbartz.android.launcher.listeners.DrawerLetterKeyListener;
import de.clemensbartz.android.launcher.util.Han2Pin;

/* loaded from: classes.dex */
public final class ApplicationModel {
    public String className;
    public boolean hidden;
    public String label;
    public String packageName;

    // 可选：提前缓存拼音首字母
    public char pinyinFirstChar = 0;

    public static void setPinyinFirst(ApplicationModel applicationModel) {
        String tempLabel = applicationModel.label.trim();
        if (!tempLabel.isEmpty()) {
            if (DrawerLetterKeyListener.isChinese(tempLabel.charAt(0))) {
                String tempPinyin = Han2Pin.convert(tempLabel.charAt(0));
                if (!tempPinyin.isEmpty()) {
                    applicationModel.pinyinFirstChar = tempPinyin.charAt(0);
                }
            } else {
                applicationModel.pinyinFirstChar = tempLabel.charAt(0);
            }
        }

    }


}
