package de.clemensbartz.android.launcher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.tasks.LoadDockTask;
import de.clemensbartz.android.launcher.tasks.LoadDrawerListAdapterTask;

/* loaded from: classes.dex */
public final class PackageChangedBroadcastReceiver extends BroadcastReceiver {
	private static PackageChangedBroadcastReceiver instance;
	private WeakReference<DockController> dockControllerWeakReference = new WeakReference<>(null);
	private WeakReference<DrawerController> drawerControllerWeakReference = new WeakReference<>(null);
	private WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference = new WeakReference<>(null);
	private WeakReference<DrawerListAdapter> drawerListAdapterWeakReference = new WeakReference<>(null);

	private PackageChangedBroadcastReceiver() {
	}

	public static PackageChangedBroadcastReceiver getInstance() {
		if (instance == null) {
			instance = new PackageChangedBroadcastReceiver();
		}
		return instance;
	}

	@Override // android.content.BroadcastReceiver
	public void onReceive(Context context,Intent intent) {
		DockController dockController = this.dockControllerWeakReference.get();
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (dockController != null && sharedPreferencesDAO != null) {
			if (LoadDockTask.getRunningTask() != null) {
				LoadDockTask.getRunningTask().cancel(true);
			}
			LoadDockTask loadDockTask = new LoadDockTask(sharedPreferencesDAO,dockController);
			LoadDockTask.setRunningTask(loadDockTask);
			loadDockTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,new Integer[0]);
		}
		DrawerController drawerController = this.drawerControllerWeakReference.get();
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		if (drawerController == null || drawerListAdapter == null || context == null) {
			return;
		}
		if (LoadDrawerListAdapterTask.getRunningTask() != null) {
			LoadDrawerListAdapterTask.getRunningTask().cancel(true);
		}
		LoadDrawerListAdapterTask loadDrawerListAdapterTask = new LoadDrawerListAdapterTask(context,drawerController,drawerListAdapter);
		LoadDrawerListAdapterTask.setRunningTask(loadDrawerListAdapterTask);
		loadDrawerListAdapterTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,new Integer[0]);
	}

	public void setDockController(DockController dockController) {
		this.dockControllerWeakReference = new WeakReference<>(dockController);
	}

	public void setDrawerController(DrawerController drawerController) {
		this.drawerControllerWeakReference = new WeakReference<>(drawerController);
	}

	public void setSharedPreferencesDAO(SharedPreferencesDAO sharedPreferencesDAO) {
		this.sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
	}

	public void setDrawerListAdapter(DrawerListAdapter drawerListAdapter) {
		this.drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
	}
}
