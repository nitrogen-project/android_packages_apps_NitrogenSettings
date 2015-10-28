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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.logging.MetricsLogger;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavBarSettings extends SettingsPreferenceFragment {

    private static final String CATEGORY_NAVBAR = "navigation_bar";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navbar_settings);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        final PreferenceCategory navbarCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_NAVBAR);

        // Enable or disable NavbarImeSwitcher based on boolean: config_show_cmIMESwitcher
        boolean showCmImeSwitcher = getResources().getBoolean(
                com.android.internal.R.bool.config_show_cmIMESwitcher);
        if (!showCmImeSwitcher) {
            Preference pref = findPreference(Settings.System.STATUS_BAR_IME_SWITCHER);
            if (pref != null) {
                navbarCategory.removePreference(pref);
            }
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.PURE_SETTINGS;
    }
}
