package de.clemensbartz.android.launcher.tasks;

import android.os.AsyncTask;
import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class FilterDrawerListAdapterTask extends AsyncTask<Integer, Integer, Integer> {
    private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Integer doInBackground(Integer... numArr) {
        return null;
    }

    public FilterDrawerListAdapterTask(DrawerListAdapter drawerListAdapter) {
        this.drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Integer num) {
        DrawerListAdapter drawerListAdapter = this.drawerListAdapterWeakReference.get();
        if (drawerListAdapter != null) {
            drawerListAdapter.filter();
        }
    }
}
