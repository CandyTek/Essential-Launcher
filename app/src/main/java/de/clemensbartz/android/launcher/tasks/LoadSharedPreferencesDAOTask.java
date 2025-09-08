package de.clemensbartz.android.launcher.tasks;

import android.os.AsyncTask;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.Launcher;
import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.controllers.ViewController;
import de.clemensbartz.android.launcher.controllers.WidgetController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;

/* loaded from: classes.dex */
public final class LoadSharedPreferencesDAOTask extends AsyncTask<Integer,Integer,LoadSharedPreferencesDAOTask.LoadModelAsyncTaskResult> {
	private final WeakReference<Launcher> launcherWeakReference;
	private final WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;
	private final WeakReference<ViewController> viewControllerWeakReference;
	private final WeakReference<WidgetController> widgetControllerWeakReference;

	public LoadSharedPreferencesDAOTask(Launcher launcher,SharedPreferencesDAO sharedPreferencesDAO,ViewController viewController,WidgetController widgetController) {
		this.viewControllerWeakReference = new WeakReference<>(viewController);
		this.sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
		this.launcherWeakReference = new WeakReference<>(launcher);
		this.widgetControllerWeakReference = new WeakReference<>(widgetController);
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public LoadModelAsyncTaskResult doInBackground(Integer... numArr) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (isCancelled() || sharedPreferencesDAO == null) {
			return null;
		}
		sharedPreferencesDAO.loadValues();
		LoadModelAsyncTaskResult loadModelAsyncTaskResult = new LoadModelAsyncTaskResult();
		loadModelAsyncTaskResult.selectedWidget = sharedPreferencesDAO.getInt(WidgetController.KEY_APPWIDGET_ID,-1);
		loadModelAsyncTaskResult.widgetLayout = sharedPreferencesDAO.getInt(WidgetController.KEY_APPWIDGET_LAYOUT,-1);
		loadModelAsyncTaskResult.drawerLayout = sharedPreferencesDAO.getInt(ViewController.KEY_DRAWER_LAYOUT,1);
		return loadModelAsyncTaskResult;
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onPostExecute(LoadModelAsyncTaskResult loadModelAsyncTaskResult) {
		MenuItem actionBarMenuItem;
		if (loadModelAsyncTaskResult == null) {
			return;
		}
		ViewController viewController = this.viewControllerWeakReference.get();
		if (viewController != null) {
			viewController.setCurrentDetailIndex(loadModelAsyncTaskResult.drawerLayout);
		}
		// Launcher launcher = this.launcherWeakReference.get();
		// if (launcher != null && (actionBarMenuItem = launcher.getActionBarMenuItem(R.id.abm_grid_toggle)) != null) {
		// 	actionBarMenuItem.setChecked(loadModelAsyncTaskResult.drawerLayout == 1);
		// }
		WidgetController widgetController = this.widgetControllerWeakReference.get();
		if (widgetController != null) {
			if (loadModelAsyncTaskResult.selectedWidget > -1) {
				widgetController.addHostView(loadModelAsyncTaskResult.selectedWidget);
			}
			widgetController.adjustWidget(loadModelAsyncTaskResult.widgetLayout);
		}
	}

	static final class LoadModelAsyncTaskResult {
		int drawerLayout;
		int selectedWidget;
		int widgetLayout;

		LoadModelAsyncTaskResult() {
		}
	}
}
