package org.dunno.kkh.models;

import java.util.ArrayList;

public class KanjiSet {
	private ArrayList<Kanji> set;
	
	public KanjiSet() {
		set = new ArrayList<Kanji>();
	}
	
	public void addKanji(Kanji kanji) {
		set.add(kanji);
	}
}
