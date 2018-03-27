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
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;

import com.notifications.screen.OverlayServiceCommon;
import com.notifications.screen.R;

public class SetupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Html.fromHtml("<font color='#FFFFFF'><b> Screenfly</b></font>"));

        setContentView(R.layout.activity_setup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStep1();
    }

    private void checkStep1() {
        if ((Build.VERSION.SDK_INT >= 18 && WelcomeActivity.isNotificationListenerEnabled(getApplicationContext()))
                || WelcomeActivity.isAccessibilityEnabled(getApplicationContext())) {
            checkStep2();
            findViewById(R.id.notification_permission).setVisibility(View.GONE);
        }
    }

    public void toggleService(View view) {
        if (Build.VERSION.SDK_INT >= 18)
            WelcomeActivity.gotoNotifyservice(this);
        else
            WelcomeActivity.gotoAccessibility(this);
    }

    private void checkStep2() {
        boolean success;
        try {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            final View view = new View(getApplicationContext());
            windowManager.addView(view, new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            ));
            windowManager.removeView(view);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        if (!success) {
            findViewById(Build.VERSION.SDK_INT >= 23
                    ? R.id.popup_permission_marshmallow
                    : R.id.popup_permission_alternative)
                    .setVisibility(View.VISIBLE);
        } else {
            findViewById(Build.VERSION.SDK_INT >= 23
                    ? R.id.popup_permission_marshmallow
                    : R.id.popup_permission_alternative)
                    .setVisibility(View.GONE);
            checkStep3();
        }
    }

    public void openOverlaySettings(View view) {
        try {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:com.notifications.screen"))
            );
        } catch (ActivityNotFoundException e) {
            openAppSettings(view);
        }
    }

    public void openAppSettings(View view) {
        try {
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.notifications.screen"))
            );
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
        }
    }

    public void checkStep3() {

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putInt("overlay_display_time", 60000)
                .commit();

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
        intent.setAction("TEST");
        intent.putExtra("packageName", getPackageName());
        intent.putExtra("title", getString(R.string.sample_notification_title_setup));
        intent.putExtra("text", getString(R.string.sample_notification));
        intent.putExtra("action", PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.notifications.screen"))
                , PendingIntent.FLAG_UPDATE_CURRENT));

        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            intent.putExtra("iconLarge", bitmap);
        }
        intent.putExtra("icon", -1);
        //noinspection deprecation
        intent.putExtra("color", getResources().getColor(R.color.primaryDark));

       // intent.putExtra("actionCount", 2);
        intent.putExtra("actionCount", 1);
        intent.putExtra("action1title", getString(R.string.action_settings));
        intent.putExtra("action1icon", R.drawable.ic_action_settings);
        intent.putExtra("action1intent", PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), SettingsActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT));
      /*
        intent.putExtra("action1title", getString(R.string.action_pro));
        intent.putExtra("action1icon", R.drawable.ic_smile);
        intent.putExtra("action1intent", PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("http://Screenfly/donate/#heads-up"))
                , PendingIntent.FLAG_UPDATE_CURRENT));

*/



        startService(intent);

        findViewById(R.id.done_section).setVisibility(View.VISIBLE);
    }

    public void openHeadsupSettings(View view) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putInt("overlay_display_time", 8000)
                .putBoolean("firstrun", false)
                .commit();
        finish();
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    public void openHelp(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Screenfly/heads-up/blob/master/README.md#common-issues")
        ));
    }
}
