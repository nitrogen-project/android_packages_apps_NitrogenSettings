/*
 * Copyright (C) 2016 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nitrogen.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.nitrogen.settings.preferences.CustomSeekBarPreference;

import java.util.Date;

public class StatusbarExpandedQSSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_LANDSCAPE =
            "qs_cat_landscape";
    private static final String PREF_ROWS_PORTRAIT =
            "qs_rows_portrait";
    private static final String PREF_COLUMNS_PORTRAIT =
            "qs_columns_portrait";
    private static final String PREF_ROWS_LANDSCAPE =
            "qs_rows_landscape";
    private static final String PREF_COLUMNS_LANDSCAPE =
            "qs_columns_landscape";

    private CustomSeekBarPreference mRowsPortrait;
    private CustomSeekBarPreference mColumnsPortrait;
    private CustomSeekBarPreference mRowsLandscape;
    private CustomSeekBarPreference mColumnsLandscape;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.status_bar_expanded_qs);

        mResolver = getContentResolver();
        Resources res = getResources();

        PreferenceCategory catLandscape =
                (PreferenceCategory) findPreference(PREF_CAT_LANDSCAPE);

        int defaultValue;

        mRowsPortrait =
                (CustomSeekBarPreference) findPreference(PREF_ROWS_PORTRAIT);
        int rowsPortrait = Settings.System.getInt(mResolver,
                Settings.System.QS_ROWS_PORTRAIT, 3);
        mRowsPortrait.setValue(rowsPortrait / 1);
        mRowsPortrait.setOnPreferenceChangeListener(this);

        mColumnsPortrait =
                (CustomSeekBarPreference) findPreference(PREF_COLUMNS_PORTRAIT);
        int columnsPortrait = Settings.System.getInt(mResolver,
                Settings.System.QS_COLUMNS_PORTRAIT, 5);
        mColumnsPortrait.setValue(columnsPortrait / 1);
        mColumnsPortrait.setOnPreferenceChangeListener(this);

        defaultValue = res.getInteger(R.integer.config_qs_num_rows_landscape_default);
        if (defaultValue != 1) {
            mRowsLandscape =
                    (CustomSeekBarPreference) findPreference(PREF_ROWS_LANDSCAPE);
            int rowsLandscape = Settings.System.getInt(mResolver,
                    Settings.System.QS_ROWS_LANDSCAPE, defaultValue);
            mRowsLandscape.setValue(rowsLandscape / 1);
            mRowsLandscape.setOnPreferenceChangeListener(this);
        } else {
            catLandscape.removePreference(findPreference(PREF_ROWS_LANDSCAPE));
        }

        mColumnsLandscape =
                (CustomSeekBarPreference) findPreference(PREF_COLUMNS_LANDSCAPE);
        defaultValue = res.getInteger(R.integer.config_qs_num_columns_landscape_default);
        int columnsLandscape = Settings.System.getInt(mResolver,
                Settings.System.QS_COLUMNS_LANDSCAPE, defaultValue);
        mColumnsLandscape.setValue(columnsLandscape / 1);
        mColumnsLandscape.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;

        if (preference == mRowsPortrait) {
            int rowsPortrait = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_ROWS_PORTRAIT, rowsPortrait * 1);
            return true;
        } else if (preference == mColumnsPortrait) {
            int columnsPortrait = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_COLUMNS_PORTRAIT, columnsPortrait * 1);
            return true;
        } else if (preference == mRowsLandscape) {
            int rowsLandscape = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_ROWS_LANDSCAPE, rowsLandscape * 1);
            return true;
        } else if (preference == mColumnsLandscape) {
            int columnsLandscape = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_COLUMNS_LANDSCAPE, columnsLandscape * 1);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.NITROGEN_SETTINGS;
    }
}
