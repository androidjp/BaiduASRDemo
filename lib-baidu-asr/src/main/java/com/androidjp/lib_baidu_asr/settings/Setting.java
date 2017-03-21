package com.androidjp.lib_baidu_asr.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.androidjp.lib_baidu_asr.R;


public class Setting extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.setting);

        PreferenceScreen s = (PreferenceScreen) findPreference("root_screen");
        bind(s);
    }

    private void bind(PreferenceGroup group) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference p = group.getPreference(i);
            if (p instanceof PreferenceGroup) {
                bind((PreferenceGroup) p);
            } else {
                if (p instanceof CheckBoxPreference) {
                    ;
                } else {
                    Object val = sp.getAll().get(p.getKey());
                    p.setSummary(val == null ? "" : ("" + val));
                    p.setOnPreferenceChangeListener(this);
                }
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference p, Object newValue) {
        if (p instanceof CheckBoxPreference) {
            ;
        } else {
            p.setSummary("" + newValue);
        }
        return true;
    }
}
