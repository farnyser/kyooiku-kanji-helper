package org.dunno.kkh.models;

import android.annotation.SuppressLint;
import java.util.HashMap;

import org.dunno.kkh.pickers.PickerInterface.QuizzCouple;

public class Stats {
	private class StatItem {
		public Integer kanjiNumber;
		public HashMap<QuizzCouple, Integer> firstHandError = new HashMap<QuizzCouple, Integer>();
		public HashMap<QuizzCouple, Integer> secondHandError = new HashMap<QuizzCouple, Integer>();
		public HashMap<QuizzCouple, Integer> firstHandSuccess = new HashMap<QuizzCouple, Integer>();

		public StatItem(int n) {
			this.kanjiNumber = n;
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
		stats.get(n).firstHandSuccess.put(qc,
				stats.get(n).firstHandSuccess.get(qc) + 1);
	}

	public void addError(QuizzCouple qc, Kanji answer, Kanji choosen) {
		Integer n = answer.getNumber();
		Integer c = choosen.getNumber();
		init(n);
		init(c);
		stats.get(n).firstHandError.put(qc,
				stats.get(n).firstHandError.get(qc) + 1);
		stats.get(c).secondHandError.put(qc,
				stats.get(c).secondHandError.get(qc) + 1);
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
	
	public String toString() {
		String result = "";

		for (StatItem si : stats.values()) {
			result += si.kanjiNumber + ", ";

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

			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[1]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[2]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[3]));
			stats.get(n).firstHandSuccess.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[4]));

			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[4 + 1]));
			stats.get(n).firstHandError.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[4 + 2]));
			stats.get(n).firstHandError.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[4 + 3]));
			stats.get(n).firstHandError.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[4 + 4]));

			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_MEANINGS,
					Integer.parseInt(items[8 + 1]));
			stats.get(n).secondHandError.put(QuizzCouple.KANJI_TO_READINGS,
					Integer.parseInt(items[8 + 2]));
			stats.get(n).secondHandError.put(QuizzCouple.READINGS_TO_KANJI,
					Integer.parseInt(items[8 + 3]));
			stats.get(n).secondHandError.put(QuizzCouple.MEANINGS_TO_KANJI,
					Integer.parseInt(items[8 + 4]));
		}
	}

	public Stats() {
	}
}
