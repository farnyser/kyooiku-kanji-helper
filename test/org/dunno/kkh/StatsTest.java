package org.dunno.kkh;

import java.util.Date;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.QuizzCouple;
import org.dunno.kkh.models.Stats;

import junit.framework.TestCase;

public class StatsTest extends TestCase {

	public void testAddSuccess() {
		Stats stats = new Stats();
		Kanji k = new Kanji(0, 1, "A", "A meaning", "A on reading", "A kun reading");
		Kanji kk = new Kanji(1, 1, "B", "B meaning", "B on reading", "B kun reading");
		long now = new Date().getTime();
		
		assertTrue(Math.abs(stats.getLastError(k) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(k));
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0.0, stats.getFirstHandSuccess(k));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addSuccess(QuizzCouple.KANJI_TO_MEANINGS, k, 0.2);
		
		assertTrue(Math.abs(stats.getLastSuccess(k) - now) < 2*1000);
		assertEquals(0, stats.getLastError(k));
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0.2, stats.getFirstHandSuccess(k));
		assertEquals(0.2, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addSuccess(QuizzCouple.KANJI_TO_READINGS, k, 0.15);
		
		assertTrue(Math.abs(stats.getLastSuccess(k) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0, stats.getLastError(k));
		assertEquals(0.35, stats.getFirstHandSuccess(k));
		assertEquals(0.2, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0.15, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addSuccess(QuizzCouple.READINGS_TO_KANJI, k, 0.35);
		
		assertTrue(Math.abs(stats.getLastSuccess(k) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0, stats.getLastError(k));
		assertEquals(0.70, stats.getFirstHandSuccess(k));
		assertEquals(0.2, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0.15, stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0.35, stats.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0.0, stats.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI));
	}

	public void testAddError() {
		Stats stats = new Stats();
		Kanji k = new Kanji(0, 1, "A", "A meaning", "A on reading", "A kun reading");
		Kanji kk = new Kanji(1, 1, "B", "B meaning", "B on reading", "B kun reading");
		long now = new Date().getTime();
		
		assertTrue(Math.abs(stats.getLastError(k) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(k));
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0, stats.getFirstHandError(k));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addError(QuizzCouple.KANJI_TO_MEANINGS, k, kk);
		
		assertTrue(Math.abs(stats.getLastError(k) - now) < 2*1000);
		assertTrue(Math.abs(stats.getLastError(kk) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(k));
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(1, stats.getFirstHandError(k));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addError(QuizzCouple.KANJI_TO_READINGS, k, kk);
		
		assertTrue(Math.abs(stats.getLastError(k) - now) < 2*1000);
		assertTrue(Math.abs(stats.getLastError(kk) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0, stats.getLastSuccess(k));
		assertEquals(2, stats.getFirstHandError(k));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		stats.addError(QuizzCouple.READINGS_TO_KANJI, k, kk);
		
		assertTrue(Math.abs(stats.getLastError(k) - now) < 2*1000);
		assertTrue(Math.abs(stats.getLastError(kk) - now) < 2*1000);
		assertEquals(0, stats.getLastSuccess(kk));
		assertEquals(0, stats.getLastSuccess(k));
		assertEquals(3, stats.getFirstHandError(k));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(1, stats.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(0, stats.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
	}

	public void testStatsString() {
		Stats stats = new Stats();
		Kanji k = new Kanji(0, 1, "A", "A meaning", "A on reading", "A kun reading");
		Kanji kk = new Kanji(1, 1, "B", "B meaning", "B on reading", "B kun reading");
		long now = new Date().getTime();
		
		stats.addError(QuizzCouple.KANJI_TO_MEANINGS, k, kk);
		stats.addError(QuizzCouple.KANJI_TO_READINGS, k, kk);		
		stats.addError(QuizzCouple.READINGS_TO_KANJI, k, kk);
		stats.addSuccess(QuizzCouple.KANJI_TO_MEANINGS, kk, 0.2);		
		stats.addSuccess(QuizzCouple.KANJI_TO_READINGS, kk, 0.15);		
		stats.addSuccess(QuizzCouple.READINGS_TO_KANJI, kk, 0.35);
		
		Stats statsImported = new Stats(stats.toString());
		
		assertEquals(statsImported.getLastSuccess(kk), stats.getLastSuccess(kk));
		assertEquals(statsImported.getLastSuccess(k), stats.getLastSuccess(k));

		assertEquals(statsImported.getFirstHandSuccess(k), stats.getFirstHandSuccess(k));
		assertEquals(statsImported.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS), stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS), stats.getFirstHandSuccess(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI), stats.getFirstHandSuccess(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI), stats.getFirstHandSuccess(k, QuizzCouple.MEANINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandError(k), stats.getFirstHandError(k));
		assertEquals(statsImported.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS), stats.getFirstHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS), stats.getFirstHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI), stats.getFirstHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI), stats.getFirstHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
		assertEquals(statsImported.getSecondHandError(k), stats.getSecondHandError(k));
		assertEquals(statsImported.getSecondHandError(k, QuizzCouple.KANJI_TO_MEANINGS), stats.getSecondHandError(k, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getSecondHandError(k, QuizzCouple.KANJI_TO_READINGS), stats.getSecondHandError(k, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getSecondHandError(k, QuizzCouple.READINGS_TO_KANJI), stats.getSecondHandError(k, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getSecondHandError(k, QuizzCouple.MEANINGS_TO_KANJI), stats.getSecondHandError(k, QuizzCouple.MEANINGS_TO_KANJI));
		
		assertEquals(statsImported.getFirstHandSuccess(kk), stats.getFirstHandSuccess(kk));
		assertEquals(statsImported.getFirstHandSuccess(kk, QuizzCouple.KANJI_TO_MEANINGS), stats.getFirstHandSuccess(kk, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getFirstHandSuccess(kk, QuizzCouple.KANJI_TO_READINGS), stats.getFirstHandSuccess(kk, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getFirstHandSuccess(kk, QuizzCouple.READINGS_TO_KANJI), stats.getFirstHandSuccess(kk, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandSuccess(kk, QuizzCouple.MEANINGS_TO_KANJI), stats.getFirstHandSuccess(kk, QuizzCouple.MEANINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandError(kk), stats.getFirstHandError(kk));
		assertEquals(statsImported.getFirstHandError(kk, QuizzCouple.KANJI_TO_MEANINGS), stats.getFirstHandError(kk, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getFirstHandError(kk, QuizzCouple.KANJI_TO_READINGS), stats.getFirstHandError(kk, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getFirstHandError(kk, QuizzCouple.READINGS_TO_KANJI), stats.getFirstHandError(kk, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getFirstHandError(kk, QuizzCouple.MEANINGS_TO_KANJI), stats.getFirstHandError(kk, QuizzCouple.MEANINGS_TO_KANJI));
		assertEquals(statsImported.getSecondHandError(kk), stats.getSecondHandError(kk));
		assertEquals(statsImported.getSecondHandError(kk, QuizzCouple.KANJI_TO_MEANINGS), stats.getSecondHandError(kk, QuizzCouple.KANJI_TO_MEANINGS));
		assertEquals(statsImported.getSecondHandError(kk, QuizzCouple.KANJI_TO_READINGS), stats.getSecondHandError(kk, QuizzCouple.KANJI_TO_READINGS));
		assertEquals(statsImported.getSecondHandError(kk, QuizzCouple.READINGS_TO_KANJI), stats.getSecondHandError(kk, QuizzCouple.READINGS_TO_KANJI));
		assertEquals(statsImported.getSecondHandError(kk, QuizzCouple.MEANINGS_TO_KANJI), stats.getSecondHandError(kk, QuizzCouple.MEANINGS_TO_KANJI));

	}
}
