package de.clemensbartz.android.launcher.listeners;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.IntentUtil;

/* loaded from: classes.dex */
public final class AdapterViewOnItemClickListener implements AdapterView.OnItemClickListener {
	private final Context context;

	public AdapterViewOnItemClickListener(Context context) {
		this.context = context;
	}

	/* JADX WARN: Type inference failed for: r1v1, types: [android.widget.Adapter] */
	@Override // android.widget.AdapterView.OnItemClickListener
	public void onItemClick(AdapterView<?> adapterView,View view,int i,long j) {
		if (adapterView == null) {
			return;
		}
		Object item = adapterView.getAdapter().getItem(i);
		if (item instanceof ApplicationModel) {
			ApplicationModel applicationModel = (ApplicationModel) item;
			if (applicationModel.packageName == null || applicationModel.className == null) {
				return;
			}
			ComponentName componentName = new ComponentName(applicationModel.packageName,applicationModel.className);
			Intent intent = new Intent("android.intent.action.MAIN");
			intent.setFlags(268435456);
			intent.setComponent(componentName);
			if (IntentUtil.isCallable(this.context.getPackageManager(),intent)) {
				try {
					this.context.startActivity(intent);
				}
				catch (Exception e) {
					Toast.makeText(context.getApplicationContext(),"出错"+e.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
