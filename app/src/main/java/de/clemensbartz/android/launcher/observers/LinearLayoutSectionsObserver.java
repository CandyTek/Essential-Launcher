package de.clemensbartz.android.launcher.observers;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import de.clemensbartz.android.launcher.listeners.SectionLabelOnClickListener;

/* loaded from: classes.dex */
public final class LinearLayoutSectionsObserver<T extends ArrayAdapter & SectionIndexer> extends DataSetObserver {
	private static final int MINIMUM_ITEM_COUNT = 3;
	private static final int PADDING_BOTTOM = 10;
	private static final float TEXT_SIZE = 20.0f;
	private final int actionBarHeight;
	private final Context context;
	private final LinearLayout linearLayout;
	private final ListView listView;
	private final T sectionedArrayAdapter;

	public LinearLayoutSectionsObserver(Context context,int i,ListView listView,LinearLayout linearLayout,T t) {
		this.context = context;
		this.listView = listView;
		this.linearLayout = linearLayout;
		this.sectionedArrayAdapter = t;
		this.actionBarHeight = i;
		t.registerDataSetObserver(this);
	}

	@Override // android.database.DataSetObserver
	public void onChanged() {
		if (this.linearLayout.getMeasuredHeight() <= 0) {
			return;
		}
		Object[] sections = this.sectionedArrayAdapter.getSections();
		for (int childCount = this.linearLayout.getChildCount() - 1;childCount >= sections.length;childCount--) {
			this.linearLayout.removeViewAt(childCount);
		}
		for (int childCount2 = this.linearLayout.getChildCount();childCount2 < sections.length;childCount2++) {
			this.linearLayout.addView(new TextView(this.context));
		}
		if (sections.length != this.linearLayout.getChildCount()) {
			throw new RuntimeException("Not enough children.");
		}
		for (int i = 0;i < sections.length;i++) {
			if (sections[i] instanceof String) {
				String str = (String) sections[i];
				int positionForSection = this.sectionedArrayAdapter.getPositionForSection(i);
				View childAt = this.linearLayout.getChildAt(i);
				if (childAt instanceof TextView) {
					TextView textView = (TextView) childAt;
					textView.setTextSize(2,TEXT_SIZE);
					textView.setText(str);
					textView.setGravity(17);
					textView.setTextColor(-1);
					textView.setPadding(0,0,0,PADDING_BOTTOM);
					textView.setVisibility(0);
					textView.setClickable(true);
					textView.setOnClickListener(new SectionLabelOnClickListener(this.listView,positionForSection));
				}
			}
		}
		hideOverlappingItems();
	}

	private void hideOverlappingItems() {
		if (this.linearLayout.getChildCount() <= MINIMUM_ITEM_COUNT) {
			return;
		}
		this.linearLayout.requestLayout();
		int measuredHeight = this.linearLayout.getMeasuredHeight() - this.actionBarHeight;
		if (measuredHeight <= 0) {
			return;
		}
		int ceil = ((int) Math.ceil(
				TypedValue.applyDimension(2,getTextSize(),this.context.getResources().getDisplayMetrics()))) + PADDING_BOTTOM;
		int childCount = (this.linearLayout.getChildCount() * ceil) - measuredHeight;
		if (childCount <= 0) {
			return;
		}
		int childCount2 = (this.linearLayout.getChildCount() - 2) / ((childCount / ceil) + 2);
		for (int i = childCount2;i < this.linearLayout.getChildCount() - 1;i += childCount2) {
			this.linearLayout.getChildAt(i).setVisibility(8);
		}
	}

	private float getTextSize() {
		return this.context.getResources().getConfiguration().fontScale * TEXT_SIZE;
	}
}
