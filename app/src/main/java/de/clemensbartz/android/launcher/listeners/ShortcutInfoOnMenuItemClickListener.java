package de.clemensbartz.android.launcher.listeners;

import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.view.MenuItem;

/* loaded from: classes.dex */
public final class ShortcutInfoOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
	private final LauncherApps launcherApps;
	private final ShortcutInfo shortcutInfo;

	public ShortcutInfoOnMenuItemClickListener(ShortcutInfo shortcutInfo,LauncherApps launcherApps) {
		this.shortcutInfo = shortcutInfo;
		this.launcherApps = launcherApps;
	}

	@Override // android.view.MenuItem.OnMenuItemClickListener
	public boolean onMenuItemClick(MenuItem menuItem) {
		if (!this.launcherApps.hasShortcutHostPermission()) {
			return false;
		}
		this.launcherApps.startShortcut(this.shortcutInfo,null,null);
		return true;
	}
}
