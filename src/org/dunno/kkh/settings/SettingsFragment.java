package org.dunno.kkh.settings;

import org.dunno.kkh.R;

//import android.preference.SeekBarPreference;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SettingsFragment extends PreferenceFragment {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	
		final int size = getPreferenceManager().getSharedPreferences().getInt("size", 0);
		
		//((SeekBarPreference)getPreferenceScreen().findPreference("pref_start")).setMax(size);
		//((SeekBarPreference)getPreferenceScreen().findPreference("pref_end")).setMax(size);
		
		getPreferenceScreen().findPreference("pref_start").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Integer start = (Integer) newValue;
				Integer end = getPreferenceManager().getSharedPreferences().getInt("pref_end", start);
				
				if ( start < 1 + end )
					return true;
				
				// start = end - 1;
				// should update here
				
				return false;
			}
		});
		
		getPreferenceScreen().findPreference("pref_end").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Integer end = (Integer) newValue;
				Integer start = getPreferenceManager().getSharedPreferences().getInt("pref_start", 0);
				
				if (  end <= size && end > 1 + start )
					return true;
				
				// end = end < start+1 ? start + 1 : end; 
				// end = end > size ? size : end;
				// should update here
			
				return false;
			}
		});
	}
}