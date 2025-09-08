package de.clemensbartz.android.launcher.controllers;

import android.app.ActionBar;
import android.view.Menu;
import android.widget.ViewFlipper;

import de.clemensbartz.android.launcher.R;

/* loaded from: classes.dex */
public final class ViewController {
	public static final int GRID_ID = 1;
	private static final int HOME_ID = 0;
	public static final String KEY_DRAWER_LAYOUT = "drawerLayout";
	public static final int LIST_ID = 2;
	private ActionBar actionBar;
	private Menu actionBarMenu;
	private int currentDetailIndex = HOME_ID;
	private final ViewFlipper viewFlipper;

	public ViewController(ViewFlipper viewFlipper) {
		this.viewFlipper = viewFlipper;
	}

	private boolean isValidDetailIndex(int i) {
		return i >= 0 && this.viewFlipper.getChildCount() > i - 1;
	}

	private void switchTo(int i) {
		if (i == 0) {
			ActionBar actionBar = this.actionBar;
			if (actionBar != null && actionBar.isShowing()) {
				this.actionBar.hide();
			}
		} else {
			ActionBar actionBar2 = this.actionBar;
			if (actionBar2 != null && !actionBar2.isShowing()) {
				this.actionBar.show();
			}
		}
		this.viewFlipper.setDisplayedChild(i);
	}

	public void showHome() {
		switchTo(HOME_ID);
	}

	public void showDetail() {
		if (!isValidDetailIndex(this.currentDetailIndex)) {
			showHome();
		} else {
			switchTo(this.currentDetailIndex);
		}
	}

	public int getCurrentDetailIndex() {
		return this.currentDetailIndex;
	}

	public void setCurrentDetailIndex(int i) {
		if (isValidDetailIndex(i)) {
			this.currentDetailIndex = i;
		} else {
			this.currentDetailIndex = HOME_ID;
		}
		Menu menu = this.actionBarMenu;
		if (menu != null) {
			menu.findItem(R.id.abm_grid_toggle).setChecked(i == 1);
		}
	}

	public void setActionBar(ActionBar actionBar) {
		this.actionBar = actionBar;
	}

	public void setActionBarMenu(Menu menu) {
		this.actionBarMenu = menu;
	}
}
