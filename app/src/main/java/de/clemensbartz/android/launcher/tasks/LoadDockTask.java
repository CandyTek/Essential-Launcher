package de.clemensbartz.android.launcher.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.models.ApplicationModel;

/* loaded from: classes.dex */
public final class LoadDockTask extends AsyncTask<Integer,LoadDockTask.LoadDockTaskProgress,Integer> {
	private static LoadDockTask runningTask;
	private final WeakReference<DockController> dockControllerWeakReference;
	private final WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;

	public LoadDockTask(SharedPreferencesDAO sharedPreferencesDAO,DockController dockController) {
		this.sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
		this.dockControllerWeakReference = new WeakReference<>(dockController);
	}

	public static LoadDockTask getRunningTask() {
		return runningTask;
	}

	public static void setRunningTask(LoadDockTask loadDockTask) {
		runningTask = loadDockTask;
	}

	@Override // android.os.AsyncTask
	protected void onPreExecute() {
		DockController dockController = this.dockControllerWeakReference.get();
		if (dockController != null) {
			for (int i = 0;i < 7;i++) {
				dockController.clearIndex(i);
			}
		}
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public Integer doInBackground(Integer... numArr) {
		SharedPreferencesDAO sharedPreferencesDAO = this.sharedPreferencesDAOWeakReference.get();
		if (sharedPreferencesDAO == null) {
			return null;
		}
		for (int i = 0;i < 7 && !isCancelled();i++) {
			String string = sharedPreferencesDAO.getString(DockController.PIN_PREFIX + i,BuildConfig.FLAVOR);
			if (string.length() > 0) {
				StringTokenizer stringTokenizer = new StringTokenizer(string,DockController.SEPARATOR);
				if (stringTokenizer.countTokens() == 2) {
					ApplicationModel applicationModel = new ApplicationModel();
					applicationModel.packageName = stringTokenizer.nextToken();
					applicationModel.className = stringTokenizer.nextToken();
					LoadDockTaskProgress loadDockTaskProgress = new LoadDockTaskProgress();
					loadDockTaskProgress.index = i;
					ApplicationModel.setPinyinFirst(applicationModel);
					loadDockTaskProgress.applicationModel = applicationModel;
					publishProgress(loadDockTaskProgress);
				}
			}
		}
		return null;
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onProgressUpdate(LoadDockTaskProgress... loadDockTaskProgressArr) {
		DockController dockController = this.dockControllerWeakReference.get();
		if (dockController != null) {
			for (LoadDockTaskProgress loadDockTaskProgress : loadDockTaskProgressArr) {
				dockController.updateDock(loadDockTaskProgress.index,loadDockTaskProgress.applicationModel);
			}
		}
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onPostExecute(Integer num) {
		setRunningTask(null);
	}

	static final class LoadDockTaskProgress {
		ApplicationModel applicationModel;
		int index;

		LoadDockTaskProgress() {
		}
	}
}
