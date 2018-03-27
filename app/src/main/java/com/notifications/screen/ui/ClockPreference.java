/*
 * Copyright (C) 2018 Screenfly Project
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

package com.notifications.screen.ui;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Date;

public class ClockPreference extends DialogPreference {

    private TimePicker mTimePicker;

    public ClockPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        int defaultValue = getKey().equals("night_mode_start") ? 1320 : 420;
        int value = getPersistedInt(defaultValue);

        mTimePicker = new TimePicker(getContext());

        mTimePicker.setCurrentHour((int) Math.floor(value / 60));
        mTimePicker.setCurrentMinute((int) Math.floor(value % 60));
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(getContext().getApplicationContext()));

        return mTimePicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hour = mTimePicker.getCurrentHour();
            int minute = mTimePicker.getCurrentMinute();
            persistInt( (hour * 60) + minute);
            // GregorianCalendar crashes with "bad class" on M, so we'll use the old Date.
            // The old thing probably works just fine for this stuff anyway.
            //noinspection deprecation
            setSummary(DateFormat.getTimeFormat(getContext().getApplicationContext())
                    .format(new Date(0, 0, 0, hour, minute)));
        }
    }

}