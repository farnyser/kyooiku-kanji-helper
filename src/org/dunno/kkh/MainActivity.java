package org.dunno.kkh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dunno.kkh.androtools.ObjectAdapter;
import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.Stats;
import org.dunno.kkh.pickers.PickerInterface;
import org.dunno.kkh.pickers.SmartPicker;
import org.dunno.kkh.pickers.PickerInterface.QuizzCouple;
import org.dunno.kkh.pickers.RandomPicker;
import org.dunno.kkh.settings.SettingsActivity;
import org.dunno.kkh.utils.FIlter;
import org.dunno.kkh.utils.ReadCSV;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int SETTINGS_REQUEST = 1;

	static ObjectAdapter adapter;
	Kanji answer;
	KanjiSet fullks, ks, choices;
	PickerInterface picker;
	QuizzCouple qc;
	Stats stats;

	int start, end;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (adapter == null) {
			adapter = new ObjectAdapter();
			((GridView) findViewById(R.id.gridView)).setAdapter(adapter);
			fullks = ReadCSV.getKanjiSet(getApplicationContext(), R.raw.kanji);
			ks = FIlter.getRange(fullks, start, end);
			//picker = new RandomPicker();
			stats = new Stats(read("stats.csv"));
			picker = new SmartPicker(stats);
		}

		if (start != Integer.parseInt(sharedPreferences.getString("pref_start",
				"0"))
				|| end != Integer.parseInt(sharedPreferences.getString(
						"pref_end", "10"))) {
			start = Integer.parseInt(sharedPreferences.getString("pref_start",
					"0"));
			end = Integer.parseInt(sharedPreferences
					.getString("pref_end", "10"));
			ks = FIlter.getRange(fullks, start, end);
			newChoice();
		}

		if (savedInstanceState == null) {
			newChoice();

			final GridView gv = (GridView) findViewById(R.id.gridView);
			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int item, long arg3) {
					Kanji choosen = adapter.getItem(item);
					Log.v("click", "item selected: " + choosen);

					if (choosen.equals(answer)) {
						stats.addSuccess(qc, answer);
						Toast.makeText(getApplicationContext(), "OK !",
								Toast.LENGTH_SHORT).show();
						newChoice();
					} else {
						stats.addError(qc, answer, choosen);
						Toast.makeText(getApplicationContext(), "WRONG !",
								Toast.LENGTH_SHORT).show();
					}
					
					write("stats.csv", stats.toString());
				}
			});
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (start != Integer.parseInt(sharedPreferences.getString(
					"pref_start", "0"))
					|| end != Integer.parseInt(sharedPreferences.getString(
							"pref_end", "10"))) {
				start = Integer.parseInt(sharedPreferences.getString(
						"pref_start", "0"));
				end = Integer.parseInt(sharedPreferences.getString("pref_end",
						"10"));
				ks = FIlter.getRange(fullks, start, end);
				newChoice();
			}
		}
	}

	private String getFirst(Kanji k, QuizzCouple qc) {
		switch (qc) {
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
		switch (qc) {
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
		for (Kanji k : choices.getAll()) {
			items.add(getSecond(k, qc));
		}
		adapter.updateContent(choices.getAll(), items);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent showSettings = new Intent(MainActivity.this,
					SettingsActivity.class);
			MainActivity.this.startActivityForResult(showSettings,
					SETTINGS_REQUEST);
			return true;
		}

		return false;
	}

	/**
	 * This method write a String in a file
	 * 
	 * @param filename
	 *            Path to the file to write to
	 * @param data
	 *            New content of the file
	 */
	private void write(String filename, String data) {
		FileOutputStream fos;

		try {
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("app", "write", e);
		} catch (IOException e) {
			Log.e("app", "write", e);
		}
	}

	/**
	 * This method read a file and return its content as a String
	 * 
	 * @param filename
	 *            Path to the file to read
	 * @return the file's content, or null
	 */
	private String read(String filename) {
		FileInputStream in;

		try {
			in = openFileInput(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			Log.e("app", "write", e);
		} catch (IOException e) {
			Log.e("app", "write", e);
		}

		return null;
	}
}
