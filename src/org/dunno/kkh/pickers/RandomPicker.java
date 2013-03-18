package org.dunno.kkh.pickers;

import java.util.Random;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;

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
}
