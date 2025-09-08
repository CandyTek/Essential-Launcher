package de.clemensbartz.android.launcher.listeners;

import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes.dex */
public final class UpOnTouchListener implements View.OnTouchListener {
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 1 || view == null) {
            return false;
        }
        view.performClick();
        return true;
    }
}
