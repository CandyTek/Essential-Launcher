package de.clemensbartz.android.launcher.listeners;

import android.view.View;
import android.widget.ListView;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class SectionLabelOnClickListener implements View.OnClickListener {
    private final WeakReference<ListView> listViewWeakReference;
    private final int position;

    public SectionLabelOnClickListener(ListView listView, int i) {
        this.listViewWeakReference = new WeakReference<>(listView);
        this.position = i;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ListView listView = this.listViewWeakReference.get();
        if (listView != null) {
            listView.smoothScrollToPosition(this.position);
        }
    }
}
