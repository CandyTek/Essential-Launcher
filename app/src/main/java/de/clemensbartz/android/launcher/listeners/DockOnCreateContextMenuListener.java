package de.clemensbartz.android.launcher.listeners;

import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.LauncherAppsUtil;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class DockOnCreateContextMenuListener implements View.OnCreateContextMenuListener {
    private final WeakReference<LauncherApps> launcherAppsWeakReference;

    public DockOnCreateContextMenuListener(LauncherApps launcherApps) {
        this.launcherAppsWeakReference = new WeakReference<>(launcherApps);
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        if ((view instanceof ImageView) && (view.getTag() instanceof ApplicationModel)) {
            ApplicationModel applicationModel = (ApplicationModel) view.getTag();
            contextMenu.setHeaderTitle(applicationModel.label);
            if (Build.VERSION.SDK_INT >= 25) {
                LauncherApps launcherApps = this.launcherAppsWeakReference.get();
                for (ShortcutInfo shortcutInfo : LauncherAppsUtil.getShortcutInfos(launcherApps, applicationModel)) {
                    contextMenu.add(0, 0, 0, shortcutInfo.getShortLabel()).setOnMenuItemClickListener(new ShortcutInfoOnMenuItemClickListener(shortcutInfo, launcherApps));
                }
            }
        }
    }
}
