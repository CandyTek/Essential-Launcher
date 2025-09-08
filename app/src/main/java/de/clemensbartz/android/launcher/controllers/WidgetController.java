package de.clemensbartz.android.launcher.controllers;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import de.clemensbartz.android.launcher.Launcher;
import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.tasks.CreateWidgetAsyncTask;
import de.clemensbartz.android.launcher.tasks.ShowWidgetListAsPopupMenuTask;
import de.clemensbartz.android.launcher.util.BundleUtil;
import de.clemensbartz.android.launcher.util.IntentUtil;

/* loaded from: classes.dex */
public final class WidgetController {
    public static final int DEFAULT_APPWIDGET_ID = -1;
    public static final int DEFAULT_APPWIDGET_LAYOUT = -1;
    private static final String EXTRA_APP_WIDGET_CONFIGURE = "EL_APP_WIDGET_CONFIGURE";
    public static final String KEY_APPWIDGET_ID = "appWidgetId";
    public static final String KEY_APPWIDGET_LAYOUT = "appWidgetLayout";
    private static final int REQUEST_BIND_APPWIDGET = 0;
    private static final int REQUEST_CREATE_APPWIDGET = 1;
    private static final int WEIGHT_SUM = 120;
    private static final int WEIGHT_SUM_HALF = 60;
    private static final int WEIGHT_SUM_QUARTER = 30;
    private static final int WEIGHT_SUM_THIRD = 40;
    private static final int WIDGET_LAYOUT_BOTTOM_HALF = 20;
    private static final int WIDGET_LAYOUT_BOTTOM_QUARTER = 30;
    private static final int WIDGET_LAYOUT_BOTTOM_THIRD = 25;
    private static final int WIDGET_LAYOUT_CENTER = 15;
    private static final int WIDGET_LAYOUT_FULL_SCREEN = -1;
    private static final int WIDGET_LAYOUT_TOP_HALF = 10;
    private static final int WIDGET_LAYOUT_TOP_QUARTER = 0;
    private static final int WIDGET_LAYOUT_TOP_THIRD = 5;
    private final AppWidgetHost appWidgetHost;
    private final AppWidgetManager appWidgetManager;
    private final Launcher launcher;
    private final SharedPreferencesDAO sharedPreferencesDAO;
    private ComponentName widgetConfigure = null;

    private int getPreciseBottomWeight(int i) {
        if (i == 0) {
            return 90;
        }
        if (i == WIDGET_LAYOUT_TOP_THIRD) {
            return 80;
        }
        if (i == WIDGET_LAYOUT_TOP_HALF) {
            return WEIGHT_SUM_HALF;
        }
        if (i != WIDGET_LAYOUT_CENTER) {
            return 0;
        }
        return WEIGHT_SUM_THIRD;
    }

    private int getPreciseTopWeight(int i) {
        if (i == WIDGET_LAYOUT_CENTER) {
            return WEIGHT_SUM_THIRD;
        }
        if (i == 20) {
            return WEIGHT_SUM_HALF;
        }
        if (i != WIDGET_LAYOUT_BOTTOM_THIRD) {
            return i != 30 ? 0 : 90;
        }
        return 80;
    }

    private int getPreciseWidgetWeight(int i) {
        if (i != 0) {
            if (i == WIDGET_LAYOUT_TOP_THIRD) {
                return WEIGHT_SUM_THIRD;
            }
            if (i == WIDGET_LAYOUT_TOP_HALF) {
                return WEIGHT_SUM_HALF;
            }
            if (i == WIDGET_LAYOUT_CENTER) {
                return WEIGHT_SUM_THIRD;
            }
            if (i == 20) {
                return WEIGHT_SUM_HALF;
            }
            if (i == WIDGET_LAYOUT_BOTTOM_THIRD) {
                return WEIGHT_SUM_THIRD;
            }
            if (i != 30) {
                return WEIGHT_SUM;
            }
        }
        return 30;
    }

    public WidgetController(Launcher launcher, SharedPreferencesDAO sharedPreferencesDAO) {
        this.sharedPreferencesDAO = sharedPreferencesDAO;
        this.launcher = launcher;
        this.appWidgetManager = AppWidgetManager.getInstance(launcher);
        this.appWidgetHost = new AppWidgetHost(launcher, R.id.flWidget);
    }

    public void startListening() {
        this.appWidgetHost.startListening();
    }

    public void stopListening() {
        this.appWidgetHost.stopListening();
    }

    public boolean isAppWidgetConfigured() {
        return this.sharedPreferencesDAO.getInt(KEY_APPWIDGET_ID, -1) > -1;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Integer valueOf = Integer.valueOf(intent.getIntExtra(KEY_APPWIDGET_ID, -1));
            if (i == 1) {
                new CreateWidgetAsyncTask(this).execute(valueOf);
            } else if (i == 0) {
                if (this.widgetConfigure != null) {
                    configureWidget(valueOf.intValue(), this.widgetConfigure);
                } else {
                    new CreateWidgetAsyncTask(this).execute(valueOf);
                }
            }
        }
        this.widgetConfigure = null;
    }

    public void addHostView(int i) {
        FrameLayout frameLayout = (FrameLayout) this.launcher.findViewById(R.id.flWidget);
        frameLayout.removeAllViews();
        AppWidgetProviderInfo appWidgetInfo = this.appWidgetManager.getAppWidgetInfo(i);
        if (appWidgetInfo != null) {
            AppWidgetHostView createView = this.appWidgetHost.createView(this.launcher, i, appWidgetInfo);
            createView.setAppWidget(i, appWidgetInfo);
            frameLayout.addView(createView);
            frameLayout.requestLayout();
            createView.updateAppWidgetOptions(BundleUtil.getWidgetOptionsBundle(frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight(), frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight()));
            this.sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, i);
            return;
        }
        this.sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, -1);
    }

    public void adjustWidget(int i) {
        View findViewById = this.launcher.findViewById(R.id.bottomFiller);
        View topFiller = getTopFiller();
        FrameLayout frameLayout = (FrameLayout) this.launcher.findViewById(R.id.flWidget);
        ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = topFiller.getLayoutParams();
        ViewGroup.LayoutParams layoutParams3 = frameLayout.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).weight = getPreciseBottomWeight(i);
        }
        if (layoutParams2 instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams2).weight = getPreciseTopWeight(i);
        }
        if (layoutParams3 instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams3).weight = getPreciseWidgetWeight(i);
        }
        findViewById.requestLayout();
        topFiller.requestLayout();
        frameLayout.requestLayout();
        if (frameLayout.getChildCount() == 1) {
            View childAt = frameLayout.getChildAt(0);
            if (childAt instanceof AppWidgetHostView) {
                ((AppWidgetHostView) childAt).updateAppWidgetOptions(BundleUtil.getWidgetOptionsBundle(frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight(), frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight()));
            }
        }
        this.sharedPreferencesDAO.putInt(KEY_APPWIDGET_LAYOUT, i);
    }

    public void bindWidget(ComponentName componentName, ComponentName componentName2) {
        FrameLayout frameLayout = (FrameLayout) this.launcher.findViewById(R.id.flWidget);
        int allocateAppWidgetId = this.appWidgetHost.allocateAppWidgetId();
        if (this.appWidgetManager.bindAppWidgetIdIfAllowed(allocateAppWidgetId, componentName)) {
            configureWidget(allocateAppWidgetId, componentName2);
            return;
        }
        this.widgetConfigure = componentName2;
        Intent createWidgetBindIntent = IntentUtil.createWidgetBindIntent(componentName, allocateAppWidgetId);
        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetCategory", 1);
        bundle.putInt("appWidgetMinWidth", frameLayout.getMinimumWidth());
        bundle.putInt("appWidgetMaxWidth", frameLayout.getWidth());
        bundle.putInt("appWidgetMinHeight", frameLayout.getMinimumHeight());
        bundle.putInt("appWidgetMaxHeight", frameLayout.getHeight());
        createWidgetBindIntent.putExtra("appWidgetOptions", bundle);
        this.launcher.startActivityForResult(createWidgetBindIntent, 0);
    }

    private void configureWidget(int i, ComponentName componentName) {
        if (i == -1) {
            return;
        }
        if (componentName != null) {
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent.setComponent(componentName);
            intent.putExtra(KEY_APPWIDGET_ID, i);
            intent.putExtra(EXTRA_APP_WIDGET_CONFIGURE, componentName);
            if (IntentUtil.isCallable(this.launcher.getPackageManager(), intent)) {
                this.launcher.startActivityForResult(intent, 1);
            }
        } else {
            new CreateWidgetAsyncTask(this).execute(Integer.valueOf(i));
        }
        this.widgetConfigure = null;
    }

    public void createWidget(int i) {
        FrameLayout frameLayout = (FrameLayout) this.launcher.findViewById(R.id.flWidget);
        int i2 = this.sharedPreferencesDAO.getInt(KEY_APPWIDGET_ID, -1);
        if (i2 > -1) {
            this.appWidgetHost.deleteAppWidgetId(i2);
            frameLayout.removeAllViews();
            adjustWidget(-1);
        }
        this.sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, i);
        MenuItem actionBarMenuItem = this.launcher.getActionBarMenuItem(R.id.abm_remove_widget);
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisible(i > -1);
        }
        MenuItem actionBarMenuItem2 = this.launcher.getActionBarMenuItem(R.id.abm_layout_widget);
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setVisible(i > -1);
        }
        addHostView(i);
    }

    public View getTopFiller() {
        return this.launcher.findViewById(R.id.topFiller);
    }

    public void requestWidgetChoosing() {
        new ShowWidgetListAsPopupMenuTask(this, this.launcher, this.appWidgetManager).execute(new Integer[0]);
    }

    public void requestWidgetLayoutChange() {
        PopupMenu popupMenu = new PopupMenu(this.launcher, getTopFiller());
        int i = this.sharedPreferencesDAO.getInt(KEY_APPWIDGET_LAYOUT, -1);
        addLayoutPopupMenuItem(popupMenu, -1, i, R.string.widgetLayoutFull);
        addLayoutPopupMenuItem(popupMenu, 0, i, R.string.widgetLayoutTopQuarter);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_TOP_THIRD, i, R.string.widgetLayoutTopThird);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_TOP_HALF, i, R.string.widgetLayoutTopHalf);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_CENTER, i, R.string.widgetLayoutCenter);
        addLayoutPopupMenuItem(popupMenu, 20, i, R.string.widgetLayoutBottomHalf);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_BOTTOM_THIRD, i, R.string.widgetLayoutBottomThird);
        addLayoutPopupMenuItem(popupMenu, 30, i, R.string.widgetLayoutBottomQuarter);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: de.clemensbartz.android.launcher.controllers.WidgetController.1
            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                WidgetController.this.adjustWidget(menuItem.getItemId());
                return true;
            }
        });
        popupMenu.show();
    }

    public void requestWidgetRemoval() {
        new CreateWidgetAsyncTask(this).execute(-1);
    }

    private void addLayoutPopupMenuItem(PopupMenu popupMenu, int i, int i2, int i3) {
        MenuItem add = popupMenu.getMenu().add(0, i, 0, i3);
        add.setCheckable(true);
        add.setChecked(i2 == i);
    }
}
