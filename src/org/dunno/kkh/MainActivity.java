package org.dunno.kkh;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.pickers.RandomPicker;
import org.dunno.kkh.utils.ReadCSV;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if ( savedInstanceState == null ) {
			KanjiSet ks = ReadCSV.getKanjiSet(getApplicationContext(), R.raw.kanji);
			RandomPicker picker = new RandomPicker();
			
			Kanji answer = picker.pickKanji(ks);
			KanjiSet choices = picker.pickChoices(ks, answer);
			
			Log.v("KKH", "Answer should be : " + answer.toString());
			for ( Kanji k : choices.getAll() ) {
				Log.v("KKH", "Choices : " + k.toString());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
