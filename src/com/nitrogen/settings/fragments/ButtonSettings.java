package com.nitrogen.settings.fragments;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Handler;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.settings.SettingsPreferenceFragment;

public class ButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "NitrogenSettings";

    private static final String KEY_HOME_LONG_PRESS = "hardware_keys_home_long_press";
    private static final String KEY_HOME_DOUBLE_TAP = "hardware_keys_home_double_tap";
    private static final String KEY_MENU_PRESS = "hardware_keys_menu_press";
    private static final String KEY_MENU_LONG_PRESS = "hardware_keys_menu_long_press";
    private static final String KEY_BACKLIGHT_TIMEOUT = "backlight_timeout";
    private static final String KILL_APP_LONGPRESS_TIMEOUT = "kill_app_longpress_timeout";
    private static final String KEY_ENABLE_HW_KEYS = "enable_hw_keys";
    private static final String KEY_BUTTON_BRIGHTNESS = "button_brightness";

    private static final String CATEGORY_POWER = "power_key";
    private static final String CATEGORY_HOME = "home_key";
    private static final String CATEGORY_MENU = "menu_key";
    private static final String CATEGORY_BACK = "back_key";
    private static final String CATEGORY_ASSIST = "assist_key";
    private static final String CATEGORY_APPSWITCH = "app_switch_key";
    private static final String CATEGORY_CAMERA = "camera_key";
    private static final String CATEGORY_VOLUME = "volume_keys";

    // Available custom actions to perform on a key press.
    // Must match values for KEY_HOME_LONG_PRESS_ACTION in:
    // frameworks/base/core/java/android/provider/Settings.java
    private static final int ACTION_NOTHING = 0;
    private static final int ACTION_MENU = 1;
    private static final int ACTION_APP_SWITCH = 2;
    private static final int ACTION_SEARCH = 3;
    private static final int ACTION_VOICE_SEARCH = 4;
    private static final int ACTION_IN_APP_SEARCH = 5;
    private static final int ACTION_LAUNCH_CAMERA = 6;
    private static final int ACTION_SLEEP = 7;
    private static final int ACTION_LAST_APP = 8;

    // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;
    public static final int KEY_MASK_ASSIST = 0x08;
    public static final int KEY_MASK_APP_SWITCH = 0x10;
    public static final int KEY_MASK_CAMERA = 0x20;

    private ListPreference mHomeLongPressAction;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mMenuPressAction;
    private ListPreference mMenuLongPressAction;
    private ListPreference mBacklightTimeout;
    private ListPreference mKillAppLongpressTimeout;
    private SwitchPreference mEnableHwKeys;
    private SwitchPreference mButtonBrightness;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nitrogen_settings_button);

        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        final boolean hasPowerKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_POWER);
        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasAssistKey = (deviceKeys & KEY_MASK_ASSIST) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;

        boolean hasAnyBindableKey = false;
        final PreferenceCategory homeCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_HOME);
        final PreferenceCategory menuCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_MENU);
        final PreferenceCategory backCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_BACK);
        mBacklightTimeout =
                (ListPreference) findPreference(KEY_BACKLIGHT_TIMEOUT);
        mEnableHwKeys =
                (SwitchPreference) findPreference(KEY_ENABLE_HW_KEYS);

        mButtonBrightness =
                (SwitchPreference) findPreference(KEY_BUTTON_BRIGHTNESS);

        mHandler = new Handler();

        if (hasHomeKey) {
            int defaultLongPressAction = res.getInteger(
                    com.android.internal.R.integer.config_longPressOnHomeBehavior);
            if (defaultLongPressAction < ACTION_NOTHING ||
                    defaultLongPressAction > ACTION_LAST_APP) {
                defaultLongPressAction = ACTION_NOTHING;
            }

            int defaultDoubleTapAction = res.getInteger(
                    com.android.internal.R.integer.config_doubleTapOnHomeBehavior);
            if (defaultDoubleTapAction < ACTION_NOTHING ||
                    defaultDoubleTapAction > ACTION_LAST_APP) {
                defaultDoubleTapAction = ACTION_NOTHING;
            }

            int longPressAction = Settings.System.getInt(resolver,
                    Settings.System.KEY_HOME_LONG_PRESS_ACTION,
                    defaultLongPressAction);
            mHomeLongPressAction = initActionList(KEY_HOME_LONG_PRESS, longPressAction);

            int doubleTapAction = Settings.System.getInt(resolver,
                    Settings.System.KEY_HOME_DOUBLE_TAP_ACTION,
                    defaultDoubleTapAction);
            mHomeDoubleTapAction = initActionList(KEY_HOME_DOUBLE_TAP, doubleTapAction);

            hasAnyBindableKey = true;
        } else {
            prefScreen.removePreference(homeCategory);
        }

        if (hasMenuKey) {
            int pressAction = Settings.System.getInt(resolver,
                    Settings.System.KEY_MENU_ACTION, ACTION_MENU);
            mMenuPressAction = initActionList(KEY_MENU_PRESS, pressAction);

            int longPressAction = Settings.System.getInt(resolver,
                        Settings.System.KEY_MENU_LONG_PRESS_ACTION,
                        hasAssistKey ? ACTION_NOTHING : ACTION_SEARCH);
            mMenuLongPressAction = initActionList(KEY_MENU_LONG_PRESS, longPressAction);

            hasAnyBindableKey = true;
        } else {
            prefScreen.removePreference(menuCategory);
        }

        if (hasMenuKey || hasHomeKey) {
            if (mBacklightTimeout != null) {
        	mBacklightTimeout.setOnPreferenceChangeListener(this);
        	int BacklightTimeout = Settings.System.getInt(getContentResolver(),
                	Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 5000);
        	mBacklightTimeout.setValue(Integer.toString(BacklightTimeout));
        	mBacklightTimeout.setSummary(mBacklightTimeout.getEntry());
            }
            if (mEnableHwKeys != null) {
        	mEnableHwKeys.setChecked((Settings.System.getInt(getContentResolver(),
            	    Settings.System.ENABLE_HW_KEYS, 1) == 1));
        	mEnableHwKeys.setOnPreferenceChangeListener(this);
            }

            if (mButtonBrightness != null) {
        	mButtonBrightness.setChecked((Settings.System.getInt(getContentResolver(),
            	    Settings.System.BUTTON_BRIGHTNESS, 255) == 1));
        	mButtonBrightness.setOnPreferenceChangeListener(this);
            }
        } else {
            prefScreen.removePreference(mBacklightTimeout);
            prefScreen.removePreference(mEnableHwKeys);
            prefScreen.removePreference(mButtonBrightness);
        }

        if (hasBackKey) {
        // Back long press timeout
        mKillAppLongpressTimeout = (ListPreference) findPreference(KILL_APP_LONGPRESS_TIMEOUT);
        mKillAppLongpressTimeout.setOnPreferenceChangeListener(this);
        int KillAppLongpressTimeout = Settings.Secure.getInt(getContentResolver(),
        	Settings.Secure.KILL_APP_LONGPRESS_TIMEOUT, 1000);
        mKillAppLongpressTimeout.setValue(Integer.toString(KillAppLongpressTimeout));
        mKillAppLongpressTimeout.setSummary(mKillAppLongpressTimeout.getEntry());
        } else {
            prefScreen.removePreference(backCategory);
        }
    }

    private ListPreference initActionList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleActionListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);

        pref.setSummary(pref.getEntries()[index]);
        Settings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHomeLongPressAction) {
            handleActionListChange(mHomeLongPressAction, newValue,
                    Settings.System.KEY_HOME_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mHomeDoubleTapAction) {
            handleActionListChange(mHomeDoubleTapAction, newValue,
                    Settings.System.KEY_HOME_DOUBLE_TAP_ACTION);
            return true;
        } else if (preference == mMenuPressAction) {
            handleActionListChange(mMenuPressAction, newValue,
                    Settings.System.KEY_MENU_ACTION);
            return true;
        } else if (preference == mMenuLongPressAction) {
            handleActionListChange(mMenuLongPressAction, newValue,
                    Settings.System.KEY_MENU_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mBacklightTimeout) {
            String BacklightTimeout = (String) newValue;
            int BacklightTimeoutValue = Integer.parseInt(BacklightTimeout);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, BacklightTimeoutValue);
            int BacklightTimeoutIndex = mBacklightTimeout
                    .findIndexOfValue(BacklightTimeout);
            mBacklightTimeout
                    .setSummary(mBacklightTimeout.getEntries()[BacklightTimeoutIndex]);
            return true;
        } else if (preference == mKillAppLongpressTimeout) {
            String KillAppLongpressTimeout = (String) newValue;
            int KillAppLongpressTimeoutValue = Integer.parseInt(KillAppLongpressTimeout);
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.KILL_APP_LONGPRESS_TIMEOUT, KillAppLongpressTimeoutValue);
            int KillAppLongpressTimeoutIndex = mKillAppLongpressTimeout
                    .findIndexOfValue(KillAppLongpressTimeout);
            mKillAppLongpressTimeout
                    .setSummary(mKillAppLongpressTimeout.getEntries()[KillAppLongpressTimeoutIndex]);
            return true;
        } else if (preference == mEnableHwKeys) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ENABLE_HW_KEYS, value ? 1 : 0);
            return true;
        } else if (preference == mButtonBrightness) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, value ? 255 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.NITROGEN_SETTINGS;
    }
}
