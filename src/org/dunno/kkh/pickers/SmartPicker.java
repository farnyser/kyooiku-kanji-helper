package org.dunno.kkh.pickers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.Stats;

// TODO put scores in cache
public class SmartPicker implements PickerInterface {
	private Stats stats;
	private Random random = new Random();

	private class SK implements Comparable {
		public Kanji kanji;
		public Double score;
		public Integer age;
		
		@Override
		public int compareTo(Object other) {
			if ( other instanceof SK )
				return score.compareTo(((SK)other).score);
			else
				return 0;
		}
	}
	
	private final int MAX_AGE = 2;
	private HashMap<Kanji, SK> cache = new HashMap<Kanji, SK>();
	
	public SmartPicker(Stats stats) {
		this.stats = stats;
	}

	@Override
	public Kanji pickKanji(KanjiSet ks) {
		ArrayList<SK> sks = new ArrayList<SK>();
		Double sumScore = 0.0;
		
		for ( Kanji k : ks.getAll() ) {
			SK sk = new SK();
			sk.kanji = k;
			sk.score = computeScore(ks, k);
			sumScore += sk.score;
			sks.add(sk);
		}

		Collections.sort(sks);
		Double selected = (double)random.nextInt((int) Math.floor(sumScore));
		
		for ( SK sk : sks ) {
			selected -= sk.score;
			if ( selected <= 0 ) 
				return sk.kanji;
		}
		
		return sks.get(0).kanji;
	}

	@Override
	public KanjiSet pickChoices(KanjiSet ks, Kanji k) {
		int SIZE = 4;
		Double SCORE = computeScore(ks, k);
		
		if ( SCORE > .8 ) SIZE = 2;
		else if ( SCORE >.6 ) SIZE = 4;
		else if ( SCORE >.4 ) SIZE = 6;
		else if ( SCORE >.2 ) SIZE = 8;
		 
		
		KanjiSet result = new KanjiSet();
		
		int correctPosition = random.nextInt(SIZE);
		for ( int i = 0 ; i < SIZE ; i++ ) {
			if ( correctPosition == i ) {
				result.addKanji(k);
				continue;
			}
			
			while ( true ) {
				Kanji tmp = pickKanji(ks);
				if ( tmp.equals(k) == false && result.getAll().contains(tmp) == false ) { 
					result.addKanji(tmp);
					break;
				}
			}
		}
		if ( correctPosition == SIZE ) {
			result.addKanji(k);
		}

		// update cache age
		for ( Kanji l : result.getAll() ) {
			cache.get(l).age++;
		}
		cache.get(k).age++;

		
		return result;
	}

	@Override
	public QuizzCouple pickQuizzCouple(KanjiSet ks, Kanji k) {
		// TODO smarter QuizzCouple select
		
		switch ( random.nextInt(4) ) {
		case 0:
			return PickerInterface.QuizzCouple.KANJI_TO_MEANINGS;
		case 1:
			return PickerInterface.QuizzCouple.KANJI_TO_MEANINGS;
		case 2:
			return PickerInterface.QuizzCouple.MEANINGS_TO_KANJI;
		case 3:
			return PickerInterface.QuizzCouple.READINGS_TO_KANJI;
		}
	
		// should never get there
		return PickerInterface.QuizzCouple.READINGS_TO_KANJI;
	}
	
	/**
	 * Get a Kanji score
	 * 
	 * Score = 1 ==> that's a kanji we want to select and play with
	 * Score = 0 ==> the user know this kanji well, don't use it
	 * 
	 * @param ks
	 * @param kanji
	 * @return score
	 */
	private Double computeScore(KanjiSet ks, Kanji kanji) {
		if ( cache.get(kanji) != null && cache.get(kanji).age < MAX_AGE ) 
			return cache.get(kanji).score;
		
		final double C_1HE = 0.65;
		final double C_2HE = 1.00 - C_1HE;
		final double C_USE = 0.30;
		final double C_SR = 0.30;
		
		int sum1HE = 0;
		int sum1HS = 0;
		int sum2HE = 0;
		int sumUse = 0;
		int ksSize = 0;
		
		for ( Kanji k : ks.getAll() ) {
			sum1HS += stats.getFirstHandSuccess(k);
			sum1HE += stats.getFirstHandError(k);
			sum2HE += stats.getSecondHandError(k);
			ksSize++;
		}
		sumUse = sum1HS + sum1HE + sum2HE; 
		double avgSuccessRate = sum1HS > 0 ? 1 : 0;
		if ( sum1HE != 0 || sum2HE != 0 )
			 avgSuccessRate = sum1HS / (C_1HE*sum1HE + C_2HE*sum2HE); 

		
		int fhs = stats.getFirstHandSuccess(kanji);
		int fhe = stats.getFirstHandError(kanji);
		int she = stats.getSecondHandError(kanji);
		int use = fhs + fhe + she;
		double successRate = fhs > 0 ? 1 : 0;
	
		if ( she != 0 || fhe != 0 )
			successRate = fhs / (C_1HE*fhe + C_2HE*she); 
		
		double score = 1 - successRate;
		
		if ( ksSize > 0 ) {
			double avgUse = sumUse / ksSize;
			
			// increase score if not used often
			if ( use < avgUse ) {
				score = (score + C_USE)/(1+C_USE);
			}
		}
		
		// increase score if worst than average
		if ( successRate < avgSuccessRate ) {
			score = (score + C_SR)/(1+C_SR);
		}

		SK sk = new SK();
		sk.age = 0;
		sk.kanji = kanji;
		sk.score = score;
		cache.put(kanji, sk);
		
		return score;
	}
}
