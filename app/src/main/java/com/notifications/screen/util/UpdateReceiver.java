
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
package com.notifications.screen.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.notifications.screen.BuildConfig;
import com.notifications.screen.R;
import com.notifications.screen.ui.SettingsActivity;

public class UpdateReceiver extends BroadcastReceiver {

    private static final int LATEST_VERSION = BuildConfig.VERSION_CODE;

    public UpdateReceiver() {
    }

    private static final String logTag = "UpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Mlog.v(logTag, "receive");
        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Mlog.v(logTag, "update");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

            final int prev_v = preferences.getInt("prev_v", LATEST_VERSION);
            if (prev_v < LATEST_VERSION) {
                SharedPreferences.Editor editor = preferences.edit();
                if (prev_v < 53) {
                    editor.putBoolean("dismiss_keyguard", true);
                }
                if (prev_v < 52) {
                    try {
                        context.getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        Mlog.i(logTag, "QQ is installed on device. Enabling ongoing notifications.");
                        editor.putBoolean("show_non_cancelable", true);
                        Notification.Builder builder = new Notification.Builder(context)
                                .setContentTitle("Settings changed")
                                .setContentText("QQ detected, enabled ongoing notifications")
                                .setSmallIcon(R.drawable.ic_stat_headsup)
                                .setVibrate(null)
                                .setAutoCancel(true)
                                .setContentIntent(PendingIntent.getActivity(
                                        context, 0, new Intent(context, SettingsActivity.class),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                ));

                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= 16)
                            notificationManager.notify(0, builder.build());
                        else
                            notificationManager.notify(0, builder.getNotification());
                    }
                }
                if (prev_v < 31) {
                    SharedPreferences oldPreferences = context.getSharedPreferences("heads-up", 0);
                    SharedPreferences.Editor oldEditor = oldPreferences.edit();
                    if (oldPreferences.contains("noshowlist")) {
                        Mlog.v(logTag, "noshowlist");
                        try {
                            oldPreferences.getString("noshowlist", null);
                        } catch (ClassCastException cce) {
                            Set<String> noshowlist = oldPreferences.getStringSet("noshowlist", new HashSet<String>());
                            try {
                                oldEditor.remove("noshowlist");
                                editor.putString("noshowlist", ObjectSerializer.serialize((Serializable) noshowlist));
                            } catch (IOException e) {
                                editor.putString("noshowlist", null);
                                e.printStackTrace();
                            }
                        }
                    }
                    if (oldPreferences.contains("blacklist")) {
                        Mlog.v(logTag, "blacklist");
                        try {
                            oldPreferences.getString("blacklist", null);
                        } catch (ClassCastException cce) {
                            Set<String> blacklist = oldPreferences.getStringSet("blacklist", new HashSet<String>());
                            try {
                                oldEditor.remove("blacklist");
                                editor.putString("blacklist", ObjectSerializer.serialize((Serializable) blacklist));
                            } catch (IOException e) {
                                editor.putString("blacklist", null);
                                e.printStackTrace();
                            }
                        }
                    }


                }
                editor.putInt("prev_v", LATEST_VERSION)
                      .apply();
            } else {
                Mlog.v(logTag, "already " + LATEST_VERSION);
            }
        }
    }
}
