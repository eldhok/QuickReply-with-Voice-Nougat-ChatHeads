

package com.notifications.screen;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.notifications.screen.util.NotificationWear;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.notifications.screen.OverlayServiceCommon.notificationWear_list;

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
public class NotificationListenerAccessibilityService extends AccessibilityService
{

    //addition

    private ArrayList<NotificationCompat.Action> actions;

    //addition

    public static NotificationWear notificationWear;



    private final static String logTag = "NotificationListenerAccessibility";
    private boolean isInit = false;

    public static boolean doLoadSettings = true;
    private VoiceOver voiceOver = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent)
	{

	    //addition
        actions = new ArrayList<>();

        if (accessibilityEvent.getEventType() ==
			AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
		{
            try {
                if (Build.VERSION.SDK_INT >= 18) return;

                // Ignore toasts
                Notification notification = (Notification) accessibilityEvent.getParcelableData();
                if (notification == null) return;
                // Do not Ignore ongoing stuff if show non-cancelable feature is selected
                if ((notification.flags & Notification.FLAG_ONGOING_EVENT) != 0 && !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                        getBoolean("show_non_cancelable", false)) return;

                //addition

                NotificationWearReader(notification);



                DecisionMaker decisionMaker = new DecisionMaker();

                //addition

                decisionMaker.handleActionAdd(notificationWear.action_size,notification,
                        accessibilityEvent.getPackageName().toString(),
                        null,
                        0,
                        null,
                        getApplicationContext(),
                        "accessibility");

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    String report = e.getMessage();
                    if (report == null) report = "";
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    e.printStackTrace(printWriter);
                    report = report.concat( writer.toString() );
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (preferences != null && report.length() > 0) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("lastBug", report);
                        editor.apply();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    //addition
    public void NotificationWearReader(Notification notification) {


        Log.d("Lines", "reached inside NotificationWearReader");

        int flag = 0;

        notificationWear = new NotificationWear();

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(notification);

        if (actions.size() == 0) {
            actions.addAll(wearableExtender.getActions());
        } else
            for (int i = 0; i < actions.size(); i++) {

                if (actions.get(i).getRemoteInputs()[0] == null) {
                    flag = 0;
                    break;
                }

                if (!actions.get(i).getRemoteInputs()[0].getLabel().equals(wearableExtender.getActions().get(0).getRemoteInputs()[0].getLabel())) {
                    flag = 1;
                } else {
                    flag = 0;
                    break;
                }
            }

        if (flag == 1) {
            actions.addAll(wearableExtender.getActions());
        }

        Log.d("Lines", "NotificationListenerAccessibility actions: " + actions.size());

        notificationWear.action_size = actions.size();
       // notificationWear.action_size = 46;

        for (NotificationCompat.Action act : actions) {
            if (act != null && act.getRemoteInputs() != null) {
                notificationWear.remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
                notificationWear.pendingIntent.add( act.actionIntent);

                Log.d(TAG, " Label " + act.getRemoteInputs()[0].getLabel());
                Log.d(TAG, "Bundle " + act.getRemoteInputs()[0].getResultKey());
            }
        }


        notificationWear.bundle = notification.extras;

        //here add notificationWear to arraylist in OverlayServiceCommon
        if (notificationWear_list!=null)
        {
            notificationWear_list.add(notificationWear);
        }
        else
        {
            notificationWear_list = new ArrayList<NotificationWear>();
            notificationWear_list.add(notificationWear);

        }


        //add from here to the new function
/*
        android.support.v4.app.RemoteInput[] remoteInputs = new android.support.v4.app.RemoteInput[notificationWear.remoteInputs.size()];
        Intent localIntent = new Intent();
        Bundle localBundle = notificationWear.bundle;

        int i = 0;

        for (android.support.v4.app.RemoteInput remoteIn : notificationWear.remoteInputs) {
            remoteInputs[i] = remoteIn;
            localBundle.putCharSequence(remoteInputs[i].getResultKey(), "kjkl");
            i++;
        }

        android.support.v4.app.RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);



        try {
           // notificationWear.pendingIntent.send(getApplicationContext(), 0, localIntent);
            notificationWear.pendingIntent.get(0).send(getApplicationContext(), 0, localIntent);

        } catch (PendingIntent.CanceledException e) {
            //Log.e(TAG, "replyToLastNotification error: " + e.getLocalizedMessage());
        }


*/
    }

    //addition

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created");
       // actions = new ArrayList<>();
    }

    @Override
    protected void onServiceConnected()
	{
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putBoolean("running", true)
                .apply();
        if (isInit)
            return;

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        setServiceInfo(info);
        isInit = true;
        doLoadSettings();

        if (Build.VERSION.SDK_INT >= 18) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
            intent.setAction("STAY");
            intent.putExtra("packageName", getPackageName());
            intent.putExtra("title", getString(R.string.app_name));


            //addition start

            if (notificationWear.action_size!=null) {
                Log.d("Lines", "reply_action_count not nul " + notificationWear.action_size);

                intent.putExtra("reply_action_count", notificationWear.action_size);
               // intent.putExtra("reply_action_count", 3);
            }
            else {
                Log.d("Lines", "reply_action_count nul " + notificationWear.action_size);

                intent.putExtra("reply_action_count", 0);
                //intent.putExtra("reply_action_count", 4);

            }

            //addition end


            if (isNotificationListenerEnabled())
                intent.putExtra("text", getString(R.string.intro_warning_both_services));
            else {
                final String str = getString(R.string.accessibility_desc);
                intent.putExtra("text", str.substring(str.lastIndexOf("\n") + 1));
            }
            intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0, new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0));

            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dismiss_white);
                intent.putExtra("iconLarge", bitmap);
            }
            intent.putExtra("icon", R.drawable.ic_dismiss_white);
            startService(intent);
            stopSelf();
        }
    }

    private void doLoadSettings() {
        doLoadSettings = false;
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("music_on", false)) {
            if (voiceOver == null)
                voiceOver = new VoiceOver();
            voiceOver.enableVoiceOver(getApplicationContext());
        } else if (voiceOver != null)
            voiceOver.disableVoiceOver(getApplicationContext());
    }

    @Override
    public void onInterrupt()
	{
        isInit = false;
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putBoolean("running", false)
                .apply();
    }

    boolean isNotificationListenerEnabled() {
        Context context = getApplicationContext();
        try {
            //noinspection ConstantConditions
            ContentResolver contentResolver = context.getContentResolver();
            String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = context.getPackageName();

            return !(enabledNotificationListeners == null
                    || !enabledNotificationListeners.contains(packageName));
        } catch (NullPointerException e) {
            return false;
        }
    }

}
