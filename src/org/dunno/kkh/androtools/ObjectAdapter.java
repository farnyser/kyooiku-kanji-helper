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
	
    public void updateContent(Collection<Kanji> kanjis, Collection<String> items, int apparance) {
    	this.apparance = apparance;
        this.items.clear();
        this.kanjis.clear();
    
        for ( String s : items ) {
        	this.items.add(s);
        }
    
        for ( Kanji k : kanjis ) {
        	this.kanjis.add(k);
        }
    
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
			text = new TextView(vg.getContext());
			defaultSize = text.getTextSize();
		}
		else
			text = (TextView) v;
		
		text.setTextAppearance(vg.getContext(), apparance);
		//text.setBackgroundColor(vg.getContext().getResources().getColor(android.R.color.darker_gray));
		text.setBackgroundColor(vg.getContext().getResources().getColor(R.color.griditem));
		text.setHeight(100);
		text.setWidth(100);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

		if ( apparance == android.R.attr.textAppearanceLarge ) {
			text.setTextSize( (float) (defaultSize * 1.5) );
		}
		else {
			text.setTextSize( defaultSize );
		}
		
		text.setText(items.get(location));
		return text;
	}
}
