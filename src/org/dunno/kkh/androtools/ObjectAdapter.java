package org.dunno.kkh.androtools;

import java.util.Collection;
import java.util.Vector;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ObjectAdapter extends BaseAdapter {
	private Vector<String> items = new Vector<String>();
	
    public void updateContent(Collection<String> items) {
        this.items.clear();
    
        for ( String s : items ) {
        	this.items.add(s);
        }
    
        this.notifyDataSetChanged();
    }

	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public String getItem(int location) {
		return items.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int location, View arg1, ViewGroup arg2) {
		TextView text = new TextView(arg2.getContext());
		text.setText(items.get(location));
		text.setTextSize((float) (text.getTextSize()*1.5));
		text.setHeight(100);
		text.setWidth(100);
		return text;
	}
}
