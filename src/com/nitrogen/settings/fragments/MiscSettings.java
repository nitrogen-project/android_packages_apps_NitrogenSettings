package com.nitrogen.settings.fragments;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.DevelopmentSettings;
import com.android.settings.R;

import java.util.Arrays;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;

public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_LOCK_CLOCK = "lock_clock";
    private static final String KEY_LOCK_CLOCK_PACKAGE_NAME = "com.cyanogenmod.lockclock";
    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
    private static final String SCROLLINGCACHE_DEFAULT = "1";

    private ListPreference mScrollingCachePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nitrogen_settings_misc);

        // mLockClock
        if (!DevelopmentSettings.isPackageInstalled(getActivity(), KEY_LOCK_CLOCK_PACKAGE_NAME)) {
            getPreferenceScreen().removePreference(findPreference(KEY_LOCK_CLOCK));
        }

        mScrollingCachePref = (ListPreference) findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setSummary(mScrollingCachePref.getEntry());
        mScrollingCachePref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mScrollingCachePref) {
            if (objValue != null) {
            String ScrollingCache = (String) objValue;
            SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, ScrollingCache);
            int ScrollingCacheIndex = mScrollingCachePref
                    .findIndexOfValue(ScrollingCache);
            mScrollingCachePref
                    .setSummary(mScrollingCachePref.getEntries()[ScrollingCacheIndex]);
            }
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.NITROGEN_SETTINGS;
    }
}
