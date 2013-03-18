package org.dunno.kkh.models;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import android.util.Log;

public class KanjiSet {
	private Vector<Kanji> set;
	
	public KanjiSet() {
		set = new Vector<Kanji>();
	}
	
	public void addKanji(Kanji kanji) {
		set.add(kanji);
	}
	
	public Collection<Kanji> getAll() {
		return Collections.unmodifiableCollection(set);
	}

	public Kanji getByIndex(int index) {
		if ( index < 0 || index >= set.size() ) {
			Log.e("KanjiSet", "getByIndex called with out of bound index");
			return null;
		}
		
		return set.get(index);
	}
}
