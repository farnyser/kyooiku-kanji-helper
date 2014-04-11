package org.dunno.kkh.models;
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