package org.dunno.kkh.models;

import java.util.ArrayList;

import android.util.Log;

public class KanjiSet {
	private ArrayList<Kanji> set;
	
	public KanjiSet() {
		set = new ArrayList<Kanji>();
	}
	
	public void addKanji(Kanji kanji) {
		Log.v("KanjiSet", "Add kanji " + kanji.getCharacter());
		set.add(kanji);
	}
}
