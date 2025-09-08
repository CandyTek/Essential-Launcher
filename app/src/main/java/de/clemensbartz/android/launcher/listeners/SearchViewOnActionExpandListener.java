package de.clemensbartz.android.launcher.listeners;

import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import de.clemensbartz.android.launcher.BuildConfig;

/* loaded from: classes.dex */
public final class SearchViewOnActionExpandListener implements MenuItem.OnActionExpandListener {
    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        View actionView = menuItem.getActionView();
        if (actionView instanceof SearchView) {
            ((SearchView) actionView).setQuery(BuildConfig.FLAVOR, true);
        }
        return true;
    }
}
