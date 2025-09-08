package de.clemensbartz.android.launcher.util;

import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Process;

import java.util.ArrayList;
import java.util.List;

import de.clemensbartz.android.launcher.models.ApplicationModel;

/* loaded from: classes.dex */
public final class LauncherAppsUtil {
	private LauncherAppsUtil() {
	}

	public static List<ShortcutInfo> getShortcutInfos(LauncherApps launcherApps,ApplicationModel applicationModel) {
		ArrayList arrayList = new ArrayList(0);
		if (launcherApps == null || applicationModel == null || !launcherApps.hasShortcutHostPermission()) {
			return arrayList;
		}
		LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
		shortcutQuery.setQueryFlags(11);
		shortcutQuery.setPackage(applicationModel.packageName);
		List<ShortcutInfo> shortcuts = launcherApps.getShortcuts(shortcutQuery,Process.myUserHandle());
		return shortcuts == null ? arrayList : shortcuts;
	}
}
