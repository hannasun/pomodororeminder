package com.hannasun.pomodororeminder.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.pomodoro.Pomodoro;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/27.
 */
public class PomodoroPreference extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Toolbar mToolbar;
    private SharedPreferences mPrefs ;
    private SliderPreference mTomatoDuration, mRestDuration;

    private HashMap<String, Preference> mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mPrefs = getPreferenceScreen().getSharedPreferences();

        mPreferences = new HashMap<String, Preference>();

        Preference pref;
        pref = findPreference(Pomodoro.PREF_REST_DURATION);
        fillInValue(pref, String.valueOf(mPrefs.getInt(Pomodoro.PREF_REST_DURATION, Pomodoro.PREF_REST_DURATION_DEFAULT)));
        mPreferences.put(Pomodoro.PREF_REST_DURATION, pref);

        pref = findPreference(Pomodoro.PREF_TOMATO_DURATION);
        fillInValue(pref, String.valueOf(mPrefs.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT)));
        mPreferences.put(Pomodoro.PREF_TOMATO_DURATION, pref);

        pref = findPreference(Pomodoro.PREF_REST_VOLUME);
        fillInValue(pref, String.valueOf(mPrefs.getInt(Pomodoro.PREF_REST_VOLUME, Pomodoro.PREF_REST_VOLUME_DEFAULT)));
        mPreferences.put(Pomodoro.PREF_REST_VOLUME, pref);

        pref = findPreference(Pomodoro.PREF_TOMATO_VOLUME);
        fillInValue(pref, String.valueOf(mPrefs.getInt(Pomodoro.PREF_TOMATO_VOLUME, Pomodoro.PREF_TOMATO_VOLUME_DEFAULT)));
        mPreferences.put(Pomodoro.PREF_TOMATO_VOLUME, pref);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = mPreferences.get(key);
        if(pref !=null) {
            fillInValue(pref, String.valueOf(sharedPreferences.getInt(key, 0)));
        }
    }

    private static void fillInValue(Preference pref, String value){
        String summary = pref.getSummary().toString();
        int index = summary.lastIndexOf('=');
        if(index != -1) {
            summary = summary.substring(0, index + 1).concat(" " + value);
        }
        pref.setSummary(summary);
    }
}
