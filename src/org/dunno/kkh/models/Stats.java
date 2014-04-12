package org.dunno.kkh.models;

import android.annotation.SuppressLint;

import java.util.Date;
import java.util.HashMap;

import org.dunno.kkh.models.QuizzCouple;

public class Stats {
	/**
	 * Represent statistics about a Kanji.
	 * 
	 * For example, contains the date of last success 
	 * (when the Kanji was found correctly for the last time)
	 */
	private class StatItem {
		/**
		 * Kanji rank in the Kyooiku ranking
		 */
		public final Integer kanjiNumber;
		
		/**
		 * Last time when the Kanji was correctly found (as a timestamp)
		 */
		public Long lastSuccess;
		
		/**
		 * LAst time when the Kanji was not correctly found (as a timestamp)
		 */
		public Long lastError;
		
		/**
		 * Map of QuizzCouple to a count of error. 
		 * 
		 * For example, when displaying "Inu"(Japanese kanji), if the user click on "Cat"(Meaning), 
		 * then this is incremented for the Kanji INU, and QuizzCouple=KANJI_TO_MEANINGS
		 */
		public HashMap<QuizzCouple, Integer> firstHandError = new HashMap<QuizzCouple, Integer>();

		/**
		 * Map of QuizzCouple to a count of error. 
		 * 
		 * For example, when displaying "Inu"(Japanese kanji), if the user click on "Cat"(Meaning), 
		 * then this is incremented for the Kanji CAT, and QuizzCouple=KANJI_TO_MEANINGS
		 */
		public HashMap<QuizzCouple, Integer> secondHandError = new HashMap<QuizzCouple, Integer>();
		

		/**
		 * Map of QuizzCouple to a count of success. 
		 * 
		 * For example, when displaying "Inu"(Japanese kanji), if the user click on "Dog"(Meaning), 
		 * then this is incremented for the Kanji INU, and QuizzCouple=KANJI_TO_MEANINGS
		 */
		public HashMap<QuizzCouple, Double> firstHandSuccess = new HashMap<QuizzCouple, Double>();

		/**
		 * Construct the stats for the Kanji of rank n
		 * @param n	Rank
		 */
		public StatItem(int n, Date now) {
			this.kanjiNumber = n;
			this.lastError = -1l;
			this.lastSuccess = -1l;
		}
	}

	
	/**
	 * Map of Kanji-rank to stats
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, StatItem> stats = new HashMap<Integer, StatItem>();

	/**
	 * Add a StatItem for a Kanji of rank n
	 * @param n	Rank of the kanji
	 * @param now Date used for initialization
	 */
	private void init(int n, Date now) {
		if (stats.get(n) == null) {
			stats.put(n, new StatItem(n, now));
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_MEANINGS, 0);
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_READINGS, 0);
			stats.get(n).firstHandError.put(QuizzCouple.READINGS_TO_KANJI, 0);
			stats.get(n).firstHandError.put(QuizzCouple.MEANINGS_TO_KANJI, 0);
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_MEANINGS, 0);
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_READINGS, 0);
			stats.get(n).secondHandError.put(QuizzCouple.READINGS_TO_KANJI, 0);
			stats.get(n).secondHandError.put(QuizzCouple.MEANINGS_TO_KANJI, 0);
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_MEANINGS, 0d);
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_READINGS, 0d);
			stats.get(n).firstHandSuccess.put(QuizzCouple.READINGS_TO_KANJI, 0d);
			stats.get(n).firstHandSuccess.put(QuizzCouple.MEANINGS_TO_KANJI, 0d);
		}
	}

	public void addSuccess(QuizzCouple qc, Kanji answer, double s) {
		Integer n = answer.getNumber();
		Date now = (new Date());
		
		init(n, now);
		if(stats.get(n).lastError < 0)
			stats.get(n).lastError = 0l;
		
		stats.get(n).lastSuccess = now.getTime();
		stats.get(n).firstHandSuccess.put(qc, 
				stats.get(n).firstHandSuccess.get(qc) + s);
	}

	public void addError(QuizzCouple qc, Kanji answer, Kanji choosen) {
		Integer n = answer.getNumber();
		Integer c = choosen.getNumber();
		Date now = (new Date());
		
		init(n, now);
		init(c, now);
		if(stats.get(n).lastSuccess < 0)
			stats.get(n).lastSuccess = 0l;
		if(stats.get(c).lastSuccess < 0)
			stats.get(c).lastSuccess = 0l;

		stats.get(n).lastError = now.getTime();
		stats.get(c).lastError = now.getTime();
		stats.get(n).firstHandError.put(qc,
				stats.get(n).firstHandError.get(qc) + 1);
		stats.get(c).secondHandError.put(qc,
				stats.get(c).secondHandError.get(qc) + 1);
	}

	public long getLastSuccess(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		if ( si != null && si.lastSuccess >= 0 )
			return si.lastSuccess;
		else
			return Long.valueOf(0);
	}
	
	public long getLastError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		if ( si != null && si.lastError >= 0 )
			return si.lastError;
		else
			return new Date().getTime();
	}
	
	public double getFirstHandSuccess(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		Double sum = 0d;
		
		if ( si != null ) {	
			for ( Double i : si.firstHandSuccess.values() ) {
				sum += i;
			}
		}
		
		return sum;
	}
	
	public double getFirstHandSuccess(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.firstHandSuccess.get(qc) != null ) {
				return si.firstHandSuccess.get(qc);
			}
		}
		
		return 0;
	}
	
	public int getFirstHandError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		Integer sum = 0;
		
		if ( si != null ) {
			for ( Integer i : si.firstHandError.values() ) {
				sum += i;
			}
		}
		
		return sum;
	}
	
	public int getFirstHandError(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.firstHandError.get(qc) != null ) {
				return si.firstHandError.get(qc);
			}
		}
		
		return 0;
	}

	public int getSecondHandError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		Integer sum = 0;
		
		if ( si != null ) {
			for ( Integer i : si.secondHandError.values() ) {
				sum += i;
			}
		}
		
		return sum;
	}
	
	public int getSecondHandError(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.secondHandError.get(qc) != null ) {
				return si.secondHandError.get(qc);
			}
		}
		
		return 0;
	}

	/**
	 * Encode the statistics to a CSV-like format
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for (StatItem si : stats.values()) {
			String s = "";
			s += si.kanjiNumber + ", ";
			
			s += si.lastSuccess + ", ";
			s += si.lastError + ", ";

			s += si.firstHandSuccess.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			s += si.firstHandSuccess.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			s += si.firstHandSuccess.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			s += si.firstHandSuccess.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ ", ";

			s += si.firstHandError.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			s += si.firstHandError.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			s += si.firstHandError.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			s += si.firstHandError.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ ", ";

			s += si.secondHandError.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			s += si.secondHandError.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			s += si.secondHandError.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			s += si.secondHandError.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ " eol\n";
			
			result.append(s);
		}

		return result.toString();
	}

	/**
	 * Initialize statistics from a CSV-like format
	 * @param csv	List of statistics
	 */
	public Stats(String csv) {
		if (csv == null)
			return;

		final String[] lines = csv.split("eol");
		final Date now = new Date();
		
		for (String line : lines) {
			String items[] = line.replace(" ", "").replace("\n", "").split(",");
			if ( items.length == 0 || items[0].isEmpty() ) continue;
			Integer n = (Integer.parseInt(items[0]));

			init(n, now);
			
			stats.get(n).lastSuccess = Long.parseLong(items[1]);
			stats.get(n).lastError = Long.parseLong(items[2]);

			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_MEANINGS,
					Double.parseDouble(items[2 + 1]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_READINGS,
					Double.parseDouble(items[2 + 2]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.READINGS_TO_KANJI,
					Double.parseDouble(items[2 + 3]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.MEANINGS_TO_KANJI,
					Double.parseDouble(items[2 + 4]));

			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[6 + 1]));
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[6 + 2]));
			stats.get(n).firstHandError.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[6 + 3]));
			stats.get(n).firstHandError.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[6 + 4]));

			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[10 + 1]));
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[10 + 2]));
			stats.get(n).secondHandError.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[10 + 3]));
			stats.get(n).secondHandError.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[10 + 4]));
		}
	}

	public Stats() {
	}
}
