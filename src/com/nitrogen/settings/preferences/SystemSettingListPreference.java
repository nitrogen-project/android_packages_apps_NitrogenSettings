/*
 * Copyright (C) 2017-2018 AICP
 * Copyright (C) 2019 Syberia Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nitrogen.settings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.settings.R;

public class SystemSettingListPreference extends ListPreference {
    private static final String SETTINGSNS = "http://schemas.android.com/apk/res/com.android.settings";
    private String dependentValue = "";

    public SystemSettingListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
         if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SystemSettingListPreference);
            dependentValue = getAttributeStringValue(attrs, SETTINGSNS, "dependentValue", "");
            a.recycle();
        }
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    public SystemSettingListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
         if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SystemSettingListPreference);
            dependentValue = getAttributeStringValue(attrs, SETTINGSNS, "dependentValue", "");
            a.recycle();
        }
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    private String getAttributeStringValue(AttributeSet attrs, String namespace, String name,
            String defaultValue) {
        String value = attrs.getAttributeValue(namespace, name);
        if (value == null)
            value = defaultValue;

        return value;
    }

    public SystemSettingListPreference(Context context) {
        super(context);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    @Override
    public void setValue(String value) {
        String mOldValue = getValue();
        super.setValue(value);
        if (!value.equals(mOldValue)) {
            notifyDependencyChange(shouldDisableDependents());
       }
    }

    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisableDependents = super.shouldDisableDependents();
        String value = getValue();
        return shouldDisableDependents || value == null || !value.equals(dependentValue);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        // This is what default ListPreference implementation is doing without respecting
        // real default value:
        //setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
        // Instead, we better do
        setValue(restoreValue ? getPersistedString((String) defaultValue) : (String) defaultValue);
    }

}
