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
		switch ( this ) {
			case KANJI:
				return k.getCharacter();
			case READINGS: {
				if ( k.getKunReading().isEmpty() ) 
					return k.getOnReading();
				else if ( k.getOnReading().isEmpty() )
					return k.getKunReading();
				else
					return k.getKunReading() + "\n" + k.getOnReading();
			}
			default:
				return k.getMeaning();
		}
	}
}