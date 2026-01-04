package de.clemensbartz.android.launcher.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.comparators.ApplicationModelComparator;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.models.ApplicationModel;

/* loaded from: classes.dex */
public final class LoadDrawerListAdapterTask extends AsyncTask<Integer,Integer,Integer> {
	private static LoadDrawerListAdapterTask runningTask;
	private final WeakReference<Context> contextWeakReference;
	private final WeakReference<DrawerController> drawerControllerWeakReference;
	private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;

	public LoadDrawerListAdapterTask(Context context,DrawerController drawerController,DrawerListAdapter drawerListAdapter) {
		this.contextWeakReference = new WeakReference<>(context);
		this.drawerControllerWeakReference = new WeakReference<>(drawerController);
		this.drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
	}

	public static LoadDrawerListAdapterTask getRunningTask() {
		return runningTask;
	}

	public static void setRunningTask(LoadDrawerListAdapterTask loadDrawerListAdapterTask) {
		runningTask = loadDrawerListAdapterTask;
	}

	@Override // android.os.AsyncTask
	protected void onPreExecute() {
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		if (drawerListAdapter != null) {
			drawerListAdapter.clear();
			drawerListAdapter.filter();
		}
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public Integer doInBackground(Integer... numArr) {
		Context context = this.contextWeakReference.get();
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		DrawerController drawerController = this.drawerControllerWeakReference.get();
		if (context == null || drawerListAdapter == null || drawerController == null) {
			return null;
		}
		if (Build.VERSION.SDK_INT >= 21) {
			LauncherApps launcherApps = (LauncherApps) context.getSystemService("launcherapps");
			if (launcherApps == null) {
				return null;
			}
			drawerListAdapter.addAll(getApplicationModelsByLauncherApps(launcherApps,context.getPackageManager(),drawerController));
		} else {
			drawerListAdapter.addAll(getApplicationModelByResolveInfos(context.getPackageManager(),drawerController));
		}
		if (isCancelled()) {
			return null;
		}
		drawerListAdapter.sort(new ApplicationModelComparator(context));
		return 0;
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onPostExecute(Integer num) {
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		if (num == null || num.intValue() <= -1 || drawerListAdapter == null) {
			return;
		}
		new FilterDrawerListAdapterTask(drawerListAdapter).execute(new Integer[0]);
	}

	private List<ApplicationModel> getApplicationModelsByLauncherApps(LauncherApps launcherApps,PackageManager packageManager,DrawerController drawerController) {
		if (Process.myUserHandle() == null) {
			return new ArrayList();
		}
		ArrayList arrayList = new ArrayList();
		for (LauncherActivityInfo launcherActivityInfo : launcherApps.getActivityList(null,Process.myUserHandle())) {
			if (isCancelled()) {
				return new ArrayList();
			}
			if (launcherActivityInfo.getComponentName() != null && launcherActivityInfo.getComponentName()
					.getClassName() != null && launcherActivityInfo.getComponentName()
					.getPackageName() != null && !BuildConfig.APPLICATION_ID.equals(launcherActivityInfo.getComponentName()
					.getPackageName()) && (Build.VERSION.SDK_INT < 29 || (launcherApps.isActivityEnabled(
					launcherActivityInfo.getComponentName(),Process.myUserHandle()) && launcherApps.isPackageEnabled(
					launcherActivityInfo.getComponentName().getPackageName(),Process.myUserHandle())))) {
				ApplicationInfo applicationInfo = launcherActivityInfo.getApplicationInfo();
				if (applicationInfo != null && applicationInfo.enabled) {
					ApplicationModel applicationModel = new ApplicationModel();
					applicationModel.className = launcherActivityInfo.getComponentName().getClassName();
					applicationModel.packageName = launcherActivityInfo.getComponentName().getPackageName();
					applicationModel.label = getLabel(launcherActivityInfo.getLabel(),launcherActivityInfo.getName());
					applicationModel.hidden = drawerController.isHiding(applicationModel);
                    ApplicationModel.setPinyinFirst(applicationModel);
					arrayList.add(applicationModel);
				}
			}
		}
		return arrayList;
	}

	private List<ApplicationModel> getApplicationModelByResolveInfos(PackageManager packageManager,DrawerController drawerController) {
		ArrayList arrayList = new ArrayList();
		for (ResolveInfo resolveInfo : getLaunchableResolveInfos(packageManager)) {
			if (isCancelled()) {
				return new ArrayList();
			}
			if (resolveInfo.activityInfo.exported && resolveInfo.activityInfo.packageName != null && resolveInfo.activityInfo.name != null) {
				ApplicationModel applicationModel = new ApplicationModel();
				applicationModel.packageName = resolveInfo.activityInfo.packageName;
				applicationModel.className = resolveInfo.activityInfo.name;
				applicationModel.label = getLabel(resolveInfo.loadLabel(packageManager),resolveInfo.activityInfo.name);
				applicationModel.hidden = drawerController.isHiding(applicationModel);
                ApplicationModel.setPinyinFirst(applicationModel);
				arrayList.add(applicationModel);
			}
		}
		return arrayList;
	}

	private List<ResolveInfo> getLaunchableResolveInfos(PackageManager packageManager) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		return packageManager.queryIntentActivities(intent,0);
	}

	private String getLabel(CharSequence charSequence,String str) {
		if (charSequence != null) {
			return charSequence.toString();
		}
		return str != null ? str : BuildConfig.FLAVOR;
	}
}
