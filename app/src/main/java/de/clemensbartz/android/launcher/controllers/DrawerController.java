package de.clemensbartz.android.launcher.controllers;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.tasks.FilterDrawerListAdapterTask;

/* loaded from: classes.dex */
public final class DrawerController {
	private static final String HIDE_PREFIX = "hide_";
	private static final String SEPARATOR = "|";
	private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;
	private final WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;

	public DrawerController(DrawerListAdapter drawerListAdapter,SharedPreferencesDAO sharedPreferencesDAO) {
		this.drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
		this.sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
	}

	public void toggleHide(ApplicationModel applicationModel) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (sharedPreferencesDAO == null || applicationModel.packageName == null || applicationModel.className == null) {
			return;
		}
		String key = getKey(applicationModel.packageName,applicationModel.className);
		applicationModel.hidden = !isHiding(applicationModel);
		if (isHiding(applicationModel)) {
			sharedPreferencesDAO.remove(key);
		} else {
			sharedPreferencesDAO.putString(key,"|");
		}
		DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
		if (drawerListAdapter != null) {
			new FilterDrawerListAdapterTask(drawerListAdapter).execute(new Integer[0]);
		}
	}

	public boolean isHiding(ApplicationModel applicationModel) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (sharedPreferencesDAO == null || applicationModel.packageName == null || applicationModel.className == null) {
			return false;
		}
		return sharedPreferencesDAO.contains(getKey(applicationModel.packageName,applicationModel.className));
	}

	private String getKey(String str,String str2) {
		return HIDE_PREFIX + str + "|" + str2;
	}
}
