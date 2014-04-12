package org.dunno.kkh.androtools;

import java.util.Collection;
import java.util.Vector;

import org.dunno.kkh.R;
import org.dunno.kkh.models.Kanji;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ObjectAdapter extends BaseAdapter {
	private Vector<String> items = new Vector<String>();
	private Vector<Kanji> kanjis = new Vector<Kanji>();
	private int apparance;
	private float defaultSize;
	
	private int minHeight = 0;
	private int remaining = 0;
	
    public void updateContent(Collection<Kanji> kanjis, Collection<String> items, int apparance) {
    	this.apparance = apparance;
        this.items.clear();
        this.kanjis.clear();
        this.items.addAll(items);
        this.kanjis.addAll(kanjis);
        this.minHeight = 100;
        this.remaining = items.size();
        this.notifyDataSetChanged();
    }

	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Kanji getItem(int location) {
		return kanjis.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int location, View v, ViewGroup vg) {
		TextView text;
		
		if ( !(v instanceof TextView) ) {
			text = new TextView(vg.getContext()) {
				@Override
				protected void onSizeChanged(int w, int h, int oldw, int oldh) {
					super.onSizeChanged(w, h, oldw, oldh);
					remaining--;
					if ( minHeight < h ) {
						minHeight = h;
					}
					if ( remaining == 0 && minHeight != 100 ) {
						notifyDataSetChanged();
					}
				}
			};
			text.setWidth(100);
			text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			defaultSize = text.getTextSize();
		}
		else
			text = (TextView) v;
		
		text.setMinHeight(minHeight);
		text.setTextAppearance(vg.getContext(), apparance);
		text.setBackground(vg.getContext().getResources().getDrawable(R.drawable.light));

		if ( apparance == android.R.attr.textAppearanceMedium ) {
			text.setTextSize( (float) (defaultSize * 1.8) );
		}
		else {
			text.setTextSize( defaultSize );
		}
		
		text.setText(items.get(location));
		return text;
	}
}
