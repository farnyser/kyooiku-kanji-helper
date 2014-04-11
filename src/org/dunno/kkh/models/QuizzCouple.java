package org.dunno.kkh.models;
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
