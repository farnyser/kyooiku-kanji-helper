package org.dunno.kkh.pickers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.Stats;

public class SmartPicker implements PickerInterface {
	private Stats stats;
	private Random random = new Random();

	private class SK implements Comparable<SK> {
		public Kanji kanji;
		public Double score;
		public Integer age;
		
		@Override
		public int compareTo(SK other) {
			return score.compareTo(((SK)other).score);
		}
	}
	
	private final int MAX_AGE = 1;
	private final double C_1HE = 0.65;
	private final double C_2HE = 1.00 - C_1HE;
	private final double C_USE = 0.30;
	private final double C_SR = 0.30;
	
	private HashMap<Kanji, SK> cache = new HashMap<Kanji, SK>();
	
	public SmartPicker(Stats stats) {
		this.stats = stats;
	}

	@Override
	public Kanji pickKanji(KanjiSet ks) {
		return pickKanji(ks, null, null);
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
				Kanji tmp = pickKanji(ks, k, result);
				if ( tmp == null )
					break;
				else if ( tmp.equals(k) == false && result.getAll().contains(tmp) == false ) { 
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
		PickerInterface.QuizzCouple qcs[] = {
				PickerInterface.QuizzCouple.KANJI_TO_MEANINGS,
				PickerInterface.QuizzCouple.KANJI_TO_MEANINGS,
				PickerInterface.QuizzCouple.MEANINGS_TO_KANJI,
				PickerInterface.QuizzCouple.READINGS_TO_KANJI };
		double errorRate[] = {0,0,0,0};
		double sumErrorRate = 0;
		
		for ( int i = 0 ; i < 4 ; i++ ) {
			QuizzCouple qc = qcs[i];
			
			int fhs = stats.getFirstHandSuccess(k, qc);
			int fhe = stats.getFirstHandError(k, qc);
			int she = stats.getSecondHandError(k, qc);
			errorRate[i] = fhs > 0 ? 0 : 1;
		
			if ( she != 0 || fhe != 0 )
				errorRate[i] = fhs / (C_1HE*fhe + C_2HE*she); 
			
			sumErrorRate += errorRate[i];
		}
		
		double d = random.nextDouble() * sumErrorRate;
		
		for ( int i = 0 ; i < 4 ; i++ ) {
			if ( d < errorRate[i] )
				return qcs[i];
			
			d -= errorRate[i];
		}
		
		
		// should not reach here unless there ain't any error
		return qcs[ random.nextInt(4) ];
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
		
		// reuse kanji, even if we are good
		// if we don't show them anymore, it can be forgotten
		if ( score < .01 ) 
			score = .01;
		
		return score;
	}
	
	/**
	 * Pick a Kanji in ks list but not notItem and not one of the element of notList
	 * @param ks			List to choose from
	 * @param notItem		Kanji to eliminate
	 * @param notList		List of Kanji to eliminate
	 * @return a kanji, or null
	 */
	private Kanji pickKanji(KanjiSet ks, Kanji notItem, KanjiSet notList) {
		ArrayList<SK> sks = new ArrayList<SK>();
		Double sumScore = 0.0;
		
		for ( Kanji k : ks.getAll() ) {
			if ( notItem != null && k.equals(notItem) ) 
				continue;
			else if ( notList != null && notList.getAll().contains(k) )
				continue;
			
			SK sk = new SK();
			sk.kanji = k;
			sk.score = computeScore(ks, k);
			sumScore += sk.score;
			sks.add(sk);
		}

		Collections.shuffle(sks);
		Double selected =  random.nextDouble()*sumScore;
		
		for ( SK sk : sks ) {
			selected -= sk.score;
			if ( selected <= 0 ) 
				return sk.kanji;
		}
		
		if ( sks.size() > 0 )
			return sks.get(0).kanji;
		else
			return null;
	}
}
