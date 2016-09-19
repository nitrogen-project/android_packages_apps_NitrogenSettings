package com.nitrogen.settings.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.PowerManager;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import com.android.settings.R;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.nitrogen.settings.preferences.SystemSettingSwitchPreference;


import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.SettingsPreferenceFragment;


public class NavigationBarSettings extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_ENABLED = "navigation_bar_enabled";

    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;

    SwitchPreference mNavigationBarEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nitrogen_settings_navigation);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        mNavigationBarEnabled =
                (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBarEnabled.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_ENABLED, 0) == 1));
        mNavigationBarEnabled.setOnPreferenceChangeListener(this);

        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;

        final boolean hasHWkeys = (hasHomeKey || hasMenuKey || hasBackKey);

        if (!hasHWkeys) {
            prefScreen.removePreference(mNavigationBarEnabled);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavigationBarEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.NITROGEN_SETTINGS;
    }
}
