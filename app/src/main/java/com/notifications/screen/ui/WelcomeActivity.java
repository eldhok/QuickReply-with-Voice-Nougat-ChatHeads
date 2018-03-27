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
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.notifications.screen.OverlayServiceCommon;
import com.notifications.screen.R;
import com.notifications.screen.util.Mlog;

public class WelcomeActivity extends Activity {
    public static final String ACCESSIBILITY_SERVICE_NAME = "com.notifications.screen/com.notifications.screen.NotificationListenerAccessibilityService";
    private static final String logTag = "Screenfly";
    private static boolean isRunning = false;
    private SharedPreferences preferences = null;
    static TextView tv;
    static View toastLayout;
    LayoutInflater inflater;
//ImageView btnPro;//btnGm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle(Html.fromHtml("<font color='#FFFFFF'><b> Screenfly</b></font>"));
        //setTitle(R.string.app_name);
       // setTitleColor(Color.parseColor("#2196F3"));
        setContentView(R.layout.activity_welcome);

        //ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));
        //mActionBar.setBackgroundDrawable(new ColorDrawable(0xff00DDED));
        //bar.setDisplayShowTitleEnabled(false);
        //bar.setDisplayShowTitleEnabled(true);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        inflater = LayoutInflater.from(this);


        toastLayout = inflater.inflate(R.layout.custom_toast, null);
        tv=(TextView)toastLayout.findViewById(R.id.custom_toast_message);
        //btnPro=(ImageView) findViewById(R.id.proBtn);
       // btnGm=(ImageView) findViewById(R.id.gameBtn);

        //btnPro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animatn));
/*

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        btnGm.startAnimation(rotateAnimation);
*/

        // btnGm.startAnimation(new RotateAnimation((float)0.0f, (float)(-10.0f * 360.0f), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        if (preferences.getBoolean("firstrun", true)) {
            startActivity(new Intent(this, SetupActivity.class));
        }

        if (Build.DISPLAY.toUpperCase().contains("MIUI") || Build.MANUFACTURER.toUpperCase().contains("XIAOMI")) {
            findViewById(R.id.miui_warning).setVisibility(View.VISIBLE);
            findViewById(R.id.mng_per).setVisibility(View.VISIBLE);
        }

        if (Mlog.isLogging) {
            doSendTest(null);
            tv.setText("Experimental demo version. Does not auto-update, and might not work at all. Please report bugs!");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();
           /*
            Toast.makeText(getApplicationContext(),
                    "Experimental demo version. Does not auto-update, and might not work at all. Please report bugs!",
                    Toast.LENGTH_LONG).show();
                    */
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity

            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivityForResult(intent, 0);

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        if (!(preferences.getBoolean("rated", false))) {


            AlertDialog.Builder alert = new AlertDialog.Builder(WelcomeActivity.this);
            alert.setTitle("If you think our Effort as Worth, please give us a Rating.");
            alert.setPositiveButton("RATE :-)", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    preferences.edit().putBoolean("rated", true).apply();

                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + getPackageName())));
                    }

                    dialog.cancel();
                    finish();
                }
            });

            alert.setNegativeButton("NO :-(", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            alert.create();
            alert.show();


        }
        else {
            finish();

        }

    }

    @Override
    protected void onResume () {
        super.onResume();
        isRunning = true;
        //TextView status = (TextView) findViewById(R.id.status);
        Button enableButton = (Button) findViewById(R.id.notification_open);
        if (
                ( Build.VERSION.SDK_INT >= 18 && isNotificationListenerEnabled(this) )
                || isAccessibilityEnabled(getApplicationContext())
        ) {
            //status.setVisibility(View.VISIBLE);
            enableButton.setText("Screenfly is ON");

            enableButton.setBackgroundResource(R.drawable.button_enable_on);
            checkEnabled();
            if (( Build.VERSION.SDK_INT >= 18 && isNotificationListenerEnabled(this) )
                    && isAccessibilityEnabled(getApplicationContext()) ) {
                final View bothEnabled = findViewById(R.id.bothEnabled);
                bothEnabled.setVisibility(View.VISIBLE);
                bothEnabled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoAccessibility(v.getContext());
                    }
                });
            } else {
                findViewById(R.id.bothEnabled).setVisibility(View.GONE);
            }
        } else {
            enableButton.setText("Screenfly is OFF");
            enableButton.setBackgroundResource(R.drawable.button_enable);
            //status.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        isRunning = false;
    }

    private void checkEnabled() {
        if (preferences.getBoolean("running", false)) {
            TextView status = (TextView) findViewById(R.id.status);
            status.setText(getString(R.string.intro_status_on_confirmed));
        } else if (isRunning) {
            //Mlog.d(logTag, "handler");
            handler.postDelayed(runnable, 5000);
        }
    }
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkEnabled();
        }
    };

    public void onClick (View v) {
        preferences.edit()
                .putBoolean("running", false)
                .apply();
        if (Build.VERSION.SDK_INT >= 18)
            gotoNotifyservice(this);
        else
            gotoAccessibility(this);
    }


    public void mng_per_clk (View v) {


        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void doOpenSettings (View v) {
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    public void doSendTest (View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
        intent.setAction("TEST");
        intent.putExtra("packageName", getPackageName());
        intent.putExtra("title", "Screenfly");
        intent.putExtra("text", getString(R.string.sample_notification));
        intent.putExtra("action", PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.notifications.screen"))
                , PendingIntent.FLAG_UPDATE_CURRENT));

        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            intent.putExtra("iconLarge", bitmap);
        }/**/
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
                        .setData(Uri.parse("http://Screenfly/donate/#Screenfly"))
                , PendingIntent.FLAG_UPDATE_CURRENT));

*/


        startService(intent);

        Mlog.v(logTag, "open");
    }

    public void getHelp (View v) {
        startActivity(new Intent(
                Intent.ACTION_VIEW, Uri.parse("https://github.com/Screenfly/Screenfly/blob/master/README.md#common-issues")
        ));
    }

    public void doReport (View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        try {
            final int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            intent.putExtra(Intent.EXTRA_TEXT,
                    preferences
                            .getString("lastBug", "Bug not saved") +
                            "--" + preferences.getBoolean("running", false) + " - " +
                            Build.VERSION.SDK_INT + " - " + versionCode + " - " + Build.PRODUCT
            );
            intent.putExtra(Intent.EXTRA_TITLE, "Bug in Screenfly " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            intent.putExtra(Intent.EXTRA_TEXT,
                    preferences
                            .getString("lastBug", "Bug not saved") +
                            "--" + preferences.getBoolean("running", false) + " - " +
                            Build.VERSION.SDK_INT + " - unknown version" + " - " + Build.PRODUCT
            );
            intent.putExtra(Intent.EXTRA_TITLE, "Bug in Screenfly");
        }
        startActivity(Intent.createChooser(intent, "Select export location"));

        preferences
                .edit()
                .remove("lastBug")
                .apply();
    }

    public void doOpenSite(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.notifications.screen"));
        startActivity(intent);
    }



/*
    public void game(View v)
    {




        Intent myIntent = new Intent(this, LLandActivity.class);
        this.startActivity(myIntent);


    }
    */

    public void pro(View v)
    {


        //link to download pro app


    }
    public static void gotoNotifyservice(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            try {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                context.startActivity(intent);
                //Toast.makeText(context, context.getText(R.string.notification_listener_not_found_detour), Toast.LENGTH_LONG).show();
                tv.setText(context.getText(R.string.notification_listener_not_found_detour));
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();
            } catch (ActivityNotFoundException anfe2) {
               // Toast.makeText(context, anfe2.getMessage(), Toast.LENGTH_LONG).show();
                tv.setText(anfe2.getMessage());
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();
            }
        }
    }

    public static void gotoAccessibility(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
            //Toast.makeText(context, context.getText(R.string.accessibility_toast), Toast.LENGTH_LONG).show();
            tv.setText(context.getText(R.string.accessibility_toast));
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();
        } catch (ActivityNotFoundException anfe) {
            try {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
                //Toast.makeText(context, context.getText(R.string.accessibility_not_found_detour), Toast.LENGTH_LONG).show();
                tv.setText( context.getText(R.string.accessibility_not_found_detour));
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();
            } catch (ActivityNotFoundException anfe2) {
                //Toast.makeText(context, anfe2.getMessage(), Toast.LENGTH_LONG).show();
                tv.setText(anfe2.getMessage());
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();
            }
        }
    }


    public static boolean isAccessibilityEnabled(Context context){
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Mlog.d(logTag, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Mlog.d(logTag, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Mlog.d(logTag, "Setting: " + settingValue);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Mlog.d(logTag, "Setting: " + accessibilityService);

                    if (accessibilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)){
                        Mlog.d(logTag, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    } else if (Build.VERSION.SDK_INT < 18 && "com.pushbullet.android/com.pushbullet.android.notifications.mirroring.CompatNotificationMirroringService".equals(accessibilityService)) {
                        // For easier translation in case of other troublesome services
                        tv.setText(String.format(context.getString(R.string.accessibility_service_blocked),
                                "PushBullet Notification Mirroring"));
                        Toast toast = new Toast(context);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(toastLayout);
                        toast.show();
                       /*
                        Toast.makeText(context, String.format(context.getString(R.string.accessibility_service_blocked),
                                "PushBullet Notification Mirroring"), Toast.LENGTH_LONG).show();
                                */
                    }
                }
            }

        }
        else{
            Mlog.d(logTag, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    public static boolean isNotificationListenerEnabled(Context context) {
        try {
            //noinspection ConstantConditions
            ContentResolver contentResolver = context.getContentResolver();
            String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = context.getPackageName();

            // check to see if the enabledNotificationListeners String contains our package name
            return !(enabledNotificationListeners == null
                    || !enabledNotificationListeners.contains(packageName));
        } catch (NullPointerException e) {
            return false;
        }
    }
}
