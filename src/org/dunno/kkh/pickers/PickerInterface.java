package org.dunno.kkh.pickers;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;

public interface PickerInterface {
	public enum QuizzCouple {
		KANJI_TO_READINGS,
		KANJI_TO_MEANINGS,
		MEANINGS_TO_KANJI,
		READINGS_TO_KANJI
	}
	
	public Kanji pickKanji(KanjiSet ks);
	public KanjiSet pickChoices(KanjiSet ks, Kanji k);
	public QuizzCouple pickQuizzCouple(KanjiSet ks, Kanji k);
}
