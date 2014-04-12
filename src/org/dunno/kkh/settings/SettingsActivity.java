package org.dunno.kkh.settings;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public static final String 
		START="pref_start", 
		END="pref_end", 
		SHOW_RESUME_SUCCESS="pref_show_resume_success", 
		SHOW_RESUME_FAILURE="pref_show_resume_failure",
		SIZE="size",
		SCORE="score",
		BEST="best"
	;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}