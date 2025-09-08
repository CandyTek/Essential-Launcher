package de.clemensbartz.android.launcher.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.controllers.WidgetController;

/* loaded from: classes.dex */
public final class IntentUtil {
	private IntentUtil() {
	}

	public static Intent newAppDetailsIntent(String str) {
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setFlags(268435456);
		intent.setData(Uri.parse("package:" + str));
		return intent;
	}

	public static Intent newAppMainIntent(String str,String str2) {
		ComponentName componentName = new ComponentName(str,str2);
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setFlags(268435456);
		intent.setComponent(componentName);
		return intent;
	}

	public static Intent createWidgetConfigureIntent(ComponentName componentName) {
		Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
		intent.setComponent(componentName);
		return intent;
	}

	public static Intent createWidgetBindIntent(ComponentName componentName,int i) {
		Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
		intent.putExtra(WidgetController.KEY_APPWIDGET_ID,i);
		intent.putExtra("appWidgetProvider",componentName);
		Bundle bundle = new Bundle();
		bundle.putInt("appWidgetCategory",1);
		intent.putExtra("appWidgetOptions",bundle);
		return intent;
	}

	public static IntentFilter createdChangeBroadReceiverFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
		intentFilter.addAction("android.intent.action.INSTALL_PACKAGE");
		intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
		intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
		intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
		intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
		intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
		intentFilter.addDataScheme("package");
		return intentFilter;
	}

	public static boolean isCallable(PackageManager packageManager,Intent intent) {
		if (packageManager != null && intent != null) {
			for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent,65536)) {
				if (resolveInfo.activityInfo.exported && (resolveInfo.activityInfo.permission == null || packageManager.checkPermission(
						resolveInfo.activityInfo.permission,BuildConfig.APPLICATION_ID) == 0)) {
					return true;
				}
			}
		}
		return false;
	}
}
