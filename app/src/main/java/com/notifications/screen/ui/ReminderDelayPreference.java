

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
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.notifications.screen.OverlayServiceCommon;
import com.notifications.screen.R;

import static android.widget.SeekBar.OnSeekBarChangeListener;

public class ReminderDelayPreference extends DialogPreference {
    private final static int mMin = OverlayServiceCommon.MIN_REMINDER_TIME;
    private final static int mMax = OverlayServiceCommon.MAX_REMINDER_TIME;
    private final int mDefault;

    private SeekBar mSeekBar;
    private static TextView mTextView;
    private static Resources resources;

    public ReminderDelayPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        resources = context.getResources();

        mDefault = 15000;
    }

    @Override
    protected View onCreateDialogView() {
        int value = getPersistedInt(mDefault);

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_dialog, null);

        mTextView = (TextView) view.findViewById(R.id.textView);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // Initialize state
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(value - mMin);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mSeekBar.getProgress() + mMin);
        }
    }

    private static final OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mTextView.setText(resources.getString(R.string.pref_reminder_delay_counter, String.valueOf( (i+mMin) / 60000 )));
        }
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    };

}