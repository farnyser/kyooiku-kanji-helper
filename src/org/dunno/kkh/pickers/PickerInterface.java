package org.dunno.kkh.pickers;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;

public interface PickerInterface {
	public Kanji pickKanji(KanjiSet ks);
	public KanjiSet pickChoices(KanjiSet ks, Kanji k);
}
