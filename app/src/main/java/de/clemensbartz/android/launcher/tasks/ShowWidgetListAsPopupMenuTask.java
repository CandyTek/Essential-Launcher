package de.clemensbartz.android.launcher.tasks;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.widget.PopupMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.controllers.WidgetController;
import de.clemensbartz.android.launcher.util.IntentUtil;

/* loaded from: classes.dex */
public final class ShowWidgetListAsPopupMenuTask extends AsyncTask<Integer,Integer,List<ShowWidgetListAsPopupMenuTask.FilledAppWidgetProviderInfo>> {
	private final WeakReference<AppWidgetManager> appWidgetManagerWeakReference;
	private final WeakReference<Context> contextWeakReference;
	private final WeakReference<WidgetController> widgetControllerWeakReference;

	public ShowWidgetListAsPopupMenuTask(WidgetController widgetController,Context context,AppWidgetManager appWidgetManager) {
		this.widgetControllerWeakReference = new WeakReference<>(widgetController);
		this.contextWeakReference = new WeakReference<>(context);
		this.appWidgetManagerWeakReference = new WeakReference<>(appWidgetManager);
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public List<FilledAppWidgetProviderInfo> doInBackground(Integer... numArr) {
		AppWidgetManager appWidgetManager = this.appWidgetManagerWeakReference.get();
		WidgetController widgetController = this.widgetControllerWeakReference.get();
		Context context = this.contextWeakReference.get();
		if (appWidgetManager == null || widgetController == null || context == null) {
			return null;
		}
		List<AppWidgetProviderInfo> installedProviders = appWidgetManager.getInstalledProviders();
		ArrayList arrayList = new ArrayList(installedProviders.size());
		for (AppWidgetProviderInfo appWidgetProviderInfo : installedProviders) {
			if (appWidgetProviderInfo.configure != null) {
				if (!IntentUtil.isCallable(context.getPackageManager(),
						IntentUtil.createWidgetConfigureIntent(appWidgetProviderInfo.configure))) {
				}
			}
			FilledAppWidgetProviderInfo filledAppWidgetProviderInfo = new FilledAppWidgetProviderInfo();
			if (Build.VERSION.SDK_INT >= 21) {
				filledAppWidgetProviderInfo.label = appWidgetProviderInfo.loadLabel(context.getPackageManager());
			} else {
				filledAppWidgetProviderInfo.label = appWidgetProviderInfo.label;
			}
			filledAppWidgetProviderInfo.provider = appWidgetProviderInfo.provider;
			filledAppWidgetProviderInfo.configure = appWidgetProviderInfo.configure;
			arrayList.add(filledAppWidgetProviderInfo);
		}
		Collections.sort(arrayList,
				new Comparator<FilledAppWidgetProviderInfo>() { // from class: de.clemensbartz.android.launcher.tasks.ShowWidgetListAsPopupMenuTask.1
					@Override // java.util.Comparator
					public int compare(FilledAppWidgetProviderInfo filledAppWidgetProviderInfo2,FilledAppWidgetProviderInfo filledAppWidgetProviderInfo3) {
						String str = BuildConfig.FLAVOR;
						String str2 = (filledAppWidgetProviderInfo2 == null || filledAppWidgetProviderInfo2.label == null) ? BuildConfig.FLAVOR : filledAppWidgetProviderInfo2.label;
						if (filledAppWidgetProviderInfo3 != null && filledAppWidgetProviderInfo3.label != null) {
							str = filledAppWidgetProviderInfo3.label;
						}
						return str2.compareTo(str);
					}
				});
		return arrayList;
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onPostExecute(List<FilledAppWidgetProviderInfo> list) {
		Context context = this.contextWeakReference.get();
		final WidgetController widgetController = this.widgetControllerWeakReference.get();
		if (list == null || context == null || widgetController == null || list.size() <= 0) {
			return;
		}
		PopupMenu popupMenu = new PopupMenu(context,widgetController.getTopFiller());
		for (FilledAppWidgetProviderInfo filledAppWidgetProviderInfo : list) {
			MenuItem add = popupMenu.getMenu().add(filledAppWidgetProviderInfo.label);
			Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
			intent.setComponent(filledAppWidgetProviderInfo.configure);
			intent.putExtra("appWidgetProvider",filledAppWidgetProviderInfo.provider);
			add.setIntent(intent);
			add.setOnMenuItemClickListener(
					new MenuItem.OnMenuItemClickListener() { // from class: de.clemensbartz.android.launcher.tasks.ShowWidgetListAsPopupMenuTask.2
						@Override // android.view.MenuItem.OnMenuItemClickListener
						public boolean onMenuItemClick(MenuItem menuItem) {
							if (menuItem == null || menuItem.getIntent() == null) {
								return false;
							}
							ComponentName componentName = (ComponentName) menuItem.getIntent().getParcelableExtra("appWidgetProvider");
							if (componentName == null) {
								return true;
							}
							widgetController.bindWidget(componentName,menuItem.getIntent().getComponent());
							return true;
						}
					});
		}
		popupMenu.show();
	}

	static final class FilledAppWidgetProviderInfo {
		ComponentName configure;
		String label;
		ComponentName provider;

		FilledAppWidgetProviderInfo() {
		}
	}
}
