package org.dunno.kkh.pickers;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.QuizzCouple;

/**
 * Interface for algorithm responsible of selecting the quizz questions
 * - Kanji to find
 * - List of Kanji for choices 
 * - Quizz Couple (Kanji to Reading, Kanji to meaning, ...)
 */
public interface PickerInterface {
	/**
	 * Pick the Kanji to find
	 * @param ks	Set of kanji to pick from (all kanji or subrange)
	 * @return Selected kanji
	 */
	public Kanji pickKanji(KanjiSet ks);
	
	/**
	 * Pick the choices for answering
	 * @param ks	Set of kanji to pick from (all kanji or subrange)
	 * @param k		Set of kanji to choose from (answer)
	 * @return Selected set of kanji
	 */
	public KanjiSet pickChoices(KanjiSet ks, Kanji k);
	
	/**
	 * Pick the QuizzCouple for this Couple
	 * @param ks 	Kanji set for choices
	 * @param k		Kanji to be found
	 * @return Selected QuizzCouple
	 */
	public QuizzCouple pickQuizzCouple(KanjiSet ks, Kanji k);
}
