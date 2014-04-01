package com.scar45.cryptoGlance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class ShowSettingsActivity extends PreferenceActivity implements
    OnSharedPreferenceChangeListener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.preferences);        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) 
    {
        Toast.makeText(getApplicationContext(),
        "Settings saved!",
        Toast.LENGTH_SHORT).show();
    }

}

