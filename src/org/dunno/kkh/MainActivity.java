package org.dunno.kkh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import org.dunno.kkh.androtools.ObjectAdapter;
import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;
import org.dunno.kkh.models.Stats;
import org.dunno.kkh.models.QuizzCouple;
import org.dunno.kkh.pickers.PickerInterface;
import org.dunno.kkh.pickers.SmartPicker;
import org.dunno.kkh.settings.SettingsActivity;
import org.dunno.kkh.utils.FIlter;
import org.dunno.kkh.utils.ReadCSV;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int SETTINGS_REQUEST = 1;

	static ObjectAdapter adapter;
		
	Kanji answer;
	KanjiSet fullks, ks, choices;
	PickerInterface picker;
	QuizzCouple qc;
	Stats stats;

	int start, end, size, count;
	boolean showResumeOnSuccess;
	boolean showResumeOnFailure;
	boolean locked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_main);
		
		Log.v("MainActivity", "onCreate");
		
		final SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		
		if (adapter == null) {
			Log.v("MainActivity", "create adapter");
			
			adapter = new ObjectAdapter();
			locked = false;
			
			((Button) findViewById(R.id.settings)).setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	     			Intent showSettings = new Intent(MainActivity.this,
	    					SettingsActivity.class);
	    			MainActivity.this.startActivityForResult(showSettings,
	    					SETTINGS_REQUEST);
	             }
	         });			
			((Button) findViewById(R.id.settings)).setText(toSmallCapital(getResources().getString(R.string.title)));
			
			
			((GridView) findViewById(R.id.gridView)).setAdapter(adapter);
			fullks = ReadCSV.getKanjiSet(getApplicationContext(), R.raw.kanji);
			ks = FIlter.getRange(fullks, start, end);
			//picker = new RandomPicker();
			
			size = fullks.getAll().size();
			sharedPreferences.edit().putInt(SettingsActivity.SIZE, size).commit();
			
			try {
				stats = new Stats(read("stats.csv"));
			}
			catch ( Exception e ) {
				Log.e("Stats", "CSV read failed", e);
				stats = new Stats();
			}
			
			picker = new SmartPicker(stats);
		}

		if (
				start != sharedPreferences.getInt(SettingsActivity.START,0)
				|| end != sharedPreferences.getInt(SettingsActivity.END, 10)
				|| showResumeOnSuccess != sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_SUCCESS, false)
				|| showResumeOnFailure != sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_FAILURE, false)
		) {
			start = sharedPreferences.getInt(SettingsActivity.START, 0);
			end = sharedPreferences.getInt(SettingsActivity.END, 10);
			showResumeOnSuccess = sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_SUCCESS, false);
			showResumeOnFailure = sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_FAILURE, false);
			ks = FIlter.getRange(fullks, start, end);
			newChoice();
		}

		if (savedInstanceState == null) {
			newChoice();
			setScore(sharedPreferences, 0f);
			
			final GridView gv = (GridView) findViewById(R.id.gridView);
			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int item, long arg3) {
					if ( locked ) return;
					
					Kanji choosen = adapter.getItem(item);
					Log.v("click", "item selected: " + choosen);

					if (choosen.equals(answer)) {
						showSuccess(choosen, arg1);
						
						int range = Math.abs(end-start);
						incScore(sharedPreferences, size/(float)(size-range));
						stats.addSuccess(qc, answer, (range-count)/range);
						locked = true;
					} else {
						showError(choosen, arg1);
						
						count++;
						setScore(sharedPreferences, 0f);
						stats.addError(qc, answer, choosen);
						locked = false;
					}
					
					write("stats.csv", stats.toString());
				}
			});
		}
	}
	
	private void incScore(SharedPreferences sharedPreferences, float inc) {
		setScore(sharedPreferences, sharedPreferences.getFloat(SettingsActivity.SCORE, 0) + inc);
	}
	
	private void setScore(SharedPreferences sharedPreferences, float score) {
		if ( score > sharedPreferences.getFloat(SettingsActivity.BEST, 0) ) {
			sharedPreferences.edit().putFloat(SettingsActivity.BEST, score).commit();
		}
		
		final TextView sc = (TextView) findViewById(R.id.score);
		final TextView bc = (TextView) findViewById(R.id.best);
		sc.setText(toSmallCapital(getResources().getString(R.string.box_score) + "<br/>" + ((int) Math.floor(score))));
		bc.setText(toSmallCapital(getResources().getString(R.string.box_best) + "<br/>" + ((int) Math.floor(sharedPreferences.getFloat("best", 0)))));
		sharedPreferences.edit().putFloat("score", score).commit();
	}

	private void showSuccess(Kanji k, final View v) {
		v.setBackground(getResources().getDrawable(R.drawable.success));
		
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	newChoice();
            	locked = false;
            }
        }, 500);
        
		if ( showResumeOnSuccess )
			showCard(k);
	}
	
	private void showError(Kanji k, final View v) {
		v.setBackground(getResources().getDrawable(R.drawable.error));
				
        
		if ( showResumeOnFailure )
			showCard(k);
	}
	
	private void showCard(Kanji k) {
		String resume = "";
		resume += "<big><b>" + k.getCharacter() + "</b></big>";
		resume += " " + k.getMeaning();
		resume += "<br />";
		resume += k.getOnReading() + " / " + k.getKunReading();
		Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(resume), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		Log.v("MainActivity", "onWindowFocusChanged");

		if (hasFocus) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (
				start != sharedPreferences.getInt(SettingsActivity.START, 0) 
				|| end != sharedPreferences.getInt(SettingsActivity.END, 10)
				|| showResumeOnSuccess != sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_SUCCESS, false)
				|| showResumeOnFailure != sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_FAILURE, false)
			) {
				start = sharedPreferences.getInt(SettingsActivity.START, 0);
				end = sharedPreferences.getInt(SettingsActivity.END, 10);
				showResumeOnSuccess = sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_SUCCESS, false);
				showResumeOnFailure = sharedPreferences.getBoolean(SettingsActivity.SHOW_RESUME_FAILURE, false);
				ks = FIlter.getRange(fullks, start, end);
				newChoice();
			}
		}
	}
	
	private int getFirstApparance(Kanji k, QuizzCouple qc) {
		switch (qc) {
		case KANJI_TO_MEANINGS:
		case KANJI_TO_READINGS:
			return android.R.attr.textAppearanceLarge;
		default:
			return android.R.attr.textAppearanceMedium;
		}
	}
	
	private int getSecondApparance(Kanji k, QuizzCouple qc) {
		switch (qc) {
		case KANJI_TO_MEANINGS:
		case KANJI_TO_READINGS:
			return android.R.attr.textAppearanceSmall;
		default:
			return android.R.attr.textAppearanceMedium;
		}
	}

	private void newChoice() {
		Log.d("MainActivity", "newChoice");
		final TextView tv = (TextView) findViewById(R.id.textView);
		count = 0;
		
		answer = picker.pickKanji(ks);
		choices = picker.pickChoices(ks, answer);
		qc = picker.pickQuizzCouple(ks, answer);

		// set question
		tv.setText(qc.firstAsString(answer));
		tv.setTextAppearance(getApplicationContext(), getFirstApparance(answer, qc));

		// set reply
		ArrayList<String> items = new ArrayList<String>();
		for (Kanji k : choices.getAll()) {
			items.add(qc.secondAsString(k));
		}
		adapter.updateContent(choices.getAll(), items, getSecondApparance(answer, qc));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	
	private Spanned toSmallCapital(String str) {
		str = str.toUpperCase(Locale.ENGLISH);
		if ( !str.isEmpty() ) 
			str = str.substring(0, 1) + "<small>" + str.substring(1) + "</small>"; 
		return Html.fromHtml(str);
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
