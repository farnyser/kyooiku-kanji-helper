package org.dunno.kkh;

import java.util.ArrayList;

import org.dunno.kkh.androtools.ObjectAdapter;
import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.pickers.PickerInterface;
import org.dunno.kkh.pickers.PickerInterface.QuizzCouple;
import org.dunno.kkh.pickers.RandomPicker;
import org.dunno.kkh.utils.FIlter;
import org.dunno.kkh.utils.ReadCSV;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static ObjectAdapter adapter;
	Kanji answer;
	KanjiSet ks, choices;
	PickerInterface picker;
	QuizzCouple qc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        
		if ( adapter == null ) {
			adapter = new ObjectAdapter();
			((GridView) findViewById(R.id.gridView)).setAdapter(adapter);
			ks = ReadCSV.getKanjiSet(getApplicationContext(), R.raw.kanji);
			ks = FIlter.getNFirst(ks, 140);
			picker = new RandomPicker();
		}
		
		if ( savedInstanceState == null ) {
			newChoice();
			
			final GridView gv = (GridView) findViewById(R.id.gridView);
			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int item, long arg3) {
					Log.v("click","item selected: " + adapter.getItem(item));
					
					if ( adapter.getItem(item).equals(getSecond(answer, qc)) ) {
						Toast.makeText(getApplicationContext(), "OK !", Toast.LENGTH_SHORT).show();
						newChoice();
					}
					else
						Toast.makeText(getApplicationContext(), "WRONG !", Toast.LENGTH_SHORT).show();
					
				}
			});
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
	
	private void newChoice() {
		final TextView tv = (TextView) findViewById(R.id.textView);
		
		answer = picker.pickKanji(ks);
		choices = picker.pickChoices(ks, answer);
		qc = picker.pickQuizzCouple(ks, answer);
		
		
		// set question
		tv.setText(getFirst(answer, qc));
		
		// set reply
		ArrayList<String> items = new ArrayList<String>();
		for ( Kanji k : choices.getAll() ) {
			items.add(getSecond(k, qc));
		}
		adapter.updateContent(items);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
