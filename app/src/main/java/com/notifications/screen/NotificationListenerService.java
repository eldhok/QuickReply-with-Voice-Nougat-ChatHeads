

package com.notifications.screen;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Process;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.notifications.screen.ui.WelcomeActivity;
import com.notifications.screen.util.Mlog;
import com.notifications.screen.util.NotificationWear;

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

@SuppressLint("NewApi")
public class NotificationListenerService extends android.service.notification.NotificationListenerService {
    private final static String logTag = "NotificationListener";
    public static final String ACTION_CUSTOM = "com.notifications.screen.NotificationListenerService.ACTION_CUSTOM";

    //hide_notification_start

    static NotificationManager notificationManager;

    //hide_notification_end
    //addition

    private static ArrayList<NotificationCompat.Action> actions;

    //addition
    static KeyguardManager myKM ;

    public static NotificationWear notificationWear;



    private static long sLastRelaunchTime = 0;
    private static int restarts;
    private static final String REQUEST_REBIND_ACTION = "rebind_action";

    static TextView tv;
    static View toastLayout;
    static LayoutInflater inflater;

    private VoiceOver voiceOver = null;
    private final IBinder mBinder = new LocalBinder();

    public NotificationListenerService() {
        Mlog.v(logTag, "Created listener");
    }

    public class LocalBinder extends Binder {
        NotificationListenerService getService() {
            return NotificationListenerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Mlog.d(logTag, "bind");
        if (intent.getAction().equals(ACTION_CUSTOM)) {
            super.onBind(intent);
            return mBinder;
        } else {
            if (Mlog.isLogging)
                Toast.makeText(getApplicationContext(), "Screenfly enabled", Toast.LENGTH_SHORT).show();
            doLoadSettings();
            return super.onBind(intent);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {

        Log.d("Lines", "reached onNotificationPosted");

        //addition



        //hide_notification_start







        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getBoolean("disable_sys_up", true)) {

            if (Build.VERSION.SDK_INT >= 21)
            {



                if (myKM==null)
                {

                    myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

                }

                if (myKM.inKeyguardRestrictedInputMode()) {
                    // it is locked


                } else {
                    //it is not locked


                    if (notificationManager==null)
                    {
                        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    }

                    if (statusBarNotification.getPackageName().equals(getPackageName())) {
                        notificationManager.cancel(12321);
                        return;

                    }


                    notificationManager.notify(12321, new Notification.Builder(this)
                            .setContentTitle("").setContentText("")
                            //.setSmallIcon(R.drawable.transparent)
                            .setPriority(Notification.PRIORITY_HIGH)
                            //.setFullScreenIntent(this.mBlankIntent, true)
                            .setAutoCancel(true)
                            .build());


                }


            }

        }



        //hide_notification_end







        actions = new ArrayList<>();



        try {
            /* if Show non-cancellable notifications is selected then need not check
            for Ongoing / Clearable as there will be ongoing notification by the background
             service which is trying to display.
            if Show non-cancellable notifications is not selected then existing logic
            prevails
             */
            if ((statusBarNotification.isOngoing() || !statusBarNotification.isClearable())
                && !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                    getBoolean("show_non_cancelable", false)) return;


            //addition

            NotificationWearReader(statusBarNotification.getNotification());





            if (NotificationListenerAccessibilityService.doLoadSettings) doLoadSettings();

            String statusBarNotificationKey = null;
            if (Build.VERSION.SDK_INT >= 20) statusBarNotificationKey = statusBarNotification.getKey();

            DecisionMaker decisionMaker = new DecisionMaker();


            //addition start

            decisionMaker.handleActionAdd(notificationWear.action_size,statusBarNotification.getNotification(),
                    statusBarNotification.getPackageName(),
                    statusBarNotification.getTag(),
                    statusBarNotification.getId(),
                    statusBarNotificationKey,
                    getApplicationContext(),
                    "listener");


            //addition end

        } catch (NullPointerException e) {
            e.printStackTrace();
            Mlog.e(logTag, "NPE");
        }






/*

        //new_update start

        notificationWear = new NotificationWear();

        Notification.WearableExtender wearableExtender = new Notification.WearableExtender(statusBarNotification.getNotification());
        List<Notification.Action> actions = wearableExtender.getActions();
        for(Notification.Action act : actions) {
            if(act != null && act.getRemoteInputs() != null) {

                //new
                notificationWear.remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
                //new
                // notificationWear.pendingIntent.add(act.actionIntent);

                //old
                // RemoteInput[] remoteInputs = act.getRemoteInputs();

                Log.e(" Run "," Inside 1 ");

            }
        }


        notificationWear.pendingIntent = statusBarNotification.getNotification().contentIntent;
        notificationWear.bundle = statusBarNotification.getNotification().extras;


        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.remoteInputs.size()];

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle localBundle = notificationWear.bundle;
        int i = 0;
        for(RemoteInput remoteIn : notificationWear.remoteInputs){
            getDetailsOfNotification(remoteIn);
            remoteInputs[i] = remoteIn;
            localBundle.putCharSequence(remoteInputs[i].getResultKey(), "jjjj");//This work, apart from Hangouts as probably they need additional parameter (notification_tag?)
            i++;
        }
        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
        try {
            notificationWear.pendingIntent.send(getApplicationContext(), 0, localIntent);
        } catch (PendingIntent.CanceledException e) {
            //Log.e(TAG, "replyToLastNotification error: " + e.getLocalizedMessage());
        }

        //new_update end


*/




    }


    //addition

    @Override
    public void onCreate() {
        super.onCreate();

        //hide_notification_start

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

        //hide_notification_end


        Log.d(TAG, "Created");
        //actions = new ArrayList<>();
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

        Log.d("Lines", "NotificationListenerService actions: " + actions.size());

        notificationWear.action_size = actions.size();
      //  notificationWear.action_size = 45;

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



    private void getDetailsOfNotification(RemoteInput remoteInput) {
        //Some more details of RemoteInput... no idea what for but maybe it will be useful at some point
        String resultKey = remoteInput.getResultKey();
        String label = remoteInput.getLabel().toString();
        Boolean canFreeForm = remoteInput.getAllowFreeFormInput();
        if(remoteInput.getChoices() != null && remoteInput.getChoices().length > 0) {
            String[] possibleChoices = new String[remoteInput.getChoices().length];
            for(int i = 0; i < remoteInput.getChoices().length; i++){
                possibleChoices[i] = remoteInput.getChoices()[i].toString();
            }
        }
    }



    private void doLoadSettings() {

        Log.d("Lines", "reached inside doLoadSettings");


        NotificationListenerAccessibilityService.doLoadSettings = false;
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("music_on", false)) {
            if (voiceOver == null)
            voiceOver = new VoiceOver();
            voiceOver.enableVoiceOver(getApplicationContext());
        } else if (voiceOver != null)
            voiceOver.disableVoiceOver(getApplicationContext());

        if (isAccessibilityEnabled()) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), OverlayServiceCommon.class);
            intent.setAction("TEST");
            intent.putExtra("packageName", getPackageName());
            intent.putExtra("title", getString(R.string.app_name));



                //addition start

                Log.d("Lines", "reached intent insert" + notificationWear.action_size);

                if (notificationWear.action_size!=null) {
                    Log.d("Lines", "reply_action_count not nul " + notificationWear.action_size);

                    intent.putExtra("reply_action_count", notificationWear.action_size);
                    //intent.putExtra("reply_action_count", 1);
                }
                else {
                    Log.d("Lines", "reply_action_count nul " + notificationWear.action_size);

                    intent.putExtra("reply_action_count", 0);
                    //intent.putExtra("reply_action_count", 2);

                }

                //addition end


            intent.putExtra("text", getString(R.string.intro_warning_both_services));
            intent.putExtra("action", PendingIntent.getActivity(getApplicationContext(), 0, new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0));

            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dismiss_white);
                intent.putExtra("iconLarge", bitmap);
            }
            intent.putExtra("icon", R.drawable.ic_dismiss_white);
            startService(intent);

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        DecisionMaker decisionMaker = new DecisionMaker();
        decisionMaker.handleActionRemove(
                statusBarNotification.getPackageName(),
                statusBarNotification.getTag(),
                statusBarNotification.getId(),
                getApplicationContext()
        );
    }

    @SuppressWarnings("deprecation")
    public void doRemove (String pkg, String tag, int id) {
        Mlog.d(logTag, pkg + tag + id);
        try {
            cancelNotification(pkg, tag, id);
        } catch (SecurityException e) {
            try {
                String report = e.getMessage();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                report = report.concat( writer.toString() );
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("lastBug", report);
                editor.apply();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static boolean hasTooManyRestarts(Context context) {
        int count = restarts;
        if (count > 5) {
            restarts = 0;


            //Toast.makeText(context, R.string.too_many_restarts_warning, Toast.LENGTH_LONG).show();


            inflater = LayoutInflater.from(context);
            toastLayout = inflater.inflate(R.layout.custom_toast, null);
            tv=(TextView)toastLayout.findViewById(R.id.custom_toast_message);
            tv.setText(context.getText(R.string.too_many_restarts_warning));
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastLayout);
            toast.show();
            return true;
        }
        return false;
    }
    public static boolean restartNotificationListenerServiceIfNeeded(Context context) {
        if (!hasTooManyRestarts(context) && System.currentTimeMillis() - sLastRelaunchTime > 60000) {
            sLastRelaunchTime = System.currentTimeMillis();
            boolean running = isNotificationListenerServiceRunning(context);
            if (!running) {
                toggleNotificationListenerService(context);
                restarts++;
                return isNotificationListenerServiceRunning(context);
            }
        }
        return false;
    }






    public static boolean isNotificationListenerServiceRunning(Context context) {
        ComponentName collectorComponent = new ComponentName(context, NotificationListenerService.class);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean serviceRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null ) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent) && service.pid == Process.myPid())
                serviceRunning = true;
        }
        return serviceRunning;
    }

    public static void toggleNotificationListenerService(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Intent intent = new Intent(context, NotificationListenerService.class);
            intent.setAction(REQUEST_REBIND_ACTION);
            context.startService(intent);
            return;
        }
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
/*
    private static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, com.notifications.screen.NotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, com.notifications.screen.NotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
    */
    public void doRemove (String key) {
        Mlog.d(logTag, key);
        try {
            cancelNotification(key);
        } catch (SecurityException e) {
            try {
                String report = e.getMessage();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                report = report.concat( writer.toString() );
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("lastBug", report);
                editor.apply();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public boolean isNotificationValid(String pkg, String tag, int id) {
        final StatusBarNotification[] activeNotifications = getActiveNotifications();
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            final String statusBarNotificationTag = statusBarNotification.getTag();
            final String statusBarNotificationPackageName = statusBarNotification.getPackageName();
            final int statusBarNotificationId = statusBarNotification.getId();
            if (statusBarNotificationPackageName.equals(pkg)
                    && statusBarNotificationId == id) {
                if (tag == null && statusBarNotificationTag == null)
                    return true;
                if (tag != null && statusBarNotificationTag != null)
                    if (statusBarNotificationTag.equals(tag))
                        return true;
            }
        }
        return false;
    }

    /*
    TODO: Doesn't work, see VoiceOver.java
    public void pushMusicNotification (String pkg) {
        StatusBarNotification[] statusBarNotifications = getActiveNotifications();
        if (statusBarNotifications.length > 0) {
            for (StatusBarNotification statusBarNotification : statusBarNotifications) {
                final String statusBarNotificationPackageName = statusBarNotification.getPackageName();
                Mlog.v(pkg, statusBarNotificationPackageName);
                //if (pkg.contains(statusBarNotificationPackageName) && !statusBarNotificationPackageName.equals("android")) {
                if (statusBarNotificationPackageName.equals("com.google.android.music")) {
                    DecisionMaker decisionMaker = new DecisionMaker();
                    decisionMaker.handleActionAdd(statusBarNotification.getNotification(),
                            statusBarNotificationPackageName,
                            statusBarNotification.getTag(),
                            statusBarNotification.getId(),
                            getApplicationContext(),
                            "music");
                }
            }
        }
    }*/

    boolean isAccessibilityEnabled(){
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Mlog.w(logTag, "Error finding accessibility setting: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Mlog.d(logTag, "Setting: " + accessibilityService);
                    if (accessibilityService.equalsIgnoreCase(WelcomeActivity.ACCESSIBILITY_SERVICE_NAME)){
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
