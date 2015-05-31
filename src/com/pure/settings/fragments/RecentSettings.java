/*
 * Copyright (C) 2016 The Pure Nexus Project
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
package com.pure.settings.fragments;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;

import com.android.internal.logging.MetricsLogger;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.pure.settings.preferences.ColorPickerPreference;
import com.pure.settings.preferences.SeekBarPreference;

public class RecentSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String SHOW_RECENTS_SEARCHBAR = "recents_show_search_bar";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String RECENTS_DISMISS_ALL = "recents_clear_all_dismiss_all";
    private static final String USE_SLIM_RECENTS = "use_slim_recents";
    private static final String RECENTS_MAX_APPS = "recents_max_apps";
    private static final String RECENT_PANEL_SCALE = "recent_panel_scale_factor";
    private static final String RECENT_PANEL_EXPANDED_MODE = "recent_panel_expanded_mode";
    private static final String RECENT_PANEL_LEFTY_MODE = "recent_panel_lefty_mode";
    private static final String RECENT_PANEL_BG_COLOR = "recent_panel_bg_color";
    private static final String RECENT_CARD_BG_COLOR = "recent_card_bg_color";
    private static final String RECENT_CARD_TEXT_COLOR = "recent_card_text_color";

    private SwitchPreference mRecentsSearchBar;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private ListPreference mImmersiveRecents;
    private SwitchPreference mRecentsDismissAll;
    private SwitchPreference mUseSlimRecents;
    private SeekBarPreference mMaxApps;
    private SeekBarPreference mRecentPanelScale;
    private ListPreference mRecentPanelExpandedMode;
    private SwitchPreference mRecentPanelLeftyMode;
    private ColorPickerPreference mRecentPanelBgColor;
    private ColorPickerPreference mRecentCardBgColor;
    private ColorPickerPreference mRecentCardTextColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recent_settings);
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        mRecentsSearchBar = (SwitchPreference) prefSet.findPreference(SHOW_RECENTS_SEARCHBAR);
        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsDismissAll = (SwitchPreference) prefSet.findPreference(RECENTS_DISMISS_ALL);

        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getInt(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                resolver, Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mUseSlimRecents = (SwitchPreference) prefSet.findPreference(USE_SLIM_RECENTS);
        mUseSlimRecents.setChecked(Settings.System.getInt(resolver,
                Settings.System.USE_SLIM_RECENTS, 0) == 1);
        mUseSlimRecents.setOnPreferenceChangeListener(this);

        mMaxApps = (SeekBarPreference) findPreference(RECENTS_MAX_APPS);
        int maxApps = Settings.System.getInt(resolver,
                Settings.System.RECENTS_MAX_APPS, ActivityManager.getMaxRecentTasksStatic());
        mMaxApps.setValue(maxApps);
        mMaxApps.setOnPreferenceChangeListener(this);

        mRecentPanelScale = (SeekBarPreference) findPreference(RECENT_PANEL_SCALE);
        int recentPanelScale = Settings.System.getInt(resolver,
                Settings.System.RECENT_PANEL_SCALE_FACTOR, 100);
        mRecentPanelScale.setValue(recentPanelScale);
        mRecentPanelScale.setOnPreferenceChangeListener(this);

        mRecentPanelExpandedMode = (ListPreference) prefSet.findPreference(RECENT_PANEL_EXPANDED_MODE);
        int recentPanelExpandedMode = Settings.System.getIntForUser(resolver,
                Settings.System.RECENT_PANEL_EXPANDED_MODE, 0, UserHandle.USER_CURRENT);
        mRecentPanelExpandedMode.setValue(String.valueOf(recentPanelExpandedMode));
        mRecentPanelExpandedMode.setSummary(mRecentPanelExpandedMode.getEntry());
        mRecentPanelExpandedMode.setOnPreferenceChangeListener(this);

        mRecentPanelLeftyMode = (SwitchPreference) prefSet.findPreference(RECENT_PANEL_LEFTY_MODE);
        mRecentPanelLeftyMode.setChecked(Settings.System.getInt(resolver,
                Settings.System.RECENT_PANEL_GRAVITY, Gravity.RIGHT) == Gravity.LEFT);
        mRecentPanelLeftyMode.setOnPreferenceChangeListener(this);

        mRecentPanelBgColor = (ColorPickerPreference) findPreference(RECENT_PANEL_BG_COLOR);
        mRecentPanelBgColor.setOnPreferenceChangeListener(this);
        mRecentPanelBgColor.setSummary(mRecentPanelBgColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.System.getInt(resolver,
                     Settings.System.RECENT_PANEL_BG_COLOR, mRecentPanelBgColor.getPrefDefault())));
        mRecentPanelBgColor.setNewPreviewColor(Settings.System.getInt(resolver, Settings.System.RECENT_PANEL_BG_COLOR, mRecentPanelBgColor.getPrefDefault()));

        mRecentCardBgColor = (ColorPickerPreference) findPreference(RECENT_CARD_BG_COLOR);
        mRecentCardBgColor.setOnPreferenceChangeListener(this);
        mRecentCardBgColor.setSummary(mRecentCardBgColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.System.getInt(resolver,
                     Settings.System.RECENT_CARD_BG_COLOR, mRecentCardBgColor.getPrefDefault())));
        mRecentCardBgColor.setNewPreviewColor(Settings.System.getInt(resolver, Settings.System.RECENT_CARD_BG_COLOR, mRecentCardBgColor.getPrefDefault()));

        mRecentCardTextColor = (ColorPickerPreference) findPreference(RECENT_CARD_TEXT_COLOR);
        mRecentCardTextColor.setOnPreferenceChangeListener(this);
        mRecentCardTextColor.setSummary(mRecentCardTextColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.System.getInt(resolver,
                     Settings.System.RECENT_CARD_TEXT_COLOR, mRecentCardTextColor.getPrefDefault())));
        mRecentCardTextColor.setNewPreviewColor(Settings.System.getInt(resolver, Settings.System.RECENT_CARD_TEXT_COLOR, mRecentCardTextColor.getPrefDefault()));

        updatePreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreference();
    }

    private void updatePreference() {
        boolean slimRecent = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.USE_SLIM_RECENTS, 0) == 1;

        if (slimRecent) {
            mRecentsSearchBar.setEnabled(false);
            mRecentsClearAll.setEnabled(false);
            mRecentsClearAllLocation.setEnabled(false);
            mRecentsDismissAll.setEnabled(false);
            mImmersiveRecents.setEnabled(false);
            mRecentPanelBgColor.setPreviewDim(true);
            mRecentCardBgColor.setPreviewDim(true);
            mRecentCardTextColor.setPreviewDim(true);
        } else {
            mRecentsSearchBar.setEnabled(true);
            mRecentsClearAll.setEnabled(true);
            mRecentsClearAllLocation.setEnabled(true);
            mRecentsDismissAll.setEnabled(true);
            mImmersiveRecents.setEnabled(true);
            mRecentPanelBgColor.setPreviewDim(false);
            mRecentCardBgColor.setPreviewDim(false);
            mRecentCardTextColor.setPreviewDim(false);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.PURE_SETTINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mImmersiveRecents) {
            Settings.System.putInt(resolver, Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) newValue));
            mImmersiveRecents.setValue(String.valueOf(newValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference == mUseSlimRecents) {
            Settings.System.putInt(resolver, Settings.System.USE_SLIM_RECENTS,
                    ((Boolean) newValue) ? 1 : 0);
            updatePreference();
            return true;
        } else if (preference == mMaxApps) {
            int maxApps = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.RECENTS_MAX_APPS, maxApps);
            return true;
        } else if (preference == mRecentPanelScale) {
            int recentPanelScale = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.RECENT_PANEL_SCALE_FACTOR, recentPanelScale);
            return true;
        } else if (preference == mRecentPanelExpandedMode) {
            int recentPanelExpandedMode = Integer.valueOf((String) newValue);
            int index = mRecentPanelExpandedMode.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.RECENT_PANEL_EXPANDED_MODE,
                recentPanelExpandedMode, UserHandle.USER_CURRENT);
            mRecentPanelExpandedMode.setSummary(mRecentPanelExpandedMode.getEntries()[index]);
            return true;
        } else if (preference == mRecentPanelLeftyMode) {
            Settings.System.putInt(resolver,
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) newValue) ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mRecentPanelBgColor) {
            Settings.System.putInt(resolver, Settings.System.RECENT_PANEL_BG_COLOR, (Integer) newValue);
            preference.setSummary(((ColorPickerPreference) preference).getSummaryText() + ColorPickerPreference.convertToARGB((Integer) newValue));
            return true;
        } else if (preference == mRecentCardBgColor) {
            Settings.System.putInt(resolver, Settings.System.RECENT_CARD_BG_COLOR, (Integer) newValue);
            preference.setSummary(((ColorPickerPreference) preference).getSummaryText() + ColorPickerPreference.convertToARGB((Integer) newValue));
            return true;
        } else if (preference == mRecentCardTextColor) {
            Settings.System.putInt(resolver, Settings.System.RECENT_CARD_TEXT_COLOR, (Integer) newValue);
            preference.setSummary(((ColorPickerPreference) preference).getSummaryText() + ColorPickerPreference.convertToARGB((Integer) newValue));
            return true;
        }
        return false;
    }
}
