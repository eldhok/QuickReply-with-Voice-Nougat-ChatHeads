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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.notifications.screen.R;
import com.notifications.screen.util.Mlog;

public class RequestBroadcastDialog extends Activity {

    public static final String ACTION_REQUEST = "com.notifications.screen.REQUEST_BROADCAST";
    public static final String ACTION_CEHCK = "com.notifications.screen.CHECK_BROADCAST";
    public static final String EXTRA_APP_NAME = "app_name";
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction().equals(ACTION_CEHCK)) {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("broadcast_notifications", false)
                    && WelcomeActivity.isNotificationListenerEnabled(getApplicationContext())) {
                sendResult(RESULT_OK);
                return;
            }
        }

        appName = getIntent().getStringExtra(EXTRA_APP_NAME);

        if (appName == null) {
            Mlog.w("RequestBroadcastDialog", "Invalid or no app name");
            sendResult(RESULT_CANCELED);
            return;
        }

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("broadcast_notifications", false)) {
            sendResult(RESULT_OK);
            return;
        }

        if (!WelcomeActivity.isNotificationListenerEnabled(getApplicationContext()) && !WelcomeActivity.isAccessibilityEnabled(getApplicationContext())) {
            showErrorDialog();
            return;
        }

        showAccessDialog(appName);
    }

    private void showAccessDialog(String appName) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_broadcast_request))
                .setMessage(String.format(getString(R.string.content_broadcast_request), appName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .edit()
                                .putBoolean("broadcast_notifications", true)
                                .commit();
                        sendResult(RESULT_OK);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(RESULT_CANCELED);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        sendResult(RESULT_CANCELED);
                    }
                })
                .show();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_broadcast_request))
                .setMessage("Please give Screenfly access to your notifications before you continue")
                .setPositiveButton("Open Screenfly", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(getApplicationContext(), WelcomeActivity.class), 18);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(RESULT_CANCELED);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        sendResult(RESULT_CANCELED);
                    }
                })
                .show();
    }

    private void sendResult(int resultCode) {
        Intent result = new Intent(ACTION_REQUEST).putExtra("result", resultCode);
        setResult(resultCode, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 18) {
            if (!WelcomeActivity.isNotificationListenerEnabled(getApplicationContext())) {
                showErrorDialog();
                return;
            }

            showAccessDialog(appName);
        }
    }
}