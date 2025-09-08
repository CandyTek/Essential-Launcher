package de.clemensbartz.android.launcher.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import de.clemensbartz.android.launcher.BuildConfig;
import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.comparators.LocaledStringComparator;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.tasks.LoadApplicationModelIconIntoImageViewTask;
import de.clemensbartz.android.launcher.util.LocaleUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/* loaded from: classes.dex */
public final class DrawerListAdapter extends ArrayAdapter<ApplicationModel> implements SearchView.OnQueryTextListener, SectionIndexer {
    private static final String FILTER_SEPARATOR = " ";
    private static final int[] ITEM_RESOURCE_IDS = {R.layout.grid_drawer_item, R.layout.list_drawer_item};
    private final Drawable defaultDrawable;
    private final List<ApplicationModel> filteredList;
    private final Map<String, Integer> indexMap;
    private final Locale locale;
    private String lowerCaseFilter;
    private final List<String> sections;
    private boolean showHiddenApps;
    private final List<ApplicationModel> unfilteredList;

    public DrawerListAdapter(Context context, Drawable drawable) {
        super(context, R.layout.grid_drawer_item);
        this.unfilteredList = new ArrayList();
        this.filteredList = new ArrayList();
        this.sections = new ArrayList();
        this.indexMap = new HashMap();
        this.lowerCaseFilter = BuildConfig.FLAVOR;
        this.showHiddenApps = false;
        this.defaultDrawable = drawable;
        this.locale = LocaleUtil.getLocale(context);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(getResource(viewGroup), (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ApplicationModel item = getItem(i);
        if (viewHolder != null && viewHolder.icon != null && viewHolder.name != null) {
            viewHolder.icon.setImageDrawable(this.defaultDrawable);
            viewHolder.icon.setContentDescription(item.label);
            viewHolder.name.setText(item.label);
            LoadApplicationModelIconIntoImageViewTask loadApplicationModelIconIntoImageViewTask = new LoadApplicationModelIconIntoImageViewTask(viewHolder.icon, item, getContext().getPackageManager(), this.defaultDrawable);
            try {
                loadApplicationModelIconIntoImageViewTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[0]);
            } catch (RejectedExecutionException unused) {
                loadApplicationModelIconIntoImageViewTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Integer[0]);
            }
        }
        return view;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public ApplicationModel getItem(int i) {
        return this.filteredList.get(i);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.filteredList.size();
    }

    @Override // android.widget.ArrayAdapter
    public int getPosition(ApplicationModel applicationModel) {
        if (applicationModel != null) {
            return this.filteredList.indexOf(applicationModel);
        }
        return -1;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        return this.unfilteredList.isEmpty();
    }

    @Override // android.widget.ArrayAdapter
    public void add(ApplicationModel applicationModel) {
        if (applicationModel != null) {
            this.unfilteredList.add(applicationModel);
        }
    }

    @Override // android.widget.ArrayAdapter
    public void addAll(Collection<? extends ApplicationModel> collection) {
        this.unfilteredList.addAll(collection);
    }

    @Override // android.widget.ArrayAdapter
    public void addAll(ApplicationModel... applicationModelArr) {
        this.unfilteredList.addAll(Arrays.asList(applicationModelArr));
    }

    @Override // android.widget.ArrayAdapter
    public void remove(ApplicationModel applicationModel) {
        if (applicationModel != null) {
            this.unfilteredList.remove(applicationModel);
        }
    }

    @Override // android.widget.ArrayAdapter
    public void clear() {
        this.unfilteredList.clear();
    }

    @Override // android.widget.ArrayAdapter
    public void sort(Comparator<? super ApplicationModel> comparator) {
        Collections.sort(this.unfilteredList, comparator);
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        if (str == null) {
            this.lowerCaseFilter = BuildConfig.FLAVOR;
        } else {
            this.lowerCaseFilter = str.toLowerCase(this.locale);
        }
        filter();
        return true;
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        return onQueryTextSubmit(str);
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        Object[] array = this.sections.toArray();
        return array != null ? array : new Object[0];
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int i) {
        Integer num = this.indexMap.get(this.sections.get(i));
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int i) {
        ApplicationModel applicationModel = this.filteredList.get(i);
        if (applicationModel.label == null || applicationModel.label.trim().length() <= 0) {
            return 0;
        }
        return this.sections.indexOf(applicationModel.label.trim().substring(0, 1).toUpperCase(this.locale));
    }

    public boolean isShowingHiddenApps() {
        return this.showHiddenApps;
    }

    public void setShowHiddenApps(boolean z) {
        this.showHiddenApps = z;
    }

    public void filter() {
        this.filteredList.clear();
        this.indexMap.clear();
        this.sections.clear();
        if (this.lowerCaseFilter.isEmpty() || this.lowerCaseFilter.trim().isEmpty()) {
            for (int i = 0; i < this.unfilteredList.size(); i++) {
                ApplicationModel applicationModel = this.unfilteredList.get(i);
                if (this.showHiddenApps || !applicationModel.hidden) {
                    this.filteredList.add(applicationModel);
                    addSection(applicationModel.label, Integer.valueOf(i));
                }
            }
            notifyDataSetChanged();
            return;
        }
        String[] split = this.lowerCaseFilter.split(FILTER_SEPARATOR);
        for (int i2 = 0; i2 < this.unfilteredList.size(); i2++) {
            ApplicationModel applicationModel2 = this.unfilteredList.get(i2);
            for (String str : split) {
                if (!str.isEmpty() && !str.trim().isEmpty() && ((this.showHiddenApps || !applicationModel2.hidden) && applicationModel2.label != null && applicationModel2.className != null && applicationModel2.packageName != null && (applicationModel2.label.toLowerCase(this.locale).contains(str) || applicationModel2.className.toLowerCase(this.locale).contains(str) || applicationModel2.packageName.toLowerCase(this.locale).contains(str)))) {
                    this.filteredList.add(applicationModel2);
                    addSection(applicationModel2.label, Integer.valueOf(i2));
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    private void addSection(String str, Integer num) {
        if (str == null || str.trim().length() <= 0) {
            return;
        }
        String upperCase = str.trim().substring(0, 1).toUpperCase(this.locale);
        if (this.sections.contains(upperCase)) {
            return;
        }
        this.sections.add(upperCase);
        Collections.sort(this.sections, new LocaledStringComparator(this.locale));
        this.indexMap.put(upperCase, num);
    }

    private int getResource(View view) {
        int id = view.getId();
        if (id == R.id.gvApplications) {
            return ITEM_RESOURCE_IDS[0];
        }
        if (id != R.id.lvApplications) {
            return -1;
        }
        return ITEM_RESOURCE_IDS[1];
    }

    static final class ViewHolder {
        ImageView icon;
        TextView name;

        ViewHolder() {
        }
    }
}
