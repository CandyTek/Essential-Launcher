package de.clemensbartz.android.launcher.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.listeners.DockOnCreateContextMenuListener;
import de.clemensbartz.android.launcher.models.ApplicationModel;
// import de.clemensbartz.android.launcher.util.SimpleIconLoader;
import de.clemensbartz.android.launcher.util.IntentUtil;

/* loaded from: classes.dex */
@Deprecated
public final class DockController {
	private static final int DENSITY_XXXHIGH = 640;
	public static final String KEY_IS_SHOWING_ALL_DOCK_ICONS = "isShowingAllDockIcons";
	public static final int NUMBER_OF_ITEMS = 7;
	public static final String PIN_PREFIX = "pin_";
	public static final String SEPARATOR = "|";
	private final Drawable defaultDrawable;
	private final ArrayList<ImageView> dockItems;
	private boolean isShowingAllDockIcons;
	private final WeakReference<PackageManager> packageManagerWeakReference;
	private final WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;

	public DockController(final Context context,PackageManager packageManager,SharedPreferencesDAO sharedPreferencesDAO,Drawable drawable,ArrayList<ImageView> arrayList) {
		LauncherApps launcherApps;
		this.isShowingAllDockIcons = false;
		this.dockItems = arrayList;
		this.defaultDrawable = drawable;
		this.sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
		this.packageManagerWeakReference = new WeakReference<>(packageManager);
		if (sharedPreferencesDAO != null) {
			this.isShowingAllDockIcons = sharedPreferencesDAO.getBoolean(KEY_IS_SHOWING_ALL_DOCK_ICONS,false);
		}
		if (context == null) {
			return;
		}
		if (Build.VERSION.SDK_INT >= 25 && (launcherApps = (LauncherApps) context.getSystemService("launcherapps")) != null) {
			Iterator<ImageView> it = arrayList.iterator();
			while (it.hasNext()) {
				it.next().setOnCreateContextMenuListener(new DockOnCreateContextMenuListener(launcherApps));
			}
		}
		Iterator<ImageView> it2 = arrayList.iterator();
		while (it2.hasNext()) {
			it2.next()
					.setOnClickListener(
							new View.OnClickListener() { // from class: de.clemensbartz.android.launcher.controllers.DockController.1
								@Override // android.view.View.OnClickListener
								public void onClick(View view) {
									if ((view instanceof ImageView) && (view.getTag() instanceof ApplicationModel)) {
										ApplicationModel applicationModel = (ApplicationModel) view.getTag();
										if (applicationModel.packageName == null || applicationModel.className == null) {
											return;
										}
										Intent newAppMainIntent = IntentUtil.newAppMainIntent(applicationModel.packageName,
												applicationModel.className);
										if (IntentUtil.isCallable(context.getPackageManager(),newAppMainIntent)) {
											try {
												context.startActivity(newAppMainIntent);
											}
											catch (Exception e) {
												Toast.makeText(context.getApplicationContext(),"出错" + e.getMessage(),Toast.LENGTH_SHORT)
														.show();
											}
										}
									}
								}
							});
		}
	}

	public boolean isShowingAllDockIcons() {
		return this.isShowingAllDockIcons;
	}

	public void setShowingAllDockIcons(boolean z) {
		this.isShowingAllDockIcons = z;
	}

	public void updateDock(int i,ApplicationModel applicationModel) {
		if (i < 0 || i >= 7) {
			return;
		}
		if (applicationModel == null) {
			clearIndex(i);
			removeFromDatabase(i);
			return;
		}
		if (applicationModel.packageName == null || applicationModel.className == null) {
			clearIndex(i);
			removeFromDatabase(i);
			return;
		}
		PackageManager packageManager = this.packageManagerWeakReference.get();
		if (packageManager != null) {
			try {
				packageManager.getPackageInfo(applicationModel.packageName,1);
				if (IntentUtil.isCallable(packageManager,
						IntentUtil.newAppMainIntent(applicationModel.packageName,applicationModel.className))) {
					insertNewItem(i,applicationModel);
				} else {
					clearIndex(i);
					removeFromDatabase(i);
				}
			}
			catch (PackageManager.NameNotFoundException unused) {
				clearIndex(i);
				removeFromDatabase(i);
			}
		}
	}

	private void insertNewItem(int i,ApplicationModel applicationModel) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (sharedPreferencesDAO != null) {
			sharedPreferencesDAO.putString(getKey(i),applicationModel.packageName + SEPARATOR + applicationModel.className);
		}
		ImageView imageView = this.dockItems.get(i);
		imageView.setTag(applicationModel);
		imageView.setContentDescription(applicationModel.label);
		// 无需在设置图标加载器了
		// SimpleIconLoader.loadIcon(applicationModel, imageView, this.defaultDrawable);
	}

	public void clearIndex(int i) {
		ImageView imageView = this.dockItems.get(i);
		imageView.setTag(null);
		imageView.setImageDrawable(this.defaultDrawable);
	}

	private void removeFromDatabase(int i) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (sharedPreferencesDAO != null) {
			sharedPreferencesDAO.remove(getKey(i));
		}
	}

	private String getKey(int i) {
		return PIN_PREFIX + i;
	}

	public void updateVisibility(Configuration configuration) {
		if (configuration == null) {
			return;
		}
		boolean z = configuration.densityDpi >= DENSITY_XXXHIGH;
		boolean z2 = configuration.orientation == 1;
		boolean z3 = (configuration.screenLayout & 15) > 3;
		if (z || z3 || !z2 || this.isShowingAllDockIcons) {
			this.dockItems.get(5).setVisibility(0);
			this.dockItems.get(6).setVisibility(0);
		} else {
			this.dockItems.get(5).setVisibility(8);
			this.dockItems.get(6).setVisibility(8);
		}
	}
}
