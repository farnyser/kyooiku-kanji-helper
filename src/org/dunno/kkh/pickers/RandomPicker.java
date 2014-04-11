package org.dunno.kkh.pickers;

import java.util.Random;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.QuizzCouple;

public class RandomPicker implements PickerInterface {
    Random random = new Random();
    
	@Override
	public Kanji pickKanji(KanjiSet ks) {
		return ks.getByIndex(random.nextInt(ks.getAll().size()));
	};
		
	@Override
	public KanjiSet pickChoices(KanjiSet ks, Kanji k) {
		KanjiSet result = new KanjiSet();
		
		int correctPosition = random.nextInt(3);
		for ( int i = 0 ; i < 3 ; i++ ) {
			if ( correctPosition == i ) 
				result.addKanji(k);
			
			while ( true ) {
				Kanji tmp = pickKanji(ks);
				if ( tmp.equals(k) == false && result.getAll().contains(tmp) == false ) { 
					result.addKanji(tmp);
					break;
				}
			}
		}
		
		return result;
	}

	@Override
	public QuizzCouple pickQuizzCouple(KanjiSet ks, Kanji k) {
		switch ( random.nextInt(4) ) {
			case 0:
				return QuizzCouple.KANJI_TO_MEANINGS;
			case 1:
				return QuizzCouple.KANJI_TO_MEANINGS;
			case 2:
				return QuizzCouple.MEANINGS_TO_KANJI;
			case 3:
				return QuizzCouple.READINGS_TO_KANJI;
		}
		
		// should never get there
		return QuizzCouple.READINGS_TO_KANJI;
	}
	
	
}
