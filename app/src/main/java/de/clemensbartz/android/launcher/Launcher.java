package de.clemensbartz.android.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ViewFlipper;
import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.controllers.ViewController;
import de.clemensbartz.android.launcher.controllers.WidgetController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.listeners.AbsListViewOnCreateContextMenuListener;
import de.clemensbartz.android.launcher.listeners.AdapterViewOnItemClickListener;
import de.clemensbartz.android.launcher.listeners.SearchViewOnActionExpandListener;
import de.clemensbartz.android.launcher.listeners.UpOnTouchListener;
import de.clemensbartz.android.launcher.observers.LinearLayoutSectionsObserver;
import de.clemensbartz.android.launcher.receivers.PackageChangedBroadcastReceiver;
import de.clemensbartz.android.launcher.tasks.FilterDrawerListAdapterTask;
import de.clemensbartz.android.launcher.tasks.LoadDockTask;
import de.clemensbartz.android.launcher.tasks.LoadDrawerListAdapterTask;
import de.clemensbartz.android.launcher.tasks.LoadSharedPreferencesDAOTask;
import de.clemensbartz.android.launcher.util.IntentUtil;
import de.clemensbartz.android.launcher.util.StrictModeUtil;
import de.clemensbartz.android.launcher.util.ThemeUtil;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class Launcher extends Activity {
    private SharedPreferencesDAO sharedPreferencesDAO = null;
    private DockController dockController = null;
    private DrawerController drawerController = null;
    private ViewController viewController = null;
    private WidgetController widgetController = null;
    private DrawerListAdapter drawerListAdapter = null;
    private Menu actionBarMenu = null;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Drawable drawable;
        ThemeUtil.setTheme(this);
        super.onCreate(bundle);
        setContentView(R.layout.launcher);
        StrictModeUtil.adjustStrictMode();
        int actionBarHeight = ThemeUtil.getActionBarHeight(this);
        this.sharedPreferencesDAO = SharedPreferencesDAO.getInstance(getPreferences(0));
        this.viewController = new ViewController((ViewFlipper) findViewById(R.id.vsLauncher));
        findViewById(R.id.up).setOnTouchListener(new UpOnTouchListener());
        if (Build.VERSION.SDK_INT < 18 || getPackageManager().hasSystemFeature("android.software.app_widgets")) {
            this.widgetController = new WidgetController(this, this.sharedPreferencesDAO);
            this.widgetController.startListening();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            drawable = getDrawable(R.drawable.ic_launcher);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_launcher);
        }
        if (drawable == null) {
            throw new NullPointerException("Could not load ic_launcher drawable.");
        }
        ArrayList arrayList = new ArrayList(7);
        arrayList.add((ImageView) findViewById(R.id.ivDock1));
        arrayList.add((ImageView) findViewById(R.id.ivDock2));
        arrayList.add((ImageView) findViewById(R.id.ivDock3));
        arrayList.add((ImageView) findViewById(R.id.ivDock4));
        arrayList.add((ImageView) findViewById(R.id.ivDock5));
        arrayList.add((ImageView) findViewById(R.id.ivDock6));
        arrayList.add((ImageView) findViewById(R.id.ivDock7));
        this.dockController = new DockController(this, getPackageManager(), this.sharedPreferencesDAO, drawable, arrayList);
        this.dockController.updateVisibility(getResources().getConfiguration());
        this.drawerListAdapter = new DrawerListAdapter(this, drawable);
        this.drawerController = new DrawerController(this.drawerListAdapter, this.sharedPreferencesDAO);
        new LinearLayoutSectionsObserver(this, actionBarHeight, (ListView) findViewById(R.id.lvApplications), (LinearLayout) findViewById(R.id.lvApplicationsSections), this.drawerListAdapter);
        for (AbsListView absListView : Arrays.asList((AbsListView) findViewById(R.id.gvApplications), (AbsListView) findViewById(R.id.lvApplications))) {
            adjustActionBarOffset(absListView, actionBarHeight);
            registerForContextMenu(absListView);
            absListView.setAdapter((ListAdapter) this.drawerListAdapter);
            absListView.setOnItemClickListener(new AdapterViewOnItemClickListener(this));
            absListView.setOnCreateContextMenuListener(new AbsListViewOnCreateContextMenuListener(getPackageManager(), this.drawerController, this.drawerListAdapter, this.dockController, this));
        }
        adjustActionBarOffset(findViewById(R.id.lvApplicationsSections), actionBarHeight);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        new LoadSharedPreferencesDAOTask(this, this.sharedPreferencesDAO, this.viewController, this.widgetController).execute(new Integer[0]);
        PackageChangedBroadcastReceiver packageChangedBroadcastReceiver = PackageChangedBroadcastReceiver.getInstance();
        packageChangedBroadcastReceiver.setDockController(this.dockController);
        packageChangedBroadcastReceiver.setDrawerController(this.drawerController);
        packageChangedBroadcastReceiver.setDrawerListAdapter(this.drawerListAdapter);
        packageChangedBroadcastReceiver.setSharedPreferencesDAO(this.sharedPreferencesDAO);
        registerReceiver(packageChangedBroadcastReceiver, IntentUtil.createdChangeBroadReceiverFilter());
        if (LoadDockTask.getRunningTask() != null) {
            LoadDockTask.getRunningTask().cancel(true);
        }
        LoadDockTask loadDockTask = new LoadDockTask(this.sharedPreferencesDAO, this.dockController);
        LoadDockTask.setRunningTask(loadDockTask);
        loadDockTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Integer[0]);
        if (LoadDrawerListAdapterTask.getRunningTask() != null) {
            LoadDrawerListAdapterTask.getRunningTask().cancel(true);
        }
        LoadDrawerListAdapterTask loadDrawerListAdapterTask = new LoadDrawerListAdapterTask(this, this.drawerController, this.drawerListAdapter);
        LoadDrawerListAdapterTask.setRunningTask(loadDrawerListAdapterTask);
        loadDrawerListAdapterTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Integer[0]);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        ViewController viewController = this.viewController;
        if (viewController != null) {
            viewController.showHome();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        DockController dockController;
        super.onConfigurationChanged(configuration);
        if (configuration == null || (dockController = this.dockController) == null) {
            return;
        }
        dockController.updateVisibility(configuration);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        ViewController viewController = this.viewController;
        if (viewController != null) {
            viewController.setActionBar(getActionBar());
            this.viewController.showHome();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        WidgetController widgetController = this.widgetController;
        if (widgetController != null) {
            widgetController.stopListening();
        }
        try {
            unregisterReceiver(PackageChangedBroadcastReceiver.getInstance());
        } catch (Exception unused) {
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 1) {
            ViewController viewController = this.viewController;
            if (viewController != null) {
                viewController.showDetail();
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        WidgetController widgetController = this.widgetController;
        if (widgetController != null) {
            widgetController.onActivityResult(i, i2, intent);
        }
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem != null && menuItem.getIntent() != null && IntentUtil.isCallable(getPackageManager(), menuItem.getIntent())) {
            startActivity(menuItem.getIntent());
            return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu == null) {
            return false;
        }
        this.actionBarMenu = menu;
        ViewController viewController = this.viewController;
        if (viewController != null) {
            viewController.setActionBarMenu(this.actionBarMenu);
        }
        getMenuInflater().inflate(R.menu.actionbar_options_menu, menu);
        WidgetController widgetController = this.widgetController;
        if (widgetController == null) {
            menu.findItem(R.id.abm_choose_widget).setVisible(false);
            menu.findItem(R.id.abm_layout_widget).setVisible(false);
            menu.findItem(R.id.abm_remove_widget).setVisible(false);
        } else {
            boolean isAppWidgetConfigured = widgetController.isAppWidgetConfigured();
            menu.findItem(R.id.abm_layout_widget).setVisible(isAppWidgetConfigured);
            menu.findItem(R.id.abm_remove_widget).setVisible(isAppWidgetConfigured);
        }
        MenuItem findItem = menu.findItem(R.id.app_bar_search);
        View actionView = findItem.getActionView();
        if (actionView instanceof SearchView) {
            ((SearchView) actionView).setOnQueryTextListener(this.drawerListAdapter);
            findItem.setOnActionExpandListener(new SearchViewOnActionExpandListener());
        }
        if (this.drawerListAdapter != null) {
            menu.findItem(R.id.abm_show_hidden).setChecked(this.drawerListAdapter.isShowingHiddenApps());
        }
        if (this.viewController != null) {
            menu.findItem(R.id.abm_grid_toggle).setChecked(this.viewController.getCurrentDetailIndex() == 1);
        }
        if (this.dockController != null) {
            menu.findItem(R.id.abm_show_all_dock_icons).setChecked(this.dockController.isShowingAllDockIcons());
        }
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == null) {
            return false;
        }
        switch (menuItem.getItemId()) {
            case R.id.abm_choose_widget /* 2130837504 */:
                WidgetController widgetController = this.widgetController;
                if (widgetController == null) {
                    menuItem.setVisible(false);
                    return true;
                }
                widgetController.requestWidgetChoosing();
                ViewController viewController = this.viewController;
                if (viewController == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                viewController.showHome();
                return true;
            case R.id.abm_grid_toggle /* 2130837505 */:
                ViewController viewController2 = this.viewController;
                if (viewController2 == null || this.sharedPreferencesDAO == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                boolean z = viewController2.getCurrentDetailIndex() == 1;
                menuItem.setChecked(!z);
                int i = !z ? 1 : 2;
                this.viewController.setCurrentDetailIndex(i);
                this.sharedPreferencesDAO.putInt(ViewController.KEY_DRAWER_LAYOUT, i);
                this.viewController.showDetail();
                return true;
            case R.id.abm_layout_widget /* 2130837506 */:
                WidgetController widgetController2 = this.widgetController;
                if (widgetController2 == null) {
                    menuItem.setVisible(false);
                    return true;
                }
                widgetController2.requestWidgetLayoutChange();
                ViewController viewController3 = this.viewController;
                if (viewController3 == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                viewController3.showHome();
                return true;
            case R.id.abm_remove_widget /* 2130837507 */:
                WidgetController widgetController3 = this.widgetController;
                if (widgetController3 == null) {
                    menuItem.setVisible(false);
                    return super.onOptionsItemSelected(menuItem);
                }
                widgetController3.requestWidgetRemoval();
                ViewController viewController4 = this.viewController;
                if (viewController4 == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                viewController4.showHome();
                return true;
            case R.id.abm_show_all_dock_icons /* 2130837508 */:
                DockController dockController = this.dockController;
                if (dockController == null || this.sharedPreferencesDAO == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                boolean isShowingAllDockIcons = dockController.isShowingAllDockIcons();
                menuItem.setChecked(!isShowingAllDockIcons);
                this.sharedPreferencesDAO.putBoolean(DockController.KEY_IS_SHOWING_ALL_DOCK_ICONS, !isShowingAllDockIcons);
                this.dockController.setShowingAllDockIcons(!isShowingAllDockIcons);
                this.dockController.updateVisibility(getResources().getConfiguration());
                return true;
            case R.id.abm_show_hidden /* 2130837509 */:
                DrawerListAdapter drawerListAdapter = this.drawerListAdapter;
                if (drawerListAdapter == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                boolean isShowingHiddenApps = drawerListAdapter.isShowingHiddenApps();
                this.drawerListAdapter.setShowHiddenApps(!isShowingHiddenApps);
                menuItem.setChecked(!isShowingHiddenApps);
                new FilterDrawerListAdapterTask(this.drawerListAdapter).execute(new Integer[0]);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public MenuItem getActionBarMenuItem(int i) {
        Menu menu = this.actionBarMenu;
        if (menu == null) {
            return null;
        }
        return menu.findItem(i);
    }

    private void adjustActionBarOffset(View view, int i) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, i + marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        } else {
            view.setPadding(view.getPaddingLeft(), i + view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }
}
