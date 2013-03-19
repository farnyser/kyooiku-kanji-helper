package org.dunno.kkh;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.pickers.PickerInterface.QuizzCouple;
import org.dunno.kkh.pickers.RandomPicker;
import org.dunno.kkh.utils.ReadCSV;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final TextView tv = (TextView) findViewById(R.id.textView);
		final GridLayout gv = (GridLayout) findViewById(R.id.gridView);
		
		if ( savedInstanceState == null ) {
			KanjiSet ks = ReadCSV.getKanjiSet(getApplicationContext(), R.raw.kanji);
			RandomPicker picker = new RandomPicker();
			
			Kanji answer = picker.pickKanji(ks);
			KanjiSet choices = picker.pickChoices(ks, answer);
			QuizzCouple qc = picker.pickQuizzCouple(ks, answer);
			
			Log.v("KKH", "Answer should be : " + answer.toString());
			Log.v("KKH", "QuizzCouple selected is : " + qc.toString());
			for ( Kanji k : choices.getAll() ) {
				Log.v("KKH", "Choices : " + k.toString());
			}
			
			// set question
			tv.setText(getFirst(answer, qc));
			
			// set reply
			for ( Kanji k : choices.getAll() ) {
				TextView text = new TextView(getApplicationContext());
				text.setText(getSecond(k, qc));
				gv.addView(text);
			}
		}
	}

	private String getFirst(Kanji k, QuizzCouple qc) {
		switch ( qc ) {
			case KANJI_TO_MEANINGS:
				return k.getCharacter();
			case KANJI_TO_READINGS:
				return k.getCharacter();
			case MEANINGS_TO_KANJI:
				return k.getMeaning();
			case READINGS_TO_KANJI:
				return k.getOnReading() + " / " + k.getKunReading();
			default:
				return "";
		}
	}
	
	private String getSecond(Kanji k, QuizzCouple qc) {
		switch ( qc ) {
			case KANJI_TO_MEANINGS:
				return k.getMeaning();
			case KANJI_TO_READINGS:
				return k.getOnReading() + " / " + k.getKunReading();
			case MEANINGS_TO_KANJI:
				return k.getCharacter();
			case READINGS_TO_KANJI:
				return k.getCharacter();
			default:
				return "";
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
