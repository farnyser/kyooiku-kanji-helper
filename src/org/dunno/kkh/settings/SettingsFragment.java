package org.dunno.kkh.settings;

import org.dunno.kkh.R;

//import android.preference.SeekBarPreference;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

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
				
				if ( start < 1 + end ) {
					Toast.makeText(getActivity(), String.valueOf(start), Toast.LENGTH_SHORT).show();
					return true;
				}
					
				// start = end - 1;
				// should update here
				Toast.makeText(getActivity(), "Start cannot be greater than end", Toast.LENGTH_SHORT).show();

				return false;
			}
		});
		
		getPreferenceScreen().findPreference("pref_end").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Integer end = (Integer) newValue;
				Integer start = getPreferenceManager().getSharedPreferences().getInt("pref_start", 0);
				
				if (  end <= size && end > 1 + start ){
					Toast.makeText(getActivity(), String.valueOf(end), Toast.LENGTH_SHORT).show();
					return true;
				}
				
				// end = end < start+1 ? start + 1 : end; 
				// end = end > size ? size : end;
				// should update here
				Toast.makeText(getActivity(), "End cannot be lower than start", Toast.LENGTH_SHORT).show();

				return false;
			}
		});
	}
}