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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ViewFlipper;


import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.controllers.ViewController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.listeners.AbsListViewOnCreateContextMenuListener;
import de.clemensbartz.android.launcher.listeners.AdapterViewOnItemClickListener;
import de.clemensbartz.android.launcher.listeners.SearchViewOnActionExpandListener;
import de.clemensbartz.android.launcher.observers.LinearLayoutSectionsObserver;
import de.clemensbartz.android.launcher.receivers.PackageChangedBroadcastReceiver;
import de.clemensbartz.android.launcher.tasks.FilterDrawerListAdapterTask;
import de.clemensbartz.android.launcher.tasks.LoadDrawerListAdapterTask;
import de.clemensbartz.android.launcher.tasks.LoadSharedPreferencesDAOTask;
import de.clemensbartz.android.launcher.util.IntentUtil;
import de.clemensbartz.android.launcher.util.StrictModeUtil;
import de.clemensbartz.android.launcher.util.ThemeUtil;

/* loaded from: classes.dex */
public final class Launcher extends Activity {
	private SharedPreferencesDAO sharedPreferencesDAO = null;
	private DrawerController drawerController = null;
	private ViewController viewController = null;
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
		if (Build.VERSION.SDK_INT >= 21) {
			drawable = getDrawable(R.drawable.ic_launcher);
		} else {
			drawable = getResources().getDrawable(R.drawable.ic_launcher);
		}
		if (drawable == null) {
			throw new NullPointerException("Could not load ic_launcher drawable.");
		}
		this.drawerListAdapter = new DrawerListAdapter(this,drawable);
		this.drawerController = new DrawerController(this.drawerListAdapter,this.sharedPreferencesDAO);
		new LinearLayoutSectionsObserver(this,actionBarHeight,(ListView) findViewById(R.id.lvApplications),
				(LinearLayout) findViewById(R.id.lvApplicationsSections),this.drawerListAdapter);
		ListView listView = (ListView) findViewById(R.id.lvApplications);
		AbsListView gridView = (AbsListView) findViewById(R.id.gvApplications);
		adjustActionBarOffset(listView,actionBarHeight);
		registerForContextMenu(listView);
		listView.setAdapter((ListAdapter) this.drawerListAdapter);
		listView.setOnItemClickListener(new AdapterViewOnItemClickListener(this));
		listView.setOnCreateContextMenuListener(
				new AbsListViewOnCreateContextMenuListener(getPackageManager(),this.drawerController,this.drawerListAdapter,
						null,this));
		adjustActionBarOffset(gridView,actionBarHeight);
		registerForContextMenu(gridView);
		gridView.setAdapter((ListAdapter) this.drawerListAdapter);
		gridView.setOnItemClickListener(new AdapterViewOnItemClickListener(this));
		gridView.setOnCreateContextMenuListener(
				new AbsListViewOnCreateContextMenuListener(getPackageManager(),this.drawerController,this.drawerListAdapter,
						null,this));
		adjustActionBarOffset(findViewById(R.id.lvApplicationsSections),actionBarHeight);
	}

	@Override // android.app.Activity
	protected void onStart() {
		super.onStart();
		new LoadSharedPreferencesDAOTask(this,this.sharedPreferencesDAO,this.viewController,null).execute(new Integer[0]);
		PackageChangedBroadcastReceiver packageChangedBroadcastReceiver = PackageChangedBroadcastReceiver.getInstance();
		packageChangedBroadcastReceiver.setDockController(null);
		packageChangedBroadcastReceiver.setDrawerController(this.drawerController);
		packageChangedBroadcastReceiver.setDrawerListAdapter(this.drawerListAdapter);
		packageChangedBroadcastReceiver.setSharedPreferencesDAO(this.sharedPreferencesDAO);
		registerReceiver(packageChangedBroadcastReceiver,IntentUtil.createdChangeBroadReceiverFilter());
		if (LoadDrawerListAdapterTask.getRunningTask() != null) {
			LoadDrawerListAdapterTask.getRunningTask().cancel(true);
		}
		LoadDrawerListAdapterTask loadDrawerListAdapterTask = new LoadDrawerListAdapterTask(this,this.drawerController,
				this.drawerListAdapter);
		LoadDrawerListAdapterTask.setRunningTask(loadDrawerListAdapterTask);
		loadDrawerListAdapterTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,new Integer[0]);
	}

	@Override // android.app.Activity
	public void onBackPressed() {
		finish();
	}

	@Override // android.app.Activity, android.content.ComponentCallbacks
	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);
	}

	@Override // android.app.Activity
	protected void onResume() {
		super.onResume();
		ViewController viewController = this.viewController;
		if (viewController != null) {
			viewController.setActionBar(getActionBar());
			this.viewController.showDetail();
		}
	}

	@Override // android.app.Activity
	protected void onDestroy() {
		try {
			unregisterReceiver(PackageChangedBroadcastReceiver.getInstance());
		}
		catch (Exception unused) {
		}
		super.onDestroy();
	}

	@Override // android.app.Activity
	public boolean onTouchEvent(MotionEvent motionEvent) {
		return super.onTouchEvent(motionEvent);
	}


	@Override // android.app.Activity
	public boolean onContextItemSelected(MenuItem menuItem) {
		if (menuItem != null && menuItem.getIntent() != null && IntentUtil.isCallable(getPackageManager(),menuItem.getIntent())) {
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
		getMenuInflater().inflate(R.menu.actionbar_options_menu,menu);
		menu.findItem(R.id.abm_show_all_dock_icons).setVisible(false);
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
			menu.findItem(R.id.abm_grid_toggle).setChecked(this.viewController.getCurrentDetailIndex() == ViewController.GRID_ID);
		}
		return true;
	}

	@Override // android.app.Activity
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem == null) {
			return false;
		}
		switch (menuItem.getItemId()) {
			case R.id.abm_grid_toggle /* 2130837505 */:
				ViewController viewController2 = this.viewController;
				if (viewController2 == null || this.sharedPreferencesDAO == null) {
					return super.onOptionsItemSelected(menuItem);
				}
				boolean z = viewController2.getCurrentDetailIndex() == ViewController.GRID_ID;
				menuItem.setChecked(!z);
				int i = !z ? ViewController.GRID_ID : ViewController.LIST_ID;
				this.viewController.setCurrentDetailIndex(i);
				this.sharedPreferencesDAO.putInt(ViewController.KEY_DRAWER_LAYOUT,i);
				this.viewController.showDetail();
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

	private void adjustActionBarOffset(View view,int i) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
			marginLayoutParams.setMargins(marginLayoutParams.leftMargin,i + marginLayoutParams.topMargin,marginLayoutParams.rightMargin,
					marginLayoutParams.bottomMargin);
		} else {
			view.setPadding(view.getPaddingLeft(),i + view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());
		}
	}
}
