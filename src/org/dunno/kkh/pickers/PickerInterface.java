package org.dunno.kkh.pickers;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;

/**
 * Interface for algorithm responsible of selecting the quizz questions
 * - Kanji to find
 * - List of Kanji for choices 
 * - Quizz Couple (Kanji to Reading, Kanji to meaning, ...)
 */
public interface PickerInterface {
	/**
	 * Define what can be shown to the user
	 */
	public enum QuizzElement {
		/**
		 * Kanji character
		 */
		KANJI, 
		
		/**
		 * Kanji's readings
		 */
		READINGS,
		
		/**
		 * Kanji's meanings
		 */
		MEANINGS;
		
		/**
		 * Get string representation of this QuizzElement for a given Kanji
		 * @param k	Given kanji
		 * @return String representation
		 */
		String toString(Kanji k) {
			if ( this == KANJI )
				return k.getCharacter();
			else if ( this == READINGS )
				return k.getKunReading() + "\n" + k.getOnReading();
			else
				return k.getMeaning();
		}
	}
	
	/**
	 * Define what to show to the user.
	 */
	public enum QuizzCouple {
		/**
		 * Show ToFind as a Kanji, and choices as readings
		 */
		KANJI_TO_READINGS(QuizzElement.KANJI, QuizzElement.READINGS),
		
		/**
		 * Show ToFind as a Kanji, and choices as meanings
		 */
		KANJI_TO_MEANINGS(QuizzElement.KANJI, QuizzElement.MEANINGS),
		
		/**
		 * Show ToFind as meanings, and choices as Kanji
		 */
		MEANINGS_TO_KANJI(QuizzElement.MEANINGS, QuizzElement.KANJI),
		
		/**
		 * Show ToFind as readings, and choices as Kanji
		 */
		READINGS_TO_KANJI(QuizzElement.READINGS, QuizzElement.KANJI);
		
		
		private QuizzElement first, second;
		
		QuizzCouple(QuizzElement first, QuizzElement second) {
			this.first = first;
			this.second = second;
		}

		public String firstAsString(Kanji k) {
			return first.toString(k);
		}
		
		public String secondAsString(Kanji k) {
			return second.toString(k);
		}
	}
	
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
