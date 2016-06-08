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
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationMediaSettings extends SettingsPreferenceFragment {

    private static final String KEY_HEADS_UP_SETTINGS = "heads_up_settings";

    private PreferenceScreen mHeadsUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_media_settings);

        mHeadsUp = (PreferenceScreen) findPreference(KEY_HEADS_UP_SETTINGS);
    }

    private boolean getUserHeadsUpState() {
         return Settings.Global.getInt(getContentResolver(),
                Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
                Settings.Global.HEADS_UP_ON) != 0;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.PURE_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();

        mHeadsUp.setSummary(getUserHeadsUpState()
                ? R.string.summary_heads_up_enabled : R.string.summary_heads_up_disabled);
    }
}
