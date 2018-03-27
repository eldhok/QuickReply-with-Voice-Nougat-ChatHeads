package com.notifications.screen.ui;
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
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import com.notifications.screen.ColorDialog;
import com.notifications.screen.NotificationListenerAccessibilityService;
import com.notifications.screen.OverlayService;
import com.notifications.screen.OverlayServiceCommon;
import com.notifications.screen.R;
import com.notifications.screen.util.Mlog;

import static com.notifications.screen.UnlockActivity.logTag;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    Toast toast;
    LayoutInflater inflater;
    static TextView tv;
    static View toastLayout;

    static int red,green,blue;
    static double lum;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);

        toastLayout = inflater.inflate(R.layout.custom_toast, null);
        tv=(TextView)toastLayout.findViewById(R.id.custom_toast_message);

        setupSimplePreferencesScreen();
    }

    @Override
    protected void onStop () {
        super.onStop();
        stopService(new Intent(getApplicationContext(), OverlayService.class));
        stopService(new Intent(getApplicationContext(), OverlayServiceCommon.class));
        NotificationListenerAccessibilityService.doLoadSettings = true;
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        setTitle(Html.fromHtml("<font color='#FFFFFF'><b> Settings</b></font>"));

        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_behaviour);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_behaviour);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_style);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_style);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_lockscreen);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_lockscreen);
/*
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_other);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_other);
*/
        bindPreferenceSummaryToValue(findPreference("night_mode_start"));
        bindPreferenceSummaryToValue(findPreference("night_mode_end"));

        final ListPreference prefListThemes = (ListPreference) findPreference("overlay_style");
        prefListThemes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(final Preference preference, Object newValue) {
                //Toast.makeText(getApplicationContext(), "toast", Toast.LENGTH_SHORT).show();
                // Restart Activity to apply Theme

                Mlog.d(logTag, " Preference " + "changed");


/*
                if (newValue.toString().equals("8"))
                {

                    preference.getEditor().putBoolean("pro_color_change",false).apply();

                   // Toast.makeText(SettingsActivity.this, "Color changed to Default. ", Toast.LENGTH_SHORT).show();
                    tv.setText("Color changed to Default.");
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();


                }
                else


                 */

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
                intent.setAction("TEST");
                intent.putExtra("packageName", getPackageName());
                intent.putExtra("title", "Screenfly");
                intent.putExtra("text", getString(R.string.sample_notification));
                intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0,
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

                //intent.putExtra("actionCount", 2);
                intent.putExtra("actionCount", 1);
                intent.putExtra("action1title", getString(R.string.action_settings));
                intent.putExtra("action1icon", R.drawable.ic_action_settings);
                intent.putExtra("action1intent", PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), SettingsActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT));


                startService(intent);





/*
{



                    int initialColor = preference.getSharedPreferences().getInt("pro_color", Color.WHITE);

                    ColorDialog colorPickerDialog = new ColorDialog(SettingsActivity.this, initialColor, new ColorDialog.OnColorSelectedListener() {

                        @Override
                        public void onColorSelected(int color) {


                            preference.getEditor().putInt("pro_color", color).apply();

                             red = Color.red(color);
                             green = Color.green(color);
                             blue = Color.blue(color);
                             lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));


                           // preference.getEditor().putInt("pro_color_text", lum > 186 ? 0xFF000000 : 0xFFFFFFFF).apply();
                            preference.getEditor().putInt("pro_color_text", lum > 186 ? Color.BLACK : Color.WHITE).apply();


                            preference.getEditor().putBoolean("pro_color_change", true).apply();
                            preference.getEditor().putBoolean("pro_color_change_b", false).apply();

                            //Toast.makeText(SettingsActivity.this, "Color " + color, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
                            intent.setAction("TEST");
                            intent.putExtra("packageName", getPackageName());
                            intent.putExtra("title", "Screenfly");
                            intent.putExtra("text", getString(R.string.sample_notification));
                            intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0,
                                    new Intent(Intent.ACTION_VIEW)
                                            .setData(Uri.parse("http://Screenfly"))
                                    , PendingIntent.FLAG_UPDATE_CURRENT));

                            if (Build.VERSION.SDK_INT >= 11) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                intent.putExtra("iconLarge", bitmap);
                            }
                            intent.putExtra("icon", -1);
                            //noinspection deprecation
                            intent.putExtra("color", getResources().getColor(R.color.primaryDark));

                            //intent.putExtra("actionCount", 2);
                            intent.putExtra("actionCount", 1);
                            intent.putExtra("action1title", getString(R.string.action_settings));
                            intent.putExtra("action1icon", R.drawable.ic_action_settings);
                            intent.putExtra("action1intent", PendingIntent.getActivity(getApplicationContext(), 0,
                                    new Intent(getApplicationContext(), SettingsActivity.class),
                                    PendingIntent.FLAG_CANCEL_CURRENT));


                            startService(intent);





                        }
                    });

                    colorPickerDialog.show();







}
*/

                return true;
            }
        });



        //single_view
        final CheckBoxPreference Single_View = (CheckBoxPreference) findPreference("single_view");
        // prefListPosLk.setEnabled(false);




        Single_View.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {



/*


                if (Boolean.parseBoolean(newValue.toString()))
                {
                    Mlog.d(logTag, " Boolean 1 " +newValue);


                    preference.getEditor().putInt("overlay_display_time",3000).apply();

                    // Toast.makeText(SettingsActivity.this, "Color changed to Default. ", Toast.LENGTH_SHORT).show();
                    tv.setText("Time changed to 3000 Seconds");
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();


                }
                else
                {
                    Mlog.d(logTag, " Boolean 2 " +newValue);

                    preference.getEditor().putInt("overlay_display_time",7000).apply();

                    tv.setText("Time changed to 7000 Seconds");
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();








                }

                */



                return true;
            }
        });

        final CheckBoxPreference color_change = (CheckBoxPreference) findPreference("pro_color_change_b");
        // prefListPosLk.setEnabled(false);




        color_change.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {


                Mlog.d(logTag, " Boolean " +newValue);




                if (Boolean.parseBoolean(newValue.toString()))
                {
                    Mlog.d(logTag, " Boolean 1 " +newValue);


                    preference.getEditor().putBoolean("pro_color_change",false).apply();

                    // Toast.makeText(SettingsActivity.this, "Color changed to Default. ", Toast.LENGTH_SHORT).show();
                    tv.setText("Color changed to Default.");
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();



                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
                    intent.setAction("TEST");
                    intent.putExtra("packageName", getPackageName());
                    intent.putExtra("title", "Screenfly");
                    intent.putExtra("text", getString(R.string.sample_notification));
                    intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0,
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

                    //intent.putExtra("actionCount", 2);
                    intent.putExtra("actionCount", 1);
                    intent.putExtra("action1title", getString(R.string.action_settings));
                    intent.putExtra("action1icon", R.drawable.ic_action_settings);
                    intent.putExtra("action1intent", PendingIntent.getActivity(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), SettingsActivity.class),
                            PendingIntent.FLAG_CANCEL_CURRENT));


                    startService(intent);


                }
                else
                {
                    Mlog.d(logTag, " Boolean 2 " +newValue);

                    preference.getEditor().putBoolean("pro_color_change",true).apply();

                    // Toast.makeText(SettingsActivity.this, "Color changed to Default. ", Toast.LENGTH_SHORT).show();
                    tv.setText("Color changed to Custom.");
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();


                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
                    intent.setAction("TEST");
                    intent.putExtra("packageName", getPackageName());
                    intent.putExtra("title", "Screenfly");
                    intent.putExtra("text", getString(R.string.sample_notification));
                    intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0,
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

                    //intent.putExtra("actionCount", 2);
                    intent.putExtra("actionCount", 1);
                    intent.putExtra("action1title", getString(R.string.action_settings));
                    intent.putExtra("action1icon", R.drawable.ic_action_settings);
                    intent.putExtra("action1intent", PendingIntent.getActivity(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), SettingsActivity.class),
                            PendingIntent.FLAG_CANCEL_CURRENT));


                    startService(intent);



                }



                return true;
            }
        });


/*

        Preference button = (Preference)getPreferenceManager().findPreference("color_selector");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {


                    return true;
                }
            });
        }

*/


        final CheckBoxPreference single_view = (CheckBoxPreference) findPreference("single_view");


        single_view.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {



                if (Boolean.parseBoolean(newValue.toString()))
                {
                    preference.getEditor().putInt("overlay_display_time", OverlayService.MAX_DISPLAY_TIME).apply();

                }
                else
                {
                    preference.getEditor().putInt("overlay_display_time", 12000).apply();

                }




                return true;
            }
        });

        final Preference color_selector = (Preference) findPreference("color_selector");
        // prefListPosLk.setEnabled(false);




        color_selector.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {


               // Mlog.d(logTag, " Boolean " +newValue);




                int initialColor = preference.getSharedPreferences().getInt("pro_color", Color.WHITE);

                ColorDialog colorPickerDialog = new ColorDialog(SettingsActivity.this, initialColor, new ColorDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {


                        preference.getEditor().putInt("pro_color", color).apply();

                        red = Color.red(color);
                        green = Color.green(color);
                        blue = Color.blue(color);
                        lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));


                        // preference.getEditor().putInt("pro_color_text", lum > 186 ? 0xFF000000 : 0xFFFFFFFF).apply();
                        preference.getEditor().putInt("pro_color_text", lum > 186 ? Color.BLACK : Color.WHITE).apply();


                        preference.getEditor().putBoolean("pro_color_change", true).apply();
                        preference.getEditor().putBoolean("pro_color_change_b", false).apply();

                        //Toast.makeText(SettingsActivity.this, "Color " + color, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
                        intent.setAction("TEST");
                        intent.putExtra("packageName", getPackageName());
                        intent.putExtra("title", "Screenfly");
                        intent.putExtra("text", getString(R.string.sample_notification));
                        intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0,
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

                        //intent.putExtra("actionCount", 2);
                        intent.putExtra("actionCount", 1);
                        intent.putExtra("action1title", getString(R.string.action_settings));
                        intent.putExtra("action1icon", R.drawable.ic_action_settings);
                        intent.putExtra("action1intent", PendingIntent.getActivity(getApplicationContext(), 0,
                                new Intent(getApplicationContext(), SettingsActivity.class),
                                PendingIntent.FLAG_CANCEL_CURRENT));


                        startService(intent);





                    }
                });

                colorPickerDialog.show();







                return true;
            }
        });

    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

           // Mlog.d(logTag, " Preference " + preference.getKey());
           // Mlog.d(logTag, " stringValue " + stringValue);


            if (preference instanceof ClockPreference) {
                final int intValue = (int) value;
                if (intValue < 0) return false; // Not set yet

                int hour = (int) Math.floor(intValue / 60);
                int minute = (int) Math.floor(intValue % 60);

                preference.setSummary(DateFormat.getTimeFormat(preference.getContext()).format(new Date(0, 0, 0, hour, minute)));

            } else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), -1));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference("night_mode_start"));
            bindPreferenceSummaryToValue(findPreference("night_mode_end"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BehaviourPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_behaviour);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class StylePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_style);




        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LockscreenPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_lockscreen);
        }
    }
/*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OtherPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);
        }
    }
*/
    @Override
    protected boolean isValidFragment (String fragmentName) {
        return true;
    }
}
