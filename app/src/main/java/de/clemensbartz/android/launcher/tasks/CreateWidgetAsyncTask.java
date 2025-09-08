package de.clemensbartz.android.launcher.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.controllers.WidgetController;

/* loaded from: classes.dex */
public final class CreateWidgetAsyncTask extends AsyncTask<Integer,Integer,Integer> {
	private final WeakReference<WidgetController> widgetControllerWeakReference;

	public CreateWidgetAsyncTask(WidgetController widgetController) {
		this.widgetControllerWeakReference = new WeakReference<>(widgetController);
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public Integer doInBackground(Integer... numArr) {
		if (numArr == null || numArr.length < 1) {
			return null;
		}
		return numArr[0];
	}

	/* JADX INFO: Access modifiers changed from: protected */
	@Override // android.os.AsyncTask
	public void onPostExecute(Integer num) {
		WidgetController widgetController;
		if (num == null || (widgetController = this.widgetControllerWeakReference.get()) == null) {
			return;
		}
		widgetController.createWidget(num.intValue());
	}
}
