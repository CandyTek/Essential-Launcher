package de.clemensbartz.android.launcher.listeners;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.IntentUtil;
import de.clemensbartz.android.launcher.util.LauncherAppsUtil;

/* loaded from: classes.dex */
public final class AbsListViewOnCreateContextMenuListener implements View.OnCreateContextMenuListener {
	private static final int ITEM_APP_INFO = 1;
	private static final int ITEM_PINTO = 2;
	private static final int ITEM_TOGGLE_HIDDEN = 3;
	private final WeakReference<DockController> dockControllerWeakReference;
	private final WeakReference<DrawerController> drawerControllerWeakReference;
	private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;
	private final WeakReference<LauncherApps> launcherAppsWeakReference;
	private final WeakReference<PackageManager> packageManagerWeakReference;

	public AbsListViewOnCreateContextMenuListener(PackageManager packageManager,DrawerController drawerController,DrawerListAdapter drawerListAdapter,DockController dockController,Context context) {
		this.packageManagerWeakReference = new WeakReference<>(packageManager);
		this.drawerControllerWeakReference = new WeakReference<>(drawerController);
		this.drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
		this.dockControllerWeakReference = new WeakReference<>(dockController);
		if (Build.VERSION.SDK_INT >= 21) {
			this.launcherAppsWeakReference = new WeakReference<>((LauncherApps) context.getSystemService("launcherapps"));
		} else {
			this.launcherAppsWeakReference = new WeakReference<>(null);
		}
	}

	@Override // android.view.View.OnCreateContextMenuListener
	public void onCreateContextMenu(ContextMenu contextMenu,View view,ContextMenu.ContextMenuInfo contextMenuInfo) {
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		PackageManager packageManager = this.packageManagerWeakReference.get();
		final DockController dockController = this.dockControllerWeakReference.get();
		final DrawerController drawerController = this.drawerControllerWeakReference.get();
		if (packageManager == null || drawerListAdapter == null) {
			return;
		}
		final ApplicationModel item = drawerListAdapter.getItem(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).position);
		contextMenu.setHeaderTitle(item.label);
		if (Build.VERSION.SDK_INT >= 25) {
			LauncherApps launcherApps = this.launcherAppsWeakReference.get();
			for (ShortcutInfo shortcutInfo : LauncherAppsUtil.getShortcutInfos(launcherApps,item)) {
				contextMenu.add(0,0,0,shortcutInfo.getShortLabel())
						.setOnMenuItemClickListener(new ShortcutInfoOnMenuItemClickListener(shortcutInfo,launcherApps));
			}
		}
		if (item.packageName != null) {
			contextMenu.add(0,1,0,R.string.showAppInfo).setIntent(IntentUtil.newAppDetailsIntent(item.packageName));
		}
		SubMenu addSubMenu = contextMenu.addSubMenu(R.string.pinApp);
		// 使用 chatgpt 修复反编译 int i 报错
		for (int i = 0;i < 7;i++) {
			final int index = i; // 给匿名类用的副本
			addSubMenu.add(0,ITEM_PINTO,0,Integer.toString(index + 1))
					.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {
							DockController dockController2 = dockController;
							if (dockController2 == null) {
								return false;
							}
							dockController2.updateDock(index,item); // 使用副本
							return true;
						}
					});
		}
		if (drawerController != null) {
			MenuItem add = contextMenu.add(0,ITEM_TOGGLE_HIDDEN,0,R.string.hidden);
			add.setCheckable(true);
			add.setChecked(item.hidden);
			add.setOnMenuItemClickListener(
					new MenuItem.OnMenuItemClickListener() { // from class: de.clemensbartz.android.launcher.listeners.AbsListViewOnCreateContextMenuListener.2
						@Override // android.view.MenuItem.OnMenuItemClickListener
						public boolean onMenuItemClick(MenuItem menuItem) {
							drawerController.toggleHide(item);
							return true;
						}
					});
		}
	}
}
