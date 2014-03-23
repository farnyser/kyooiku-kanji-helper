package org.dunno.kkh.models;

import android.annotation.SuppressLint;

import java.util.Date;
import java.util.HashMap;

import org.dunno.kkh.pickers.PickerInterface.QuizzCouple;

public class Stats {
	private class StatItem {
		public Integer kanjiNumber;
		public Long lastSuccess;
		public Long lastError;
		public HashMap<QuizzCouple, Integer> firstHandError = new HashMap<QuizzCouple, Integer>();
		public HashMap<QuizzCouple, Integer> secondHandError = new HashMap<QuizzCouple, Integer>();
		public HashMap<QuizzCouple, Integer> firstHandSuccess = new HashMap<QuizzCouple, Integer>();

		public StatItem(int n) {
			this.kanjiNumber = n;
			this.lastError = (new Date()).getTime();
			this.lastSuccess = Long.valueOf(0);
		}
	}

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, StatItem> stats = new HashMap<Integer, StatItem>();

	private void init(int n) {
		if (stats.get(n) == null) {
			stats.put(n, new StatItem(n));
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_MEANINGS, 0);
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_READINGS, 0);
			stats.get(n).firstHandError.put(QuizzCouple.READINGS_TO_KANJI, 0);
			stats.get(n).firstHandError.put(QuizzCouple.MEANINGS_TO_KANJI, 0);
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_MEANINGS, 0);
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_READINGS, 0);
			stats.get(n).secondHandError.put(QuizzCouple.READINGS_TO_KANJI, 0);
			stats.get(n).secondHandError.put(QuizzCouple.MEANINGS_TO_KANJI, 0);
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_MEANINGS, 0);
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_READINGS, 0);
			stats.get(n).firstHandSuccess.put(QuizzCouple.READINGS_TO_KANJI, 0);
			stats.get(n).firstHandSuccess.put(QuizzCouple.MEANINGS_TO_KANJI, 0);
		}
	}

	public void addSuccess(QuizzCouple qc, Kanji answer) {
		Integer n = answer.getNumber();
		init(n);
		stats.get(n).lastSuccess = (new Date()).getTime();
		stats.get(n).firstHandSuccess.put(qc,
				stats.get(n).firstHandSuccess.get(qc) + 1);
	}

	public void addError(QuizzCouple qc, Kanji answer, Kanji choosen) {
		Integer n = answer.getNumber();
		Integer c = choosen.getNumber();
		init(n);
		init(c);
		stats.get(n).lastError = (new Date()).getTime();
		stats.get(c).lastError = (new Date()).getTime();
		stats.get(n).firstHandError.put(qc,
				stats.get(n).firstHandError.get(qc) + 1);
		stats.get(c).secondHandError.put(qc,
				stats.get(c).secondHandError.get(qc) + 1);
	}

	public Long getLastSuccess(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		if ( si != null )
			return si.lastSuccess;
		else
			return Long.valueOf(0);
	}
	
	public Long getLastError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		if ( si != null )
			return si.lastError;
		else
			return Long.valueOf(0);
	}
	
	public Integer getFirstHandSuccess(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {
			Integer sum = 0;
			
			for ( Integer i : si.firstHandSuccess.values() ) {
				sum += i;
			}
			
			return sum;
		}
		
		return 0;
	}
	
	public Integer getFirstHandSuccess(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.firstHandSuccess.get(qc) != null ) {
				return si.firstHandSuccess.get(qc);
			}
		}
		
		return 0;
	}
	
	public Integer getFirstHandError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {
			Integer sum = 0;
			
			for ( Integer i : si.firstHandError.values() ) {
				sum += i;
			}
			
			return sum;
		}
		
		return 0;
	}
	
	public Integer getFirstHandError(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.firstHandError.get(qc) != null ) {
				return si.firstHandError.get(qc);
			}
		}
		
		return 0;
	}

	public Integer getSecondHandError(Kanji k) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {
			Integer sum = 0;
			
			for ( Integer i : si.secondHandError.values() ) {
				sum += i;
			}
			
			return sum;
		}
		
		return 0;
	}
	
	public Integer getSecondHandError(Kanji k, QuizzCouple qc) {
		StatItem si = stats.get(k.getNumber());
		
		if ( si != null ) {			
			if ( si.secondHandError.get(qc) != null ) {
				return si.secondHandError.get(qc);
			}
		}
		
		return 0;
	}

	public String toString() {
		String result = "";

		for (StatItem si : stats.values()) {
			result += si.kanjiNumber + ", ";
			
			result += si.lastSuccess + ", ";
			result += si.lastError + ", ";

			result += si.firstHandSuccess.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			result += si.firstHandSuccess.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			result += si.firstHandSuccess.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			result += si.firstHandSuccess.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ ", ";

			result += si.firstHandError.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			result += si.firstHandError.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			result += si.firstHandError.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			result += si.firstHandError.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ ", ";

			result += si.secondHandError.get(QuizzCouple.KANJI_TO_MEANINGS)
					+ ", ";
			result += si.secondHandError.get(QuizzCouple.KANJI_TO_READINGS)
					+ ", ";
			result += si.secondHandError.get(QuizzCouple.READINGS_TO_KANJI)
					+ ", ";
			result += si.secondHandError.get(QuizzCouple.MEANINGS_TO_KANJI)
					+ " eol\n";
		}

		return result;
	}

	public Stats(String csv) {
		if (csv == null)
			return;

		final String[] lines = csv.split("eol");

		for (String line : lines) {
			String items[] = line.replace(" ", "").split(",");
			Integer n = (Integer.parseInt(items[0]));

			init(n);
			
			stats.get(n).lastSuccess = Long.parseLong(items[1]);
			stats.get(n).lastError = Long.parseLong(items[2]);

			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[2 + 1]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[2 + 2]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[2 + 3]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[2 + 4]));

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
