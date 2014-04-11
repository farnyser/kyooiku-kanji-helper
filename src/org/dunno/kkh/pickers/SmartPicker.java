package org.dunno.kkh.pickers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.QuizzCouple;
import org.dunno.kkh.models.Stats;

import android.util.Log;

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
	private final long MAX_TIME = 7 * 24 * 60 * 60;
	private final double C_1HE = 0.65;
	private final double C_2HE = 1.00 - C_1HE;
	private final double C_USE = 0.30;
	private final double C_SR = 0.30;
	private final double C_LS_LE = 0.20;
	
	private HashMap<Kanji, SK> cache = new HashMap<Kanji, SK>();
	
	public SmartPicker(Stats stats) {
		this.stats = stats;
	}

	@Override
	public Kanji pickKanji(KanjiSet ks) {
		return pickKanji(ks, null, null, false);
	}

	@Override
	public KanjiSet pickChoices(KanjiSet ks, Kanji k) {
		int SIZE = 4;
		Double SCORE = computeScore(ks, k);
		
		if ( SCORE > .75 ) SIZE = 2;
		else if ( SCORE >.55 ) SIZE = 4;
		else if ( SCORE >.30 ) SIZE = 6;
		else if ( SCORE >.10 ) SIZE = 8;
		else SIZE = 10;
		 
		
		KanjiSet result = new KanjiSet();
		boolean reverse = SCORE >= .99; 
		
		int correctPosition = random.nextInt(SIZE);
		for ( int i = 0 ; i < SIZE ; i++ ) {
			if ( correctPosition == i ) {
				result.addKanji(k);
				continue;
			}
			
			while ( true ) {
				Kanji tmp = pickKanji(ks, k, result, reverse);
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
		QuizzCouple qcs[] = {
				QuizzCouple.KANJI_TO_MEANINGS,
				QuizzCouple.KANJI_TO_READINGS,
				QuizzCouple.MEANINGS_TO_KANJI,
				QuizzCouple.READINGS_TO_KANJI };
		double errorRate[] = {0,0,0,0};
		double sumErrorRate = 0;
		
		for ( int i = 0 ; i < 4 ; i++ ) {
			QuizzCouple qc = qcs[i];
			
			double fhs = stats.getFirstHandSuccess(k, qc);
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
		
		long now = (new Date()).getTime();
		
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

		
		double fhs = stats.getFirstHandSuccess(kanji);
		int fhe = stats.getFirstHandError(kanji);
		int she = stats.getSecondHandError(kanji);
		long ls = Math.min(Math.abs(now - stats.getLastSuccess(kanji)), MAX_TIME) / MAX_TIME;
		long le = Math.min(Math.abs(now - stats.getLastError(kanji)), MAX_TIME) / MAX_TIME;
		double use = fhs + fhe + she;
		double successRate = fhs > 0 ? 1 : 0;
		
	
		if ( she != 0 || fhe != 0 )
			successRate = fhs / (C_1HE*fhe + C_2HE*she); 
		
		double score = 1 - successRate;
		Log.d("score suc/err", kanji + " => " + score);
		
		if ( ksSize > 0 ) {
			double avgUse = sumUse / ksSize;
			
			// increase score if not used often
			if ( use < avgUse ) {
				score = (score + C_USE)/(1+C_USE);
				Log.d("score use", kanji + " => " + score);
			}
		}
		
		// increase score if worst than average
		if ( successRate < avgSuccessRate ) {
			score = (score + C_SR)/(1+C_SR);
		}
		Log.d("score avg", kanji + " => " + score);

		// increase/decrease score based on last usage
		Log.d("le", kanji + " => " + le);
		Log.d("ls", kanji + " => " + ls);
		score += C_LS_LE * (1-score) * le;
		Log.d("score time le", kanji + " => " + score);
		score -= C_LS_LE * score * ls;
		Log.d("score time ls", kanji + " => " + score);
		
		// reuse kanji, even if we are good
		// if we don't show them anymore, it can be forgotten
		if ( score < .01 ) 
			score = .01;
		
		SK sk = new SK();
		sk.age = 0;
		sk.kanji = kanji;
		sk.score = score;
		cache.put(kanji, sk);

		Log.d("score", kanji + " => " + score);
		return score;
	}
	
	/**
	 * Pick a Kanji in ks list but not notItem and not one of the element of notList
	 * @param ks			List to choose from
	 * @param notItem		Kanji to eliminate
	 * @param notList		List of Kanji to eliminate
	 * @param reverse		If set to true, pick easiest choices
	 * @return a kanji, or null
	 */
	private Kanji pickKanji(KanjiSet ks, Kanji notItem, KanjiSet notList, boolean reverse) {
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
			if ( reverse ) 
				sk.score = 1 - sk.score;
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
