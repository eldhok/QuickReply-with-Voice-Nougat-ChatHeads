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

package com.notifications.screen;


import android.content.ActivityNotFoundException;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.notifications.screen.admin.AdminReceiver;
import com.notifications.screen.theme.HoloDark;
import com.notifications.screen.theme.HoloLight;
import com.notifications.screen.theme.L5Black;
import com.notifications.screen.theme.L5Dark;
import com.notifications.screen.theme.L5Light;
import com.notifications.screen.theme.Random;
import com.notifications.screen.theme.ThemeClass;
import com.notifications.screen.theme.Custom_1;
import com.notifications.screen.theme.Ubuntu;
import com.notifications.screen.theme.bubble_only;
import com.notifications.screen.util.Mlog;
import com.notifications.screen.util.NotificationWear;
import com.notifications.screen.util.ObjectSerializer;
import com.notifications.screen.util.SwipeDismissTouchListener;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.notifications.screen.R.id.index;

public class OverlayServiceCommon extends Service implements SensorEventListener {
    final static String logTag = "Overlay";
    public static final int MAX_DISPLAY_TIME = 60000;
    public static final int MAX_REMINDER_TIME = 1200000;
    public static final int MIN_REMINDER_TIME = 6000;
    private static final int MAX_LINES = 12;
    private static final int SENSOR_DELAY_MILLIS = 10000;
    private static final int MIN_LINES = 2;
    public static final int FLAG_FLOATING_WINDOW = 0x00002000;
    static TextView tv;
    static View toastLayout;

    public static int i;

//addition
    public static ArrayList<NotificationWear> notificationWear_list;


    private static final ArrayList<String> LOCKSCREEN_APPS = new ArrayList<>(Arrays.asList(new String[]{
            "com.achep.acdisplay",
            "com.silverfinger.lockscreen",
            "com.slidelock",
            "com.coverscreen.cover",
            "com.jiubang.goscreenlock",
            "com.greatbytes.activenotifications",
            "com.nemis.memlock",
            "com.teslacoilsw.widgetlocker",
            "com.jedga.peek",
            "com.jedga.peek.free",
            "com.jedga.peek.pro",
            "com.hi.locker",
            "com.vlocker.locker",
            "com.microsoft.next",
            "com.cmcm.locker"
    }));

    static LinearLayout fillView;

    static TextView notification_no;

    static Float opacity;
    int val_thm;
    static int finalHeight, finalWidth;
    LinearLayout.LayoutParams viewPram,viewPram_scroll;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private LinearLayout layout,mainlayout;
    private ScrollView scrollView;
   public static View main_view;
    //private boolean isViewAdded = false;
    private ThemeClass themeClass = new ThemeClass();

    public static SharedPreferences preferences = null;

    private PendingIntent pendingIntent;
    private int displayTime = 15000;
    private int position = 1;
    private String currentPackage = "";
    private boolean isCompact = false;
    private boolean isActionButtons = false;
    private Date notificationTime = null;

    private static final int SENSOR_SENSITIVITY = 4;

    //private List<View> chatHeads;
    private List<PendingIntent> pendingIntentList,pendingIntentListb;
    private List<Integer> actionCount;
    private List<SpannableString> mSpannableString_l;
    private  ArrayList<Handler> handler;
    //  private List<Integer> indexH;
    //private List<Integer> actionStart;

    private SensorManager sensorManager = null;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private DevicePolicyManager policyManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wLock;
    private boolean isProximityClose = false;
    private boolean isLocked;
    String packageName = "";
    String notTime = "";
    String tag = "";
    String key = "";
    private String prevPackageName = "0";


    //speech_to_text

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    protected volatile boolean mIsCountDownOn;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;



public static EditText msg_rply_mic;
public static ImageView msg_snd_mic;


    int id = 0;
public static Drawable mDrawable,cus;

    public static TypedArray colors_md;
    public static int index_md,color_md;
    public static Context context;

    LayoutInflater inflater;
    // ViewStub stub;
    @Override
    public void onCreate () {
        super.onCreate();
        try {
            Mlog.d(logTag, "Create");
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            displayTime = preferences.getInt("overlay_display_time", 12000);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            inflater = LayoutInflater.from(this);
            getCurrentPackage();
            isLocked = isLocked();

            //chatHeads = new ArrayList<View>();
            actionCount = new ArrayList<Integer>();


            //addition

            if (notificationWear_list==null) {
                notificationWear_list = new ArrayList<NotificationWear>();
            }

            mSpannableString_l = new ArrayList<SpannableString>();

            handler = new ArrayList<Handler>();

            context = getApplicationContext();

            colors_md = context.getResources().obtainTypedArray(R.array.loading_colors);
            index_md = (int) (Math.random() * colors_md .length());
            color_md = colors_md.getColor(index_md, Color.BLACK);
            // indexH = new ArrayList<Integer>();
            //actionStart = new ArrayList<Integer>();
            pendingIntentList = new ArrayList<PendingIntent>();
            pendingIntentListb = new ArrayList<PendingIntent>();

            mainlayout = new LinearLayout(this);
            fillView = new LinearLayout(this);

            scrollView = new ScrollView(this);


            scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            layout = new LinearLayout(this);

            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            fillView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,windowManager.getDefaultDisplay().getWidth()/2));
            //fillView.setBackgroundColor(Color.BLUE);
            fillView.setId(R.id.fill_View);

            fillView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    stopSelf();


                }
            });
            // ViewGroup toastGrp = (ViewGroup)findViewById(R.id.custom_toast_layout);
            toastLayout = inflater.inflate(R.layout.custom_toast, null);
            tv=(TextView)toastLayout.findViewById(R.id.custom_toast_message);

            //layout.setId(0);

            //viewPram = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);





            //viewPram = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //layout.setLayoutParams(viewPram);


            layout.setOrientation(LinearLayout.VERTICAL);
            mainlayout.setOrientation(LinearLayout.VERTICAL);



            scrollView.addView(layout);

            mainlayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));




            //mainlayout.addView(scrollView);


            //addition edit changed flag

            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                    //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );


            if (isLocked)
                position = Integer.valueOf(preferences.getString("overlay_vertical_position_locked", "-10"));
            if (!isLocked || position == -10)
                position = Integer.valueOf(preferences.getString("overlay_vertical_position", "1"));

          /*
            switch (position) {
                case 2:
                    layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                    layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case 1:
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case 0:
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    mainlayout.addView(fillView);
                    break;
                case -1:
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
            }
            */

            if (position==0)
            {
                mainlayout.addView(fillView);
            }

            mainlayout.addView(scrollView);



            isCompact = preferences.getBoolean("compact_mode", true);


            //speech_to_text

            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    this.getPackageName());


//to start Listening
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                // turn off beep sound
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

*/



        } catch (VerifyError ve) {
            Mlog.w(logTag, ve.getMessage());
        }
    }


    //speech_to_text


    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000){
        @Override
        public void onTick(long millisUntilFinished){}
        @Override
        public void onFinish(){
            mIsCountDownOn = false;
            mSpeechRecognizer.cancel();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                // turn off beep sound
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    };



    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            // speech input will be processed, so there is no need for count down anymore
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer){
            //			Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech(){
            Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error)
        {
            if (mIsCountDownOn){
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                // turn off beep sound
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            Log.d(TAG, "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onEvent(int eventType, Bundle params){
            Log.d(TAG, "onEvent");
        }

        @Override
        public void onPartialResults(Bundle partialResults){
            //			Log.d(TAG, "onPartialResults");
            //			ArrayList strlist = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //			for (int i = 0; i < strlist.size();i++ ) {
            //				Log.d(TAG, "partialResult =" + strlist.get(i));
            //			}
        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results){
            Log.d(TAG, "onResults"); //$NON-NLS-1$



            ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < strlist.size();i++ ) {


/*
                Toast.makeText(getApplicationContext(),
                        strlist.get(i),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "result=" + strlist.get(i));
*/

                //msg_rply_mic.append(strlist.get(i));

                msg_rply_mic.setText(strlist.get(0));

            }



            switch (val_thm) {

                case 0: // L

                    msg_snd_mic.setImageResource(R.drawable.black_send);


                    break;
                case 1: // L Dark
                    msg_snd_mic.setImageResource(R.drawable.send);


                    break;
                case 5: // L Black

                    msg_snd_mic.setImageResource(R.drawable.send);

                    break;
                case 2: // Holo Light
                    msg_snd_mic.setImageResource(R.drawable.black_send);


                    break;
                case 3: // Holo

                    msg_snd_mic.setImageResource(R.drawable.send);


                    break;
                case 4: // Random

                    msg_snd_mic.setImageResource(R.drawable.send);


                    break;
                case 6: // ubuntu

                    msg_snd_mic.setImageResource(R.drawable.send);



                    break;
                case 7: // Custom_1

                    msg_snd_mic.setImageResource(R.drawable.send);



                    break;

            }




            /*
            //Keep listening
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                // turn off beep sound
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            */
        }

        @Override
        public void onRmsChanged(float rmsdB){
            //			Log.d(TAG, "onRmsChanged");
        }

    }



//addition
public void Reply(String message,NotificationWear notificationWear)
{




    android.support.v4.app.RemoteInput[] remoteInputs = new android.support.v4.app.RemoteInput[notificationWear.remoteInputs.size()];
    Intent localIntent = new Intent();
    Bundle localBundle = notificationWear.bundle;

    int i = 0;

    for (android.support.v4.app.RemoteInput remoteIn : notificationWear.remoteInputs) {
        remoteInputs[i] = remoteIn;
        localBundle.putCharSequence(remoteInputs[i].getResultKey(), message);
        i++;
    }

    android.support.v4.app.RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);

    //Log.d("Lines", "Lines labels length"+Integer.toString(remoteInputs.length));

/*
    for (int x = 0; x < remoteInputs.length; x++) {


        Log.d("Lines", "Lines labels"+ remoteInputs[x].getLabel().toString());


    }
    */

    try {



        //old
        notificationWear.pendingIntent.get(0).send(getApplicationContext(), 0, localIntent);



/*


        //notificationWear.pendingIntent.get((remoteInputs.length)-1).send(getApplicationContext(), 0, localIntent);



        if (remoteInputs[0].getLabel().toString().matches("Reply to ")) {
            notificationWear.pendingIntent.get(0).send(context, 0, localIntent);
        } else {


            for (int x = 0; x < remoteInputs.length; x++) {
                if (remoteInputs[x].getLabel().toString().matches("Reply to ")) {
                    notificationWear.pendingIntent.get(x).send(context, 0, localIntent);
                    break;
                }
                Log.d("Lines", remoteInputs[x].getLabel().toString());
            }

        }

*/


    } catch (PendingIntent.CanceledException e) {
        //Log.e(TAG, "replyToLastNotification error: " + e.getLocalizedMessage());
    }

}


    //from here wrap textview



    /**
     * This method builds the text layout
     */
    private void makeSpan() {



    }

    /**
     *
     */
    class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {

        private int margin;
        private int lines;

        MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        /**
         * Apply the margin
         *
         * @param first
         * @return
         */
        @Override
        public int getLeadingMargin(boolean first) {

            // Mlog.d(logTag, " margin: " + margin+" "+first);

            if (first) {

                return margin;
            } else {
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {}


        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    };





    // to here wrap textview








    private void getCurrentPackage() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityManager.RunningAppProcessInfo processInfo = getTopAppLollipop(am);
                if (processInfo != null)
                    currentPackage = processInfo.processName;
            }
            else
                currentPackage = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        } catch (SecurityException | IndexOutOfBoundsException | NullPointerException e) {
            reportError(e, "Please allow Screenfly to get running tasks", getApplicationContext());
        }
    }

    @TargetApi(21)
    private ActivityManager.RunningAppProcessInfo getTopAppLollipop(ActivityManager am) {
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        if (tasks != null && tasks.size() > 0) {
            Field processStateField = null;
            try {
                processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (NoSuchFieldException ignored) {}
            final int PROCESS_STATE_TOP = 2;

            for (ActivityManager.RunningAppProcessInfo task : tasks) {
                if (task.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && task.importanceReasonCode == 0) {
                    if (processStateField == null) return task;
                    try {
                        if (processStateField.getInt(task) == PROCESS_STATE_TOP)
                            return task;
                    } catch (IllegalAccessException e) {
                        Mlog.d(logTag, "IAE: " + e.getMessage());
                        return task;
                    }
                }
            }
        }
        return null;
    }

    private void displayWindow () {
        if (preferences.getBoolean("lock_screen_on", false)) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            if (preferences.getBoolean("only_on_lock_screen", false)) {
                if (stopIfNotLocked()) return;

            }
            initPowerManager();

            Mlog.d(logTag, " proximity " + preferences.getBoolean("use_proximity", false));



            if (preferences.getBoolean("turn_screen_on", false) && !powerManager.isScreenOn()) {
                if (preferences.getBoolean("use_proximity", false)) {
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    if (sensor == null) {
                        try {
                            sensor = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
                        } catch (Exception sensorListException) {
                            if (Mlog.isLogging) sensorListException.printStackTrace();
                        }
                    }
                    if (sensor != null) {
                        addViewToWindowManager();
                        sensorEventListener = this;
                        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                        //here


                        if (!actionCount.isEmpty())
                        {
                            handler.add(actionCount.size()-1,new Handler());

                            handler.get(actionCount.size()-1).postDelayed(sensorChecker, SENSOR_DELAY_MILLIS);
                        }




                    } else {
                        reportError(null, getString(R.string.no_proximity_sensor_error), getApplicationContext());
                        preferences.edit().putBoolean("use_proximity", false).apply();
                        createWLock();
                        screenOn();
                        addViewToWindowManager();
                    }
                } else {
                    createWLock();
                    screenOn();
                    addViewToWindowManager();
                }
            } else {
                addViewToWindowManager();
            }
        } else {
            if (isLocked())
                stopSelf();
            else
                addViewToWindowManager();
        }
    }

    private boolean stopIfNotLocked() {
        if (isLocked) return false;
        Mlog.d(logTag, "not locked");
        stopSelf();
        return true;
    }

    private boolean isLocked() {
        if (preferences.getBoolean("off_as_locked", false)) {
            initPowerManager();
            if (!powerManager.isScreenOn()) {
                isLocked = true;
                return isLocked;
            }
        }

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        final boolean isKeyguardLocked;
        if (Build.VERSION.SDK_INT >= 16)
            isKeyguardLocked = keyguardManager.isKeyguardLocked();
        else isKeyguardLocked = keyguardManager.inKeyguardRestrictedInputMode();

        Mlog.v(logTag, isKeyguardLocked + " " + LOCKSCREEN_APPS.contains(currentPackage));
        isLocked = isKeyguardLocked || (currentPackage != null && LOCKSCREEN_APPS.contains(currentPackage));
        return isLocked;
    }




    private void addViewToWindowManager() {


try {




        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // StatusBarNotification[] not = ;
        // Mlog.d(logTag, " StatusBarNotification "+manager.getActiveNotifications());


/*

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("remaining", preferences.getInt("remaining",0)+1);
        editor.apply();

*/



/*

        TextView textView_index = (TextView) main_view.findViewById(index);


        Mlog.v(logTag + " Start SIZE ", chatHeads.size());


        if (chatHeads.isEmpty()) {
            textView_index.setText("0");
            Mlog.d(logTag, " Value of INDEXXX  " + textView_index.getText());

        }
        else {
            textView_index.setText(chatHeads.size());
            Mlog.d(logTag, " Value of INDEXXX  " + textView_index.getText());

        }

*/


        //  if (!isViewAdded) {
        Mlog.d(logTag, " AddViewtoWindow Child Count Before "+layout.getChildCount());







        Mlog.d(logTag, " AddViewtoWindow Child Count After "+layout.getChildCount());


        //layout.removeView(main_view);
        // layout.addView(main_view);

        TextView textView_index = (TextView) main_view.getRootView().findViewById(index);
        TextView textView_pack = (TextView) main_view.getRootView().findViewById(R.id.pack);
        TextView textView_text = (TextView) main_view.findViewById(R.id.notification_subtitle);
        TextView textView_head = (TextView) main_view.findViewById(R.id.notification_title);

        if (layout.getChildCount()==0)
        {







            main_view.setId(0);
            textView_index.setText(Integer.toString(0));

            //main_view.setId(0);

            // viewPram.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            //main_view.setLayoutParams(viewPram);
            //layout.updateViewLayout(main_view,viewPram);


            /*

            for (int c=0;c<20;c++)
            {
                Mlog.d(logTag, " XXXXXX "+c);



            }
            */


            if (preferences.getBoolean("single_view", false) && !isLocked()) {
                main_view.setVisibility(View.VISIBLE);

                main_view.findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                notification_no = (TextView) main_view.findViewById(R.id.notification_no);
                notification_no.setText("0");

            }
            // indexH.add(0);

            layout.addView(main_view,0);





            main_view.setAlpha(opacity);

            Mlog.d(logTag, " Opacity "+opacity);

            // layout.addView(main_view);


            windowManager.addView(mainlayout, layoutParams);
            layout.requestFocus();

        }else
        {



            //single_view
            if (preferences.getBoolean("single_view", false) && !isLocked())
            {


                main_view.setVisibility(View.GONE);

                layout.findViewById(0).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                notification_no = (TextView)layout.findViewById(0).findViewById(R.id.notification_no);
                notification_no.setText(Integer.toString(layout.getChildCount()));

            }


            //new edit for removing view when new onw comes



            String pack = textView_pack.getText().toString();
            String text = textView_text.getText().toString();
            String head = textView_head.getText().toString();

            TextView textView_pack_temp,textView_text_temp,textView_head_temp;
            View childView;

            View chumma;
            TextView textView_index_temp;

            for (int inx=0;inx<layout.getChildCount();inx++)
            {


                childView = (View)layout.findViewById(inx);

                textView_pack_temp = (TextView) childView.findViewById(R.id.pack);


                if (pack.equals(textView_pack_temp.getText().toString()))
                {
                    Mlog.d(logTag, " Same PKG ");


                    if (pack.equals("com.whatsapp"))
                    {


                        layout.removeView(layout.findViewById(inx));

                        //  indexH.remove(inx);
                        if (handler.get(inx) != null) {
                            handler.get(inx).removeCallbacks(closeTimer);

                        }

                        handler.remove(inx);

                        for (int i = inx + 1; i < layout.getChildCount() + 1; i++) {


                            // Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I " + i);


                            chumma = layout.getRootView().findViewById(i);


                            //   indexH.set(i,i-1);

                            textView_index_temp = (TextView) chumma.findViewById(index);
                            textView_index_temp.setText(Integer.toString(i - 1));
                            chumma.setId(i - 1);


                        }


                        //chatHeads.remove(inx);
                        if (actionCount==null || actionCount.isEmpty())
                            return;
                        actionCount.remove(inx);

                        //addition

                        notificationWear_list.remove(inx);

                        mSpannableString_l.remove(inx);

                        pendingIntentList.remove(inx);


                        if (preferences.getBoolean("single_view", false) && !isLocked())
                        {

                            if (layout.getChildCount()==0)
                            {
                                main_view.findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                notification_no = (TextView)main_view.findViewById(R.id.notification_no);

                                notification_no.setText(Integer.toString(layout.getChildCount()));


                                main_view.setVisibility(View.VISIBLE);

                                handler.get(inx).postDelayed(closeTimer, displayTime);
                            }
                            else
                            {
                                layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);

                                notification_no.setText(Integer.toString(layout.getChildCount()));


                                layout.findViewById(inx).setVisibility(View.VISIBLE);

                                handler.get(inx).postDelayed(closeTimer, displayTime);
                            }
                        }

                    }
                    else
                    {




                        textView_text_temp = (TextView) childView.findViewById(R.id.notification_subtitle);
                        textView_head_temp = (TextView) childView.findViewById(R.id.notification_title);

                        if (textView_head_temp.getText().toString().equals(head)) {


                            // if (text.equals(textView_text_temp.getText().toString()))
                            {

                                layout.removeView(layout.findViewById(inx));

                                //  indexH.remove(inx);
                                if (handler.get(inx) != null) {
                                    handler.get(inx).removeCallbacks(closeTimer);

                                }

                                handler.remove(inx);

                                for (int i = inx + 1; i < layout.getChildCount() + 1; i++) {


                                    // Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I " + i);


                                    chumma = layout.getRootView().findViewById(i);


                                    //   indexH.set(i,i-1);

                                    textView_index_temp = (TextView) chumma.findViewById(index);
                                    textView_index_temp.setText(Integer.toString(i - 1));
                                    chumma.setId(i - 1);


                                }


                                //chatHeads.remove(inx);
                                if (actionCount==null || actionCount.isEmpty())
                                    return;

                                mSpannableString_l.remove(inx);
                                actionCount.remove(inx);

                                //addition

                                notificationWear_list.remove(inx);

                                pendingIntentList.remove(inx);



                                if (preferences.getBoolean("single_view", false) && !isLocked())
                                {
/*
                                    if (layout.getChildCount()==0)
                                    {
                                        main_view.setVisibility(View.VISIBLE);
                                        main_view.findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)main_view.findViewById(R.id.notification_no);
                                        notification_no.setText("0");
                                    }

                                    layout.findViewById(inx).setVisibility(View.VISIBLE);

                                    handler.get(inx).postDelayed(closeTimer, displayTime);
                                    */

                                    if (layout.getChildCount()==0)
                                    {
                                        main_view.findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)main_view.findViewById(R.id.notification_no);

                                        notification_no.setText(Integer.toString(layout.getChildCount()));


                                        main_view.setVisibility(View.VISIBLE);

                                        handler.get(inx).postDelayed(closeTimer, displayTime);
                                    }
                                    else
                                    {
                                        layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);

                                        notification_no.setText(Integer.toString(layout.getChildCount()));


                                        layout.findViewById(inx).setVisibility(View.VISIBLE);

                                        handler.get(inx).postDelayed(closeTimer, displayTime);
                                    }

                                }
                            }
                        } else if (text.contains(textView_text_temp.getText().toString())) {


                            if (text.equals(textView_text_temp.getText().toString())) {


                                layout.removeView(layout.findViewById(inx));
                                if (handler.get(inx) != null) {
                                    handler.get(inx).removeCallbacks(closeTimer);

                                }

                                handler.remove(inx);

                                //  indexH.remove(inx);


                                for (int i = inx + 1; i < layout.getChildCount() + 1; i++) {


                                    Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I " + i);


                                    chumma = layout.getRootView().findViewById(i);


                                    //  indexH.set(i,i-1);


                                    textView_index_temp = (TextView) chumma.findViewById(index);
                                    textView_index_temp.setText(Integer.toString(i - 1));
                                    chumma.setId(i - 1);


                                }

                                if (actionCount==null || actionCount.isEmpty())
                                    return;
                                //chatHeads.remove(inx);
                                mSpannableString_l.remove(inx);
                                actionCount.remove(inx);

                                //addition

                                notificationWear_list.remove(inx);

                                pendingIntentList.remove(inx);

                                if (preferences.getBoolean("single_view", false) && !isLocked())
                                {

/*
                                    layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                    notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);

                                    notification_no.setText(Integer.toString(layout.getChildCount()));


                                    layout.findViewById(inx).setVisibility(View.VISIBLE);

                                    handler.get(inx).postDelayed(closeTimer, displayTime);

                                    */

                                    if (layout.getChildCount()==0)
                                    {
                                        main_view.findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)main_view.findViewById(R.id.notification_no);

                                        notification_no.setText(Integer.toString(layout.getChildCount()));


                                        main_view.setVisibility(View.VISIBLE);

                                        handler.get(inx).postDelayed(closeTimer, displayTime);
                                    }
                                    else
                                    {
                                        layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);

                                        notification_no.setText(Integer.toString(layout.getChildCount()));


                                        layout.findViewById(inx).setVisibility(View.VISIBLE);

                                        handler.get(inx).postDelayed(closeTimer, displayTime);
                                    }

                                }

                            } else {


                                //text = text.replace(textView_text_temp.getText().toString(), "");
/*

                            //textView_head.setText(head.replace(" "+textView_head_temp.getText().toString()+" ",""));

                            String[] w_ori = text.toString().split("\n");
                            String[] w_tmp = textView_text_temp.getText().toString().split("\n");

                            //String word = text;

                            for (int i = 0; i < w_ori.length; i++) {
                                for (int j = 0; j < w_tmp.length; j++) {

                                    if (w_ori[i].equals(w_tmp[j])) {

                                        text = text.replace(w_ori[i], "");


                                    }


                                }
                            }



                            */
                                // textView_text.setText(text);


                            }


                        }


                    }

                }


            }


            //upto here only



            //  indexH.add(layout.getChildCount());

            main_view.setId(layout.getChildCount());

            // main_view.setId(chatHeads.size());

            //viewPram.addRule(RelativeLayout.BELOW,chatHeads.size()-1);
            //main_view.setLayoutParams(viewPram);
            // layout.updateViewLayout(main_view,viewPram);

            textView_index.setText(Integer.toString(layout.getChildCount()));







            //new edit for removing view when new onw comes




            //View main = (View)view.getRootView().findViewById(inx);










/*
            for (int i=chatHeads.size();i>0;i--)
            {

                layout.removeView(chatHeads.get(i-1));
                layout.addView(chatHeads.get(i-1),i);

            }

*/







            layout.addView(main_view,0);


            main_view.setAlpha(opacity);


            //layout.addView(main_view,chatHeads.size());


            //layout.addView(main_view);



            // windowManager.updateViewLayout(layout,layoutParams);

        }

        // layout.removeView(main_view);



        // chatHeads.add(main_view);

        // Mlog.d(logTag ," ChatHeads SIZE "+chatHeads.size());



        // }




        //   isViewAdded = true;

    }
                                catch (NullPointerException n)
    {
        //removed
       stopSelf();

    }
                                 catch (IndexOutOfBoundsException ignored) {
                                     //removed

                                     stopSelf();

                                 } catch (RuntimeException rte) {
        reportError(rte, "ThemeActionIcon", getApplicationContext());
    //removed

//    stopSelf();

}

    }

    private final Runnable sensorChecker = new Runnable() {
        @Override
        public void run() {
            Mlog.d(logTag + "SensorChecker", String.valueOf(isProximityClose));

            if (sensorManager != null) {
                sensorManager.unregisterListener(sensorEventListener, sensor);
                sensorManager = null;
            }
        }
    };






    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);



        Mlog.d(logTag + " mSpannableString_l Start", mSpannableString_l.size());





        try {



            opacity = (float) preferences.getInt("opacity", 98);
            if (opacity == null)
                opacity = 98f;

            opacity=opacity/100;

            isLocked = isLocked();

            if (isLocked)
                position = Integer.valueOf(preferences.getString("overlay_vertical_position_locked", "-10"));
            if (!isLocked || position == -10)
                position = Integer.valueOf(preferences.getString("overlay_vertical_position", "1"));
            switch (position) {
                case 2:
                    layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                    layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case 1:
                    if (mainlayout.findViewById(R.id.fill_View)!=null)
                    {
                        mainlayout.removeView(mainlayout.findViewById(R.id.fill_View));

                    }
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case 0:
                    if (mainlayout.findViewById(R.id.fill_View)==null)
                    {
                        mainlayout.addView(fillView,0);

                    }
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

                    break;
                case -1:
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
            }




            main_view = inflater.inflate(R.layout.activity_read, null);


            //main_view.setVisibility(View.GONE);
            ViewStub stub = (ViewStub) main_view.findViewById(R.id.viewStub);

            if (!getResources().getBoolean(R.bool.is_tablet)) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                final ViewGroup.LayoutParams stubLayoutParams = stub.getLayoutParams();
                if (metrics.widthPixels <= metrics.heightPixels)
                    stubLayoutParams.width = metrics.widthPixels;
                else
                    //noinspection SuspiciousNameCombination
                    stubLayoutParams.width = metrics.heightPixels;
                stub.setLayoutParams(stubLayoutParams);
            }

            final int theme = Integer.parseInt(preferences.getString("overlay_style", "1"));

            val_thm = theme;

            switch (theme) {
                case 0: // L
                    themeClass = new L5Light(stub);
                    break;
                case 1: // L Dark
                    themeClass = new L5Dark(stub);
                    break;
                case 5: // L Black
                    themeClass = new L5Black(stub);
                    break;
                case 2: // Holo Light
                    themeClass = new HoloLight(stub);
                    break;
                case 3: // Holo
                    themeClass = new HoloDark(stub);
                    break;
                case 4: // Random
                    themeClass = new Random(stub);
                    break;
                case 6: // Custom_1
                    themeClass = new Ubuntu(stub);
                    break;
                case 7: // Custom_1
                    themeClass = new Custom_1(stub);
                    break;

            }

            stub.inflate();
            themeClass.init(main_view);

        }
        catch (VerifyError ve) {
            Mlog.w(logTag, ve.getMessage());
        }



        switch (val_thm) {

            case 0: // L

                if (preferences.getBoolean("pro_color_change", false))
                {




                    mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_white);


                    //mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                   // DrawableCompat.setTintList(mDrawable, preferences.getInt("pro_color",Color.WHITE));
                    mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);
                    (main_view.findViewById(R.id.linearLayout)).setBackground(mDrawable);

                }

                break;
            case 1: // L Dark
                if (preferences.getBoolean("pro_color_change", false))
                {
                    mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_dark);


                   // mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);

                    (main_view.findViewById(R.id.linearLayout)).setBackground(mDrawable);
                }

                break;
            case 5: // L Black
                if (preferences.getBoolean("pro_color_change", false))
                {
                    mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_black);


                   // mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);

                    (main_view.findViewById(R.id.linearLayout)).setBackground(mDrawable);
                }


                break;
            case 2: // Holo Light
                if (preferences.getBoolean("pro_color_change", false))
                {
                    //mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_ubuntu);


                   // mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                   // mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);

                    (main_view.findViewById(R.id.contentContainer)).setBackgroundColor(preferences.getInt("pro_color",Color.WHITE));
                }


                break;
            case 3: // Holo

                if (preferences.getBoolean("pro_color_change", false))
                {
                   // mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_ubuntu);


                    //mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                   // mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);

                    (main_view.findViewById(R.id.contentContainer)).setBackgroundColor(preferences.getInt("pro_color",Color.WHITE));
                }


                break;
            case 4: // Random



                break;
            case 6: // ubuntu

                if (preferences.getBoolean("pro_color_change", false))
                {
                    mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.card_ubuntu);


                   // mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    mDrawable.setColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.SRC_ATOP);

                    (main_view.findViewById(R.id.linearLayout)).setBackground(mDrawable);
                }

                break;
            case 7: // Custom_1



                if (preferences.getBoolean("pro_color_change", false)) {




                    mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.user_name_bg_cut);
                    cus = getApplicationContext().getResources().getDrawable(R.drawable.shape_chat_user_name_bg);

                    //mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    //cus.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));


                    ((TextView)main_view.findViewById(R.id.notification_title)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));


                    mDrawable.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);
                    cus.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);


                    (main_view.findViewById(R.id.bgMyImage)).setBackground(mDrawable);
                    (main_view.findViewById(R.id.notification_title)).setBackground(cus);


                    ((EditText)main_view.findViewById(R.id.msg_rply)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
                    ((EditText)main_view.findViewById(R.id.msg_rply)).setHintTextColor(preferences.getInt("pro_color_text",Color.WHITE));


                    ((GradientDrawable)((LinearLayout) main_view.findViewById(R.id.msg_rply_RL)).getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));


                }
                break;

          //  case 8: //bubble_only

                /*
                //new_edit_bubble

                if (preferences.getBoolean("pro_color_change", false)) {




                    //mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.rounded_corner1);
                    //cus = getApplicationContext().getResources().getDrawable(R.drawable.shape_chat_user_name_bg);

                    //mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    //cus.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));


                    //((TextView)main_view.findViewById(R.id.notification_title)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));


                    //mDrawable.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);
                   // cus.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);


                   // (main_view.findViewById(R.id.imageView3)).setBackground(mDrawable);
                   // (main_view.findViewById(R.id.notification_title)).setBackground(cus);


                    ((EditText)main_view.findViewById(R.id.msg_rply)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
                    ((EditText)main_view.findViewById(R.id.msg_rply)).setHintTextColor(preferences.getInt("pro_color_text",Color.WHITE));


                    ((GradientDrawable)((RelativeLayout) main_view.findViewById(R.id.imageView3_b)).getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));
                    ((GradientDrawable)((LinearLayout) main_view.findViewById(R.id.msg_rply_RL)).getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));


                }
*/

/*
                if (preferences.getBoolean("pro_color_change", false)) {




                    //mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.user_name_bg_cut);
                    //cus = getApplicationContext().getResources().getDrawable(R.drawable.rounded_corner1);

                    //mDrawable.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));
                    //cus.setColorFilter(new PorterDuffColorFilter(preferences.getInt("pro_color",Color.WHITE), PorterDuff.Mode.MULTIPLY));


                    ((TextView)main_view.findViewById(R.id.notification_subtitle)).setTextColor(preferences.getInt("pro_color_text",Color.BLACK));


                    //mDrawable.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);
                    //cus.setColorFilter(preferences.getInt("pro_color", Color.WHITE), PorterDuff.Mode.SRC_ATOP);


                    //(main_view.findViewById(R.id.bgMyImage)).setBackground(mDrawable);
                    //(main_view.findViewById(R.id.notification_subtitle)).setBackground(cus);


                    ((GradientDrawable)((RelativeLayout) main_view.findViewById(R.id.imageView3)).getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));



                    ((EditText)main_view.findViewById(R.id.msg_rply)).setTextColor(preferences.getInt("pro_color_text",Color.BLACK));
                    ((EditText)main_view.findViewById(R.id.msg_rply)).setHintTextColor(preferences.getInt("pro_color_text",Color.BLACK));


                    ((GradientDrawable)((LinearLayout) main_view.findViewById(R.id.msg_rply_RL)).getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));


                }

*/



               // break;

        }





        if (preferences.getBoolean("pro_color_change", false) && val_thm!=7 && val_thm!=4) {

            ((TextView)main_view.findViewById(R.id.notification_title)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
            ((TextView)main_view.findViewById(R.id.notification_subtitle)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
            ((TextView)main_view.findViewById(R.id.timeView)).setTextColor(preferences.getInt("pro_color_text",Color.WHITE));



        }



            Mlog.d(logTag, "Start "+val_thm);
        try {
            /*

            if (intent.getAction().equals("REMOVE")) {

                try {
                    //checking previous view present , then remove it by looping here

                    if (packageName.equals(intent.getStringExtra("packageName"))
                            && tag.equals(intent.getStringExtra("tag"))
                            && id == intent.getIntExtra("id", 0)) {
                        Mlog.d(logTag, "remove");

                       // doFinish(0);
                        // remove previous one from
                    }


                } catch (Exception e) {
                    //reportError(e, "remove failed", getApplicationContext());
                    stopSelf();
                }
                if (packageName.equals("")) stopSelf();
                return START_NOT_STICKY;
            }


            */
/*

            if (!chatHeads.isEmpty())
            {
                TextView titleTextView = (TextView) main_view.findViewById(R.id.notification_subtitle);

                Mlog.d(logTag ," Entered ");



                titleTextView.setText("Nothing");
            }

            */

            //  displayWindow();



            PackageManager pm = getPackageManager();
            Resources appRes = null;
            final Bundle extras = intent.getExtras();

            //here handler.removeCallbacks(closeTimer);

/*
            //new_edit_bubble
            final String text;

            if (val_thm==8)
            {
                 text = extras.getString("text")+" : "+extras.getString("title");
            }
            else
            {
                 text = extras.getString("text");

            }
            */
            String title = extras.getString("title");
           final String text = extras.getString("text");



            //addition start

            Integer reply_action_count = extras.getInt("reply_action_count");

            Log.d("Lines", "OverlayServiceCommn value of reply_action_count " + reply_action_count);


            //addition end


            packageName = extras.getString("packageName");



            key = extras.getString("key");
            tag = extras.getString("tag");
            id = extras.getInt("id", 0);





            try {

                if (text!=null) {
                    if (!text.equals("")) {


                        if (title.equals("")) {
                            try {
                                title = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));
                            } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                                reportError(e, "EmptyTitle", getApplicationContext());
                            }
                        }
                    } else {
                        return START_NOT_STICKY;

                    }
                }
                else
                {
                    return START_NOT_STICKY;

                }
            } catch (NullPointerException e) {
                reportError(e, "EmptyTitle", getApplicationContext());
            }




/*
            if (notificationTime != null) {
                  themeClass.hideTime(main_view);
                  notificationTime.setTime(System.currentTimeMillis());

            } else {
                notificationTime = new Date();
            }
*/

            notificationTime = new Date();

            notificationTime.setTime(System.currentTimeMillis());


            themeClass.showTime(main_view,notificationTime);

            final float sizeMultiplier = (float) (preferences.getInt("font_size", 100) / 100.0);

            Mlog.v(logTag, currentPackage);
            if ( preferences.getBoolean("block_current_app", true)
                    && !isLocked
                    && packageName.equals(currentPackage)
                    && !packageName.equals("com.notifications.screen")
                    ) {
                Mlog.d(logTag, "Current package match - stopping");
                stopSelf();
                return START_NOT_STICKY;
            }
            Set<String> blockedApps = (Set<String>) ObjectSerializer.deserialize(preferences.getString("noshowlist", ""));
            if (blockedApps != null && blockedApps.size() > 0) {
                final boolean isBlacklistInverted = preferences.getBoolean("noshowlist_inverted", false);
                boolean contains = blockedApps.contains(currentPackage);
                Mlog.v(logTag, blockedApps.toString());
                Mlog.v(logTag+"NoShow", String.format("%s %s", String.valueOf(isBlacklistInverted), contains));
                if (
                        (!isBlacklistInverted && contains)
                                ||(isBlacklistInverted && !contains)
                        ) {
                    Mlog.d(logTag+"NoShow", "Package match - stopping");
                    stopSelf();
                    return START_NOT_STICKY;
                }
            }

            try {
                if (packageName.equals("com.notifications.voiceover"))
                    appRes = getResources();
                else
                    appRes = pm.getResourcesForApplication(packageName);
            } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                reportError(e, "", getApplicationContext());
            }

            View dismissButton = themeClass.getDismissButton(main_view);
            if (preferences.getBoolean("hide_dismiss_button", true))
                themeClass.hideDismissButton(dismissButton);

            try {
                pendingIntent = (PendingIntent) extras.get("action");

                pendingIntentList.add(pendingIntent);
            } catch (NullPointerException npe) {
                reportError(npe, "", getApplicationContext());
            }

            final int color = extras.getInt("color");





            ImageView imageView = themeClass.getIconView(main_view);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    onPopupClick(v, preferences.getBoolean("floating_window", false));
                }
            });

            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    Drawable drawable = null;
                    Bitmap bitmap = (Bitmap) extras.get("iconLarge");
                    if (appRes != null && extras.containsKey("icon")) {
                        int icon_id = extras.getInt("icon");
                        if (icon_id > 0)
                            drawable = appRes.getDrawable(icon_id);
                    } else {
                        try {
                            drawable = pm.getApplicationIcon(packageName);
                        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                            reportError(e, "", getApplicationContext());
                        }
                    }
                    ImageView smallIconView = themeClass.getSmallIconView(main_view);
                    if (bitmap == null || bitmap.isRecycled()) {
                        if (drawable != null)
                            bitmap = drawableToBitmap(drawable);
                        if (smallIconView != null)
                            themeClass.setSmallIcon(smallIconView, null, color);
                    } else if (smallIconView != null)
                        themeClass.setSmallIcon(smallIconView, drawable, color);

                    if (bitmap != null && !bitmap.isRecycled()) {
                        final int shortestSide;
                        final int width = bitmap.getWidth();
                        final int height = bitmap.getHeight();
                        if (width > height) shortestSide = height;
                        else                shortestSide = width;

                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, shortestSide, shortestSide, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                        themeClass.setIcon( imageView, bitmap, preferences.getBoolean("round_icons", true), color);
                    }
                }
            } catch (Exception e) {
                reportError(e, "Icon", getApplicationContext());
            }



            TextView titleTextView = (TextView) main_view.findViewById(R.id.notification_title);
            final  TextView textView = (TextView) main_view.findViewById(R.id.notification_subtitle);

            TextView textView_pack = (TextView) main_view.findViewById(R.id.pack);


            //speech_to_text

            ImageView msg_send = (ImageView) main_view.findViewById(R.id.msg_snd);


            msg_send.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub


                    TextView textView_index = (TextView) ((View)(v.getParent().getParent())).findViewById(index);




                    Mlog.d(logTag, " MESSAGE REPLY -> textView_index "+textView_index.getText());

                    if (handler==null || handler.isEmpty()) {

                        Mlog.d(logTag, " MESSAGE REPLY -> 1 ");

                        stopSelf();
                    }



                    try {
                        if (handler.get(Integer.parseInt(textView_index.getText().toString())) != null) {
                            handler.get(Integer.parseInt(textView_index.getText().toString())).removeCallbacks(closeTimer);
                            Mlog.d(logTag, " MESSAGE REPLY -> 2 ");

                        }


                        handler.set(Integer.parseInt(textView_index.getText().toString()), null);
                    }catch (IndexOutOfBoundsException in)
                    {

                        Mlog.d(logTag, " MESSAGE REPLY -> 3 ");

                        Mlog.d(logTag, " Exception handler.get(Integer.parseInt(textView_index.getText().toString())) != null");


                        stopSelf();
                    }

                    msg_rply_mic = (EditText) ((View)(v.getParent())).findViewById(R.id.msg_rply);
                    msg_snd_mic = (ImageView) ((View)(v.getParent())).findViewById(R.id.msg_snd);



                    switch (val_thm) {

                        case 0: // L

                            msg_snd_mic.setImageResource(R.drawable.black_mic);


                            break;
                        case 1: // L Dark
                            msg_snd_mic.setImageResource(R.drawable.mic_white);


                            break;
                        case 5: // L Black

                            msg_snd_mic.setImageResource(R.drawable.mic_white);

                            break;
                        case 2: // Holo Light
                            msg_snd_mic.setImageResource(R.drawable.black_mic);


                            break;
                        case 3: // Holo

                            msg_snd_mic.setImageResource(R.drawable.mic_white);


                            break;
                        case 4: // Random

                            msg_snd_mic.setImageResource(R.drawable.mic_white);


                            break;
                        case 6: // ubuntu

                            msg_snd_mic.setImageResource(R.drawable.mic_white);



                            break;
                        case 7: // Custom_1

                            msg_snd_mic.setImageResource(R.drawable.mic_white);



                            break;

                    }

                    msg_rply_mic.setText("");

                    msg_rply_mic.setHint("Speak...");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        // turn off beep sound
                        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    }
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);



                    return true;
                }
            });


//addition start

            LinearLayout msg_rply_RL = (LinearLayout) main_view.findViewById(R.id.msg_rply_RL);


            if (reply_action_count>0)
            {
                msg_rply_RL.setVisibility(View.VISIBLE);

            }
            else
            {

                msg_rply_RL.setVisibility(View.GONE);

            }


            EditText txtEdit = (EditText) msg_rply_RL.findViewById(R.id.msg_rply);

            txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    Mlog.d(logTag, " MESSAGE REPLY -> onFocusChange " );

                    msg_reply_func(v);
                }
            });


//addition end



            textView_pack.setText(packageName);

            titleTextView.setText(title);

/*
            if (chatHeads.size()>1)
            {
                titleTextView.setText(" Nothing to Say ! ");

            }

            */

            final boolean privacy_on_lockscreen = isLocked && preferences.getBoolean("privacy_on_lockscreen", false);


            if (privacy_on_lockscreen)
            {


                if (val_thm<=6) {



                        //make textview right of imageview

                        RelativeLayout rel_layout = (RelativeLayout) main_view.findViewById(R.id.rel);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.BELOW, R.id.text_wrap);
                        params.addRule(RelativeLayout.RIGHT_OF, R.id.image_wrap);
                        rel_layout.setLayoutParams(params);

                    textView.setText("  "+pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)));



                }
                else
                {
                    textView.setText(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)));

                }


                    mSpannableString_l.add(new SpannableString(Html.fromHtml(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString())));

                Mlog.d(logTag + " mSpannableString_l Added at privacy_on_lockscreen", mSpannableString_l.size());

            }
            else
            {
                /*

                textView.setText(text);

                ViewTreeObserver vto_tmp = textView.getViewTreeObserver();
                vto_tmp.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewTreeObserver obs = textView.getViewTreeObserver();
                        obs.removeOnGlobalLayoutListener(this);
                      int  height = textView.getHeight();
                        int  scrollY = textView.getScrollY();
                        Layout layout = textView.getLayout();

                        int  firstVisibleLineNumber = layout.getLineForVertical(scrollY);
                        int  lastVisibleLineNumber = layout.getLineForVertical(height+scrollY);

                        Mlog.d(logTag, " text "+lastVisibleLineNumber);


                    }
                });


                */

                if (val_thm<=6)
                {



                    if (isCompact)
                    {
                        //make textview right of imageview

                        RelativeLayout rel_layout = (RelativeLayout) main_view.findViewById(R.id.rel);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.BELOW, R.id.text_wrap);
                        params.addRule(RelativeLayout.RIGHT_OF, R.id.image_wrap);
                        rel_layout.setLayoutParams(params);

                    }




                    final FrameLayout wrap_image = (FrameLayout) main_view.findViewById(R.id.image_wrap);



                    final ViewTreeObserver vto = wrap_image.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            wrap_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);




                //single_view
                if (wrap_image.getMeasuredHeight()!=0 && wrap_image.getMeasuredWidth()!=0)
                {
                    finalHeight = wrap_image.getMeasuredHeight();
                    finalWidth = wrap_image.getMeasuredWidth();
                }



                            // makeSpan();


                            //String plainText=getResources().getString(R.string.text_sample);


                            //   Spanned htmlText = Html.fromHtml(text.replace("\n","<t/>"));
                            // Spanned htmlText = Html.fromHtml(text);

                            Rect bounds = new Rect();


                            if (text.length()<12)
                            {
                                String temp=text;

                                while (temp.length()<12)
                                {

                                    temp+=" ";

                                }
                                textView.getPaint().getTextBounds(temp.substring(0, 10), 0, 1, bounds);
                            }
                            else
                            {
                                textView.getPaint().getTextBounds(text.substring(0, 10), 0, 1, bounds);

                            }

                            SpannableString mSpannableString = new SpannableString(Html.fromHtml(text.replace("\n","<br/>")));




                            int allTextStart = 0;
                            int allTextEnd = text.length() - 1;



                            int lines;


                            //float textLineHeight = mTextView.getPaint().getTextSize();
                            float fontSpacing = textView.getPaint().getFontSpacing();
                            lines = (int) (finalHeight / (fontSpacing));


                            MyLeadingMarginSpan2 span = new MyLeadingMarginSpan2(lines, finalWidth + 10);
                            mSpannableString.setSpan(span, allTextStart, allTextStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                            Mlog.d(logTag, " lines "+lines);
                            // Mlog.d(logTag, " text "+htmlText);
                            Mlog.d(logTag, " text "+mSpannableString);
                            Mlog.d(logTag, " text "+text);
                            // Mlog.d(logTag, " text "+htmlText.length());
                            Mlog.d(logTag, " text "+mSpannableString.length());
                            Mlog.d(logTag, " text "+text.length());


//error found


                                try
                                {
                                    mSpannableString_l.add(mSpannableString);

                                }
                                catch (NullPointerException ne)
                                {
                                    stopSelf();
                                }

                            //mSpannableString_l.add(mSpannableString);

                          //  Mlog.d(logTag + " mSpannableString_l Added at 1 ", mSpannableString_l.size());

                            if (isCompact)
                            {

                                textView.setText("  "+text);


                            }
                            else
                            {
                                textView.setText(mSpannableString);
                            }
                           // textView.setText(mSpannableString);



//                            Mlog.d(logTag, " text lineCount "+textView.getLayout().getLineCount());


/*

                            try {
                                if (textView.getLineCount()==1)
                                {
                                    textView.setText("  "+text);
                                }
                            }
                            catch (Exception e)
                            {
                                Mlog.d(logTag, " Exception caught "+e);

                            }

*/

/*
                            ViewTreeObserver vto = textView.getViewTreeObserver();
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    // Layout layout = mytextview.getLayout();

                                    Mlog.d(logTag, " text lCount 1 "+textView.getLayout().getLineCount());
                                    Mlog.d(logTag, " text lCount 2 "+textView.getLineCount());


                                    if (textView.getLayout().getLineCount()==1)
                                    {
                                        textView.setText("  "+text);
                                    }
                                }
                            });

*/




                        }
                    });




                    //textView.setText(text);

                }
                    /*
                else if (val_thm==6)
                {



                    if (isCompact)
                    {
                        //make textview right of imageview

                        RelativeLayout rel_layout = (RelativeLayout) main_view.findViewById(R.id.rel);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.BELOW, R.id.text_wrap);
                        params.addRule(RelativeLayout.RIGHT_OF, R.id.image_wrap);
                        rel_layout.setLayoutParams(params);

                    }




                    final LinearLayout wrap_image = (LinearLayout) main_view.findViewById(R.id.image_wrap);

                    final ViewTreeObserver vto = wrap_image.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            wrap_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            finalHeight = wrap_image.getMeasuredHeight();
                            finalWidth = wrap_image.getMeasuredWidth();





                            // makeSpan();


                            //String plainText=getResources().getString(R.string.text_sample);


                            //   Spanned htmlText = Html.fromHtml(text.replace("\n","<t/>"));
                            // Spanned htmlText = Html.fromHtml(text);

                            Rect bounds = new Rect();


                            if (text.length()<12)
                            {
                                String temp=text;

                                while (temp.length()<12)
                                {

                                    temp+=" ";

                                }
                                textView.getPaint().getTextBounds(temp.substring(0, 10), 0, 1, bounds);
                            }
                            else
                            {
                                textView.getPaint().getTextBounds(text.substring(0, 10), 0, 1, bounds);

                            }

                            SpannableString mSpannableString = new SpannableString(Html.fromHtml(text.replace("\n","<br/>")));




                            int allTextStart = 0;
                            int allTextEnd = text.length() - 1;



                            int lines;


                            //float textLineHeight = mTextView.getPaint().getTextSize();
                            float fontSpacing = textView.getPaint().getFontSpacing();
                            lines = (int) (finalHeight / (fontSpacing));


                            MyLeadingMarginSpan2 span = new MyLeadingMarginSpan2(lines, finalWidth + 10);
                            mSpannableString.setSpan(span, allTextStart, allTextStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                            Mlog.d(logTag, " lines "+lines);
                            // Mlog.d(logTag, " text "+htmlText);
                            Mlog.d(logTag, " text "+mSpannableString);
                            Mlog.d(logTag, " text "+text);
                            // Mlog.d(logTag, " text "+htmlText.length());
                            Mlog.d(logTag, " text "+mSpannableString.length());
                            Mlog.d(logTag, " text "+text.length());

                            mSpannableString_l.add(mSpannableString);

                            textView.setText(mSpannableString);


                            if (textView.getLayout().getLineCount()==1)
                            {
                                textView.setText("  "+text);
                            }




                        }
                    });




                    //textView.setText(text);

                }
*/
                    //new_edit_bubble
                else if (val_thm==7)
                {




                    final ImageView wrap_image = (ImageView) main_view.findViewById(R.id.notification_icon);

                    final ViewTreeObserver vto = wrap_image.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            wrap_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            finalHeight = wrap_image.getMeasuredHeight();
                            finalWidth = wrap_image.getMeasuredWidth();


                            // makeSpan();


                            /**
                             * Get the text
                             */
                            //String plainText=getResources().getString(R.string.text_sample);

                            Rect bounds = new Rect();



                            if (text.length()<12)
                            {
                                String temp=text;

                                while (temp.length()<12)
                                {

                                    temp+=" ";

                                }
                                textView.getPaint().getTextBounds(temp.substring(0, 10), 0, 1, bounds);
                            }
                            else
                            {
                                textView.getPaint().getTextBounds(text.substring(0, 10), 0, 1, bounds);

                            }

                            //Spanned htmlText = Html.fromHtml(text);
                            //SpannableString mSpannableString = new SpannableString(htmlText);
                            SpannableString mSpannableString = new SpannableString(Html.fromHtml(text.replace("\n","<br/>")));




                            int allTextStart = 0;
                            int allTextEnd = text.length() - 1;

                            /**
                             * Calculate the lines number = image height.
                             * You can improve it... it is just an example
                             */
                            int lines;



                            //float textLineHeight = mTextView.getPaint().getTextSize();
                            float fontSpacing = textView.getPaint().getFontSpacing();
                            lines = (int) (finalHeight / (fontSpacing));

                            /**
                             * Build the layout with LeadingMarginSpan2
                             */
                            MyLeadingMarginSpan2 span = new MyLeadingMarginSpan2(lines, finalWidth + 10);
                            mSpannableString.setSpan(span, allTextStart, allTextStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            mSpannableString_l.add(mSpannableString);

                            Mlog.d(logTag + " mSpannableString_l Added at 2 ", mSpannableString_l.size());


                            textView.setText(mSpannableString);
                        }
                    });





                }
                //new_edit_bubble
                /*
                else if (val_thm==8)
                {

                    String boldText =titleTextView.getText().toString() ;
                    String normalText = text;
                    SpannableString str = new SpannableString(boldText +" "+ normalText);
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    textView.setText(str);

                    // textView.setText(titleTextView.getText().toString()+"\n"+textView.getText().toString());

                }
                */

            }





            if (isCompact)
            {
                textView.setMaxLines(MIN_LINES);


                textView.setSelected(true);
                textView.setFocusable(true);
                textView.setFocusableInTouchMode(true);
                textView.setSingleLine(true);
                textView.setHorizontallyScrolling(true);
                textView.setMarqueeRepeatLimit(-1);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);



                textView.setSelected(true);

                //textView.setMovementMethod(new ScrollingMovementMethod());
            }
            else
            {


                textView.setMaxLines(MAX_LINES);

                textView.setSelected(false);
                textView.setFocusable(false);
                textView.setFocusableInTouchMode(false);
                textView.setHorizontallyScrolling(false);
                textView.setMarqueeRepeatLimit(0);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(false);


                textView.setSelected(false);

            }

            final Resources resources = getResources();



            // titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,sizeMultiplier * resources.getDimension(R.dimen.text_size_notification_title));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeMultiplier * resources.getDimension(R.dimen.text_size_notification_text));


            if (Build.VERSION.SDK_INT >= 16 && !privacy_on_lockscreen) {
                try {
                    ViewGroup actionButtons = themeClass.getActionButtons(main_view);
                    themeClass.removeActionButtons(actionButtons);
                    i = extras.getInt("actionCount");


                    actionCount.add(i);
                    isActionButtons = i > 0;
                    // if (isActionButtons)

                    {
                        Mlog.d(logTag, String.valueOf(i));

/*

                        if (i<2)
                        {
                            themeClass.showActionButtons(main_view, i+1);

                        }
                        else


                        {

                        */
                            themeClass.showActionButtons(main_view, i);

                      //  }


                        //size=i;




                        while (i > 0) {
                            String actionTitle = extras.getString("action" + i + "title");
                            final PendingIntent actionIntent = (PendingIntent)
                                    extras.get("action" + i + "intent");





                            int actionIcon = extras.getInt("action" + i + "icon");
                            Drawable icon = null;
                            if (appRes != null) {
                                try {
                                    icon = appRes.getDrawable(actionIcon);
                                } catch (Resources.NotFoundException ignored) {}
                            }


                            pendingIntentListb.add(actionIntent);



                            themeClass.addActionButton(actionButtons,pendingIntentListb.size()-1, actionTitle, icon, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {

                                        TextView titleTextView = (TextView) view.findViewById(R.id.bindex);



                                        //  View temp1 = ;
                                        // View temp2 = (View)temp1.getParent();
                                        //  View temp3 = (View)temp2.getParent();

                                        //  Mlog.d(logTag, " Parent1   "+temp1);


                                        if (((View)(view.getParent().getParent().getParent())).getTranslationX() != 0) return; // Stop if we're currently swiping. Bug 0000034


                                        Mlog.d(logTag, " Vieww   "+view);
                                        //check

                                        triggerIntent(pendingIntentListb.get(Integer.parseInt(titleTextView.getText().toString())), false,main_view);

                                        // stopSelf();

                                    }
                                    catch (NullPointerException n)
                                    {
                                       // stopSelf();

                                    }
                                    catch (IndexOutOfBoundsException ignored) {
                                       // stopSelf();

                                    } catch (RuntimeException rte) {
                                        reportError(rte, "ThemeActionIcon", getApplicationContext());
                                       // stopSelf();

                                    }

                                }
                            }, sizeMultiplier);

                            i--;
                        }




/*
                        if (actionCount.get(actionCount.size()-1)<2)
                        {




                            actionCount.set(actionCount.size()-1,actionCount.get(actionCount.size()-1)+1);
                            //actionCount.set(actionCount.size()-1,2);




                            Mlog.d(logTag, " Gooooot inside   ");




                            Drawable icon = null;
                            // if (appRes != null) {
                            try {
                                icon = getResources().getDrawable(R.drawable.ic_smile);
                            } catch (Resources.NotFoundException ignored) {}
                            // }


                            pendingIntentListb.add(PendingIntent.getActivity(this, 0,
                                    new Intent(Intent.ACTION_VIEW)
                                            .setData(Uri.parse("http://Screenfly/donate/#Screenfly"))
                                    , PendingIntent.FLAG_UPDATE_CURRENT));



                            themeClass.addActionButton(actionButtons,pendingIntentListb.size()-1, getString(R.string.action_pro), icon, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {

                                        TextView titleTextView = (TextView) view.findViewById(R.id.bindex);


                                        //  View temp1 = (View)view.getParent();
                                        //  View temp2 = (View)temp1.getParent();
                                        //  View temp3 = (View)temp2.getParent();

                                        //  Mlog.d(logTag, " Parent2   "+temp3);

                                        if (((View)(view.getParent().getParent().getParent())).getTranslationX() != 0) return; // Stop if we're currently swiping. Bug 0000034


                                        Mlog.d(logTag, " Vieww   "+view);
                                        //check

                                        triggerIntent(pendingIntentListb.get(Integer.parseInt(titleTextView.getText().toString())), false,main_view);

                                        // stopSelf();

                                    } catch (NullPointerException e) {
                                        reportError(e, "", getApplicationContext());
                                    }
                                }
                            }, sizeMultiplier);



                        }


*/






                        Mlog.d(logTag, " Gooooot out   "+actionCount.get(actionCount.size()-1));




                        if (isCompact) {
                            themeClass.hideActionButtons(main_view);
                        }



                    }

                    //else {
                    //   themeClass.hideActionButtons(main_view);

/*
                        //if (actionCount.get(actionCount.size()-1)<2)
                        {


                            if (actionCount.get(actionCount.size()-1)==0)
                            {
                                themeClass.showActionButtons(main_view, 1);

                            }



                            //actionCount.set(actionCount.size()-1,actionCount.get(actionCount.size()-1)+1);
                            actionCount.set(actionCount.size()-1,1);




                            Mlog.d(logTag, " Gooooot inside   ");




                            Drawable icon = null;
                            // if (appRes != null) {
                            try {
                                icon = getResources().getDrawable(R.drawable.ic_smile);
                            } catch (Resources.NotFoundException ignored) {}
                            // }


                            pendingIntentListb.add(PendingIntent.getActivity(this, 0,
                                    new Intent(Intent.ACTION_VIEW)
                                            .setData(Uri.parse("http://Screenfly/donate/#Screenfly"))
                                    , PendingIntent.FLAG_UPDATE_CURRENT));



                            themeClass.addActionButton(actionButtons,pendingIntentListb.size()-1, getString(R.string.action_pro), icon, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {

                                        TextView titleTextView = (TextView) view.findViewById(R.id.bindex);


                                        //  View temp1 = (View)view.getParent();
                                        //  View temp2 = (View)temp1.getParent();
                                        //  View temp3 = (View)temp2.getParent();

                                        //  Mlog.d(logTag, " Parent2   "+temp3);

                                        if (((View)(view.getParent().getParent().getParent())).getTranslationX() != 0) return; // Stop if we're currently swiping. Bug 0000034


                                        Mlog.d(logTag, " Vieww   "+view);
                                        //check

                                        triggerIntent(pendingIntentListb.get(Integer.parseInt(titleTextView.getText().toString())), false,main_view);

                                        // stopSelf();

                                    } catch (NullPointerException e) {
                                        reportError(e, "", getApplicationContext());
                                    }
                                }
                            }, sizeMultiplier);



                        }



                        if (isCompact) {
                            themeClass.hideActionButtons(main_view);
                        }


                        */

                    //  }



                    //edit from here









                    // edit to here














                } catch (NullPointerException npe) {// Ignored, usually happens in case of missing icons
                } catch (IndexOutOfBoundsException ignored) {
                } catch (RuntimeException rte) {
                    reportError(rte, "ThemeActionIcon", getApplicationContext());
                }
            } else {
                themeClass.hideActionButtons(main_view);
            }

            //changed to dismiss btn longclk
            // View longClk = main_view.findViewById(R.id.linearLayout);
            View longClk = main_view.findViewById(R.id.notification_dismiss);

            longClk.setOnLongClickListener(blockTouchListener);

            if (Build.VERSION.SDK_INT >= 12) {


                mainlayout.setOnTouchListener(new View.OnTouchListener() {


                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_OUTSIDE: {

                                Mlog.e(logTag, " Outside Touch ");

                                // We won't get X and Y for outside touches


                                if (preferences.getBoolean("close_on_outside_touch", true)) {

                                    if (isLocked) pokeScreenTimer();
                                    {
                                        //chatHeads = null;
                                        mSpannableString_l = null;
                                        actionCount = null;

                                        //addition

                                        notificationWear_list = null;

                                        pendingIntentList = null;
                                        pendingIntentListb = null;

                                        //  indexH = null;

                                        //layout.removeAllViews();
                                        stopSelf();
                                        //doFinish(0, null);
                                    }
                                }
                            }
                        }
                        return true;
                    }
                });







                final ViewGroup self = themeClass.getRootView(main_view);
                //ViewGroup self =(ViewGroup) main_view.findViewById(R.id.notificationbg);

                // Init swipe listener
                final SwipeDismissTouchListener dismissTouchListener = new SwipeDismissTouchListener(
                        self, position == 2 || position == 1, new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss() {
                        return true;
                    }

                    @Override
                    public boolean canExpand() {
                        return isCompact;
                    }

                    @Override
                    public void onDismiss(View view, Object token, int direction) {
                        //Mlog.v(logTag, "DIR" + direction);
                        switch (direction) {
                            case SwipeDismissTouchListener.DIRECTION_LEFT:
                            case SwipeDismissTouchListener.DIRECTION_RIGHT:
                                try {

                                    Mlog.d(logTag, " mmmmmmmmmmmmmm " + view);


                                    Mlog.d(logTag, " Swipeinggg Child Count Before " + layout.getChildCount());
                                    TextView textView_index = (TextView) view.findViewById(index);


                                    // layout.removeView(chatHeads.get(chatHeads.size()-1));
                                    // chatHeads.remove(chatHeads.size()-1);
                                    //actionCount.remove(actionCount.size()-1);
                                    int inx = Integer.parseInt(textView_index.getText().toString());

                                    Mlog.d(logTag, " Swipeinggg INXXXXX " + inx);

                                    //View main = (View)view.getRootView().findViewById(inx);




                                    View chumma;
                                    TextView textView_index_temp;


                                    layout.removeView(view.getRootView().findViewById(inx));




                                    //remove call back here

                                    Mlog.d(logTag, " IIIIII " + handler.size() + "   " + inx);

                                    if (handler.get(inx) != null) {
                                        handler.get(inx).removeCallbacks(closeTimer);

                                    }

                                    handler.remove(inx);

                                    //  indexH.remove(inx);


                                    for (int i = inx + 1; i < layout.getChildCount() + 1; i++) {


                                        Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I " + i);


                                        chumma = layout.getRootView().findViewById(i);


                                        //   indexH.set(i,i-1);


                                        textView_index_temp = (TextView) chumma.findViewById(index);
                                        textView_index_temp.setText(Integer.toString(i - 1));
                                        chumma.setId(i - 1);


                                    }


                                    //for pendingIntentListb
                                    /*

                                                                    int start=-1;

                                                                    for (int i=1;i<=inx;i++)
                                                                    {
                                                                        start+=actionCount.get(i-1);
                                                                    }


                                                                    if (start==-1)
                                                                    {
                                                                        start = 0;
                                                                    }

                                                                    int end = start+actionCount.get(inx);







                                                                    if (start!=end)
                                                                    {
                                                                        for (int i=start;i<=end;i++)
                                                                        {
                                                                            //pendingIntentListb.remove(i);
                                                                            pendingIntentListb.set(i,null);

                                                                        }
                                                                    }

                                    */
                                    //upto here for pendingIntentListb


                                    //chatHeads.remove(inx);
                                    mSpannableString_l.remove(inx);
                                    actionCount.remove(inx);

                                    //addition

                                    notificationWear_list.remove(inx);

                                    pendingIntentList.remove(inx);


                                    //Mlog.d(logTag, " Swipeinggg XAAAAAA Chathead size   "+chatHeads.size());

                                    /*
                                                                    if (!chatHeads.isEmpty())
                                                                    {

                                                                        for (int i=0;i<chatHeads.size();i++)
                                                                        {


                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA I   "+i);


                                                                            chumma = main_view.getRootView().findViewById(i);

                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA ID  "+chumma.getId());


                                                                            textView_index_temp = (TextView)chumma.findViewById(R.id.index);

                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA TEXT "+textView_index_temp.getText());







                                                                        }

                                                                    }

                                    */

                                    if (layout.getChildCount() == 0) {
                                        //windowManager.removeView(layout);


                                        stopSelf();

                                    }

                                    if ( actionCount==null || actionCount.isEmpty()) {
                                        //windowManager.removeView(layout);


                                        stopSelf();

                                    }

                                    //Mlog.d(logTag, " Swipeinggg chatHeads "+chatHeads.size());
                                    Mlog.d(logTag, " Swipeinggg actionCount " + actionCount.size());
                                    Mlog.d(logTag, " Swipeinggg Child Count After " + layout.getChildCount());




                                    //single_view
                                    if (preferences.getBoolean("single_view", false) && !isLocked())
                                    {

                                        Mlog.d(logTag, " single_view Direction inx "+inx);

                                        Mlog.d(logTag, " single_view Direction child_Count "+layout.getChildCount());

                                        Mlog.d(logTag, " single_view Direction handler_Count "+handler.size());




                                        //view.getRootView().findViewById(inx).setVisibility(View.VISIBLE);
                                        layout.findViewById(inx).setVisibility(View.VISIBLE);

                                        layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
                                        notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);
                                        notification_no.setText(Integer.toString(layout.getChildCount()-1));


                                        handler.get(inx).postDelayed(closeTimer, displayTime);



                                    }




                                }
                                catch (NullPointerException n)
                                {

                                }
                                 catch (IndexOutOfBoundsException ignored) {
                                } catch (RuntimeException rte) {
                                    reportError(rte, "ThemeActionIcon", getApplicationContext());
                                }

                                break;
                                /*
                                if (preferences.getBoolean("dismiss_on_swipe", true))
                                {
                                    Mlog.d(logTag, " from here");
                                   if (chatHeads.isEmpty())
                                    Mlog.d(logTag, " from here chatheads empty");
                                    else
                                    Mlog.d(logTag, " from here chatheads index "+chatHeads.indexOf(view));
                                   // TextView index = (TextView)view.findViewById(R.id.notification_title);

                                    TextView index = (TextView)view.getRootView().findViewById(R.id.index);

                                    Mlog.d(logTag, " from here main index "+Integer.parseInt(index.getText().toString()));

                                    doFinish(1,view);
                                }
                               // else                                                  doFinish(0,null);
                                break;
                                */
                            // case SwipeDismissTouchListener.DIRECTION_UP:
                            //doFinish(0);
                            // break;
                            // case SwipeDismissTouchListener.DIRECTION_DOWN:
                            // expand(view);
                            // return;

                            default:
                                Mlog.e(logTag, "Unknown direction: " + direction);
                                break;
                        }
                        //main_view.setVisibility(View.GONE);
                    }

                    @Override
                    public void outside() {

                        Mlog.e(logTag, " Outside Touch ");


                        if (preferences.getBoolean("close_on_outside_touch", true) || (isLocked && !isLocked())) {

                            if (isLocked) pokeScreenTimer();
                            {
                                //chatHeads = null;
                                mSpannableString_l = null;
                                actionCount = null;

                                //addition

                                notificationWear_list = null;
                                pendingIntentList = null;
                                pendingIntentListb = null;
                                //  indexH = null;

                                stopSelf();
                                //doFinish(0, null);
                            }
                        }

                    }
                }
                );
                self.setClipChildren(false);
                self.setClipToPadding(false);
                final ArrayList<View> allChildren = getAllChildren(main_view);
                if (allChildren.size() > 0)
                    for (View v : allChildren) {
                        v.setOnTouchListener(dismissTouchListener);
                    }
/*
                // Animate in
                if (!prevPackageName.equals(packageName)) {
                    AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (displayTime < MAX_DISPLAY_TIME || !intent.getAction().equals("STAY")) {
                                handler.postDelayed(closeTimer, displayTime);
                                System.gc();
                            }
                        }
                    };
                    self.setTranslationX(0);
                    switch (position) {
                        case 2:
                        case 1:
                            self.setTranslationY(-300);
                            break;
                        case 0:
                            self.setTranslationX(-self.getWidth());
                            break;
                        case -1:
                            self.setTranslationY(300);
                    }
                    self.setAlpha(0.0f);
                    Float opacity = (float) preferences.getInt("opacity", 98);
                    if (opacity == null)
                        opacity = 98f;
                    try {
                        self.animate()
                                .setDuration(700)
                                .alpha(opacity / 100)
                                .translationY(0)
                                .translationX(0)
                                .setListener(listener);
                    } catch (NullPointerException npe) {
                        reportError(npe, "", getApplicationContext());
                        if (displayTime < MAX_DISPLAY_TIME || !intent.getAction().equals("STAY"))
                            handler.postDelayed(closeTimer, displayTime);
                    }
                    prevPackageName = packageName;
                }
                else
                    */{



                    if (isLocked() && preferences.getBoolean("keep_on_lock_screen", false) )
                    {

                        Mlog.d(logTag, " Inside if islocked "+isLocked());
                        Mlog.d(logTag, " Inside if pref "+preferences.getBoolean("keep_on_lock_screen", false));


                        if (!actionCount.isEmpty())
                        {
                            handler.add(actionCount.size()-1,null);
                        }


                    }
                    else if (displayTime < MAX_DISPLAY_TIME || !intent.getAction().equals("STAY")) {
                        //here



                        Mlog.d(logTag, " Inside elseif islocked "+isLocked());
                        Mlog.d(logTag, " Inside elseif pref "+preferences.getBoolean("keep_on_lock_screen", false));

                        if (!actionCount.isEmpty())
                        {
                            handler.add(actionCount.size()-1,new Handler());
                            //handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                            //single_view
                            if (preferences.getBoolean("single_view", false) && !isLocked())
                            {

                               // handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime*handler.size());

                                if (handler.size()==1)
                                {
                                    handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                                }


                            }
                            else {
                                handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                            }
                        }


                        //  handler.postDelayed(closeTimer, displayTime);
                    }
                    else
                    {
                        Mlog.d(logTag, " Inside else islocked "+isLocked());
                        Mlog.d(logTag, " Inside else pref "+preferences.getBoolean("keep_on_lock_screen", false));


                        if (!actionCount.isEmpty())
                        {
                            handler.add(actionCount.size()-1,null);
                        }
                    }
                }
            } else {
                textView.setMaxLines(MAX_LINES);
                if (isLocked() && preferences.getBoolean("keep_on_lock_screen", false) )
                {


                    Mlog.d(logTag, " Outside if islocked "+isLocked());
                    Mlog.d(logTag, " Outside if pref "+preferences.getBoolean("keep_on_lock_screen", false));


                    if (!actionCount.isEmpty())
                    {
                        handler.add(actionCount.size()-1,null);
                    }


                }
                else if (displayTime < MAX_DISPLAY_TIME || !intent.getAction().equals("STAY")) {
                    //here


                    Mlog.d(logTag, " Outside elseif islocked "+isLocked());
                    Mlog.d(logTag, " Outside elseif pref "+preferences.getBoolean("keep_on_lock_screen", false));


                    if (!actionCount.isEmpty())
                    {
                        handler.add(actionCount.size()-1,new Handler());

                        //single_view
                        if (preferences.getBoolean("single_view", false) && !isLocked())
                        {

                            //handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime*handler.size());

                            if (handler.size()==1)
                            {
                                handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                            }


                        }
                        else {
                            handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                        }

                    }


                    //  handler.postDelayed(closeTimer, displayTime);
                }
                else
                {

                    Mlog.d(logTag, " Outside else islocked "+isLocked());
                    Mlog.d(logTag, " Outside else pref "+preferences.getBoolean("keep_on_lock_screen", false));


                    if (!actionCount.isEmpty())
                    {
                        handler.add(actionCount.size()-1,null);
                    }
                }
            }

            //main_view.setVisibility(View.VISIBLE);

        } catch (Exception catchAllException) {
            reportError(catchAllException, "", getApplicationContext());
            //removed
            //stopSelf();
        }



        Mlog.d(logTag + " mSpannableString_l End", mSpannableString_l.size());


        displayWindow();




        return START_NOT_STICKY;
    }

//addition
    public void msg_reply_func(View view)
    {

        Mlog.d(logTag, " MESSAGE REPLY");

        //disable timer



        TextView textView_index = (TextView) ((View)(view.getParent().getParent())).findViewById(index);




        Mlog.d(logTag, " MESSAGE REPLY -> textView_index "+textView_index.getText());

        if (handler==null || handler.isEmpty()) {

            Mlog.d(logTag, " MESSAGE REPLY -> 1 ");

            return;
        }



        try {
            if (handler.get(Integer.parseInt(textView_index.getText().toString())) != null) {
                handler.get(Integer.parseInt(textView_index.getText().toString())).removeCallbacks(closeTimer);
                Mlog.d(logTag, " MESSAGE REPLY -> 2 ");

            }


            handler.set(Integer.parseInt(textView_index.getText().toString()), null);
        }catch (IndexOutOfBoundsException in)
        {

            Mlog.d(logTag, " MESSAGE REPLY -> 3 ");

            Mlog.d(logTag, " Exception handler.get(Integer.parseInt(textView_index.getText().toString())) != null");


            stopSelf();
        }


    }

//addition

    public void msg_reply_send(View view)
    {

        TextView textView_index = (TextView) ((View)(view.getParent().getParent())).findViewById(R.id.index);
        EditText msg_rply = (EditText) ((View)(view.getParent())).findViewById(R.id.msg_rply);

        Mlog.d(logTag, " Msg text : "+msg_rply.getText());


       // if (!(msg_rply.getText().equals(""))) {

        if (!(msg_rply.getText().toString().matches(""))) {



            try {



                Reply(msg_rply.getText().toString(), notificationWear_list.get(Integer.parseInt(textView_index.getText().toString())));





            } catch (IndexOutOfBoundsException in) {



                tv.setText("Sorry unable to send message !");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();


                Mlog.d(logTag, " Index Out of bound Reply()");


                stopSelf();
            }
            catch (NullPointerException ne)
            {

                tv.setText("Sorry unable to send message !");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();



                stopSelf();

            }



            try {



                handler.set(Integer.parseInt(textView_index.getText().toString()),new Handler());
                handler.get(Integer.parseInt(textView_index.getText().toString())).postDelayed(closeTimer, 3000);


            } catch (IndexOutOfBoundsException in) {


                Mlog.d(logTag, " Index Out of bound Reply()");

                tv.setText("Message Send");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();

                stopSelf();
            }
            catch (NullPointerException ne)
            {

                tv.setText("Message Send");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();

                stopSelf();

            }






            msg_rply.setHint("Message Sent");

            msg_rply.setText("");


            //((View)(view.getParent())).setVisibility(View.GONE);

            msg_rply.setInputType(0);

            view.setVisibility(View.INVISIBLE);
            view.setEnabled(false);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(msg_rply.getWindowToken(), 0);

        }

    }


    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private ArrayList<View> getAllChildren(View v) {
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }







    //private final Handler handler = new Handler();


    private final Runnable closeTimer = new Runnable() {
        @Override
        public void run() {



try {



            int inx = 0;


            while (handler.get(inx)==null)
            {
                inx+=1;
            }

            Mlog.d(logTag, " Swipeinggg INXXXXX "+inx);

            //View main = (View)view.getRootView().findViewById(inx);

            View chumma;
            TextView textView_index_temp;



            layout.removeView(layout.getRootView().findViewById(inx));


            //remove call back here

            handler.remove(inx);


            for (int i=inx+1;i<layout.getChildCount()+1;i++)
            {


                Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I "+i);


                chumma = layout.getRootView().findViewById(i);


                //indexH.set(i,i-1);



                textView_index_temp = (TextView)chumma.findViewById(index);
                textView_index_temp.setText(Integer.toString(i-1));
                chumma.setId(i-1);



            }


            //chatHeads.remove(inx);
            mSpannableString_l.remove(inx);
            actionCount.remove(inx);

    //addition

    notificationWear_list.remove(inx);

    pendingIntentList.remove(inx);



            if (layout.getChildCount()==0)
            {
                //windowManager.removeView(layout);


                stopSelf();
            }





    //single_view
    if (preferences.getBoolean("single_view", false) && !isLocked())
    {


        Mlog.d(logTag, " single_view Timer inx "+inx);

        Mlog.d(logTag, " single_view Timer child_Count "+layout.getChildCount());

        Mlog.d(logTag, " single_view Timer handler_Count "+handler.size());





        layout.findViewById(inx).setVisibility(View.VISIBLE);

        layout.findViewById(inx).findViewById(R.id.notification_no).setVisibility(View.VISIBLE);
        notification_no = (TextView)layout.findViewById(inx).findViewById(R.id.notification_no);
        notification_no.setText(Integer.toString(layout.getChildCount()-1));

        handler.get(inx).postDelayed(closeTimer, displayTime);


    }

        }
catch (NullPointerException n)
        {

        }
catch (IndexOutOfBoundsException ignored) {
        } catch (RuntimeException rte) {
            reportError(rte, "ThemeActionIcon", getApplicationContext());
        }



        }
    };





    @SuppressWarnings("UnusedDeclaration")
    public void doStop(View v) {


        View view = ((View)(v.getParent().getParent()));

        try {

            Mlog.d(logTag, " mmmmmmmmmmmmmm " + view);


            Mlog.d(logTag, " Swipeinggg Child Count Before " + layout.getChildCount());
            TextView textView_index = (TextView) view.findViewById(index);


            // layout.removeView(chatHeads.get(chatHeads.size()-1));
            // chatHeads.remove(chatHeads.size()-1);
            //actionCount.remove(actionCount.size()-1);
            int inx = Integer.parseInt(textView_index.getText().toString());

            Mlog.d(logTag, " Swipeinggg INXXXXX " + inx);

            //View main = (View)view.getRootView().findViewById(inx);

            View chumma;
            TextView textView_index_temp;


            layout.removeView(view.getRootView().findViewById(inx));


            //remove call back here

            Mlog.d(logTag, " IIIIII " + handler.size() + "   " + inx);

            if (handler.get(inx) != null) {
                handler.get(inx).removeCallbacks(closeTimer);

            }

            handler.remove(inx);

            //  indexH.remove(inx);


            for (int i = inx + 1; i < layout.getChildCount() + 1; i++) {


                Mlog.d(logTag, " Swipeinggg Vaaaaaaaaaaa I " + i);


                chumma = layout.getRootView().findViewById(i);


                //   indexH.set(i,i-1);


                textView_index_temp = (TextView) chumma.findViewById(index);
                textView_index_temp.setText(Integer.toString(i - 1));
                chumma.setId(i - 1);


            }


            //for pendingIntentListb
                                    /*

                                                                    int start=-1;

                                                                    for (int i=1;i<=inx;i++)
                                                                    {
                                                                        start+=actionCount.get(i-1);
                                                                    }


                                                                    if (start==-1)
                                                                    {
                                                                        start = 0;
                                                                    }

                                                                    int end = start+actionCount.get(inx);







                                                                    if (start!=end)
                                                                    {
                                                                        for (int i=start;i<=end;i++)
                                                                        {
                                                                            //pendingIntentListb.remove(i);
                                                                            pendingIntentListb.set(i,null);

                                                                        }
                                                                    }

                                    */
            //upto here for pendingIntentListb


            //chatHeads.remove(inx);
            mSpannableString_l.remove(inx);
            actionCount.remove(inx);

            //addition

            notificationWear_list.remove(inx);

            pendingIntentList.remove(inx);


            //Mlog.d(logTag, " Swipeinggg XAAAAAA Chathead size   "+chatHeads.size());

                                    /*
                                                                    if (!chatHeads.isEmpty())
                                                                    {

                                                                        for (int i=0;i<chatHeads.size();i++)
                                                                        {


                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA I   "+i);


                                                                            chumma = main_view.getRootView().findViewById(i);

                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA ID  "+chumma.getId());


                                                                            textView_index_temp = (TextView)chumma.findViewById(R.id.index);

                                                                            Mlog.d(logTag, " Swipeinggg XAAAAAA TEXT "+textView_index_temp.getText());







                                                                        }

                                                                    }

                                    */

            if (layout.getChildCount() == 0) {
                //windowManager.removeView(layout);


                stopSelf();
            }

            //Mlog.d(logTag, " Swipeinggg chatHeads "+chatHeads.size());
            Mlog.d(logTag, " Swipeinggg actionCount " + actionCount.size());
            Mlog.d(logTag, " Swipeinggg Child Count After " + layout.getChildCount());

        }
        catch (NullPointerException n)
        {

        }
        catch (IndexOutOfBoundsException ignored) {
        } catch (RuntimeException rte) {
            reportError(rte, "ThemeActionIcon", getApplicationContext());
        }


        //doFinish(1,v);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void doHide(View v) {

        //doFinish(1,v);
    }

    @SuppressWarnings("unused")
    public void onPopupClick(View v) {





        if (v==null)
            return;
        onPopupClick(v, false);
    }

    public void onPopupClick(View v, boolean isFloating) {


        //v = v.getRootView();


        // if (Build.VERSION.SDK_INT >= 12 || !expand(v)) triggerIntent(pendingIntent, isFloating,v);
        // if (!expand(v)) triggerIntent(pendingIntent, isFloating,v);


        TextView textView_index = (TextView) v.findViewById(index);


        // layout.removeView(chatHeads.get(chatHeads.size()-1));
        // chatHeads.remove(chatHeads.size()-1);
        //actionCount.remove(actionCount.size()-1);

        //v = v.findViewById(Integer.parseInt(textView_index.getText().toString()));


        final ViewGroup rootView = (ViewGroup) v.findViewById(R.id.linearLayout);

        if (rootView == null)
            return;
        //final ViewGroup rootView = themeClass.getRootView(v);
        if (rootView.getTranslationX() != 0 || rootView.getTranslationY() != 0)
            return; // Stop if we're currently swiping. Bug 0000034 (in the old bug tracker)


        if (v.findViewById(R.id.button_container) == null)
            return;


        //TextView index = (TextView) v.findViewById(R.id.index);
        TextView subtitle = (TextView) v.findViewById(R.id.notification_subtitle);

        TextView title = (TextView) v.findViewById(R.id.notification_title);







        //RelativeLayout rel_layout = (RelativeLayout) v.findViewById(R.id.rel);

        //new <code></code>

        Mlog.d(logTag, " triggerIntent c " + pendingIntentList.size());


        //error
        if (actionCount==null || actionCount.isEmpty())
            stopSelf();


        if (handler==null || handler.isEmpty())
            return;



        try {


            //single_view

            /*
            if (preferences.getBoolean("single_view", false) && !isLocked())
            {

                handler.get(Integer.parseInt(textView_index.getText().toString())).removeCallbacks(closeTimer);


                handler.get(Integer.parseInt(textView_index.getText().toString())).postDelayed(closeTimer, displayTime*5);




            }
            else {
                //handler.get(actionCount.size()-1).postDelayed(closeTimer, displayTime);

                if (handler.get(Integer.parseInt(textView_index.getText().toString())) != null) {



                    handler.get(Integer.parseInt(textView_index.getText().toString())).removeCallbacks(closeTimer);

                }


                handler.set(Integer.parseInt(textView_index.getText().toString()), null);


            }

            */


            if (handler.get(Integer.parseInt(textView_index.getText().toString())) != null) {



                handler.get(Integer.parseInt(textView_index.getText().toString())).removeCallbacks(closeTimer);

            }


            handler.set(Integer.parseInt(textView_index.getText().toString()), null);




        }catch (IndexOutOfBoundsException in)
        {


            Mlog.d(logTag, " Exception handler.get(Integer.parseInt(textView_index.getText().toString())) != null");


            stopSelf();
        }
        // if (v.findViewById(R.id.button_container).getVisibility()!=View.VISIBLE ) {


        //}

        Mlog.d(logTag, " countpopup " + subtitle.getLineCount());
      //  Mlog.d(logTag, " countpopup " + v.findViewById(R.id.button_container).getVisibility());
        Mlog.d(logTag, " countpopup " + View.VISIBLE);
        Mlog.d(logTag, " countpopup " + View.INVISIBLE);
        Mlog.d(logTag, " countpopup " + View.GONE);

/*

        if (subtitle.getLineCount()>1 || v.findViewById(R.id.button_container).getVisibility()==View.VISIBLE)
        {
            triggerIntent(pendingIntentList.get(Integer.parseInt(textView_index.getText().toString())), isFloating, v);

        }
        else// if (actionCount.get(Integer.parseInt(textView_index.getText().toString()))>0 && v.findViewById(R.id.button_container).getVisibility()!=View.VISIBLE)
            {

                Mlog.d(logTag, " Rrrrrrrrrrr 4 ");


                //disable right of imageview


                if (val_thm<=6) {

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.text_wrap);
                    rel_layout.setLayoutParams(params);


                }


                subtitle.setText(mSpannableString_l.get(Integer.parseInt(textView_index.getText().toString())));



                //  isCompact = false;
                subtitle.setMaxLines(MAX_LINES);


                subtitle.setSelected(false);
                subtitle.setFocusable(false);
                subtitle.setFocusableInTouchMode(false);
                subtitle.setHorizontallyScrolling(false);
                subtitle.setMarqueeRepeatLimit(0);
                subtitle.setEllipsize(TextUtils.TruncateAt.END);
                subtitle.setSingleLine(false);


                subtitle.setSelected(false);


                if (actionCount.get(Integer.parseInt(textView_index.getText().toString()))>0)
                    themeClass.showActionButtons(v, -1);
                if (displayTime < MAX_DISPLAY_TIME) {
                    //here handler.removeCallbacks(closeTimer);
                    //here handler.postDelayed(closeTimer, displayTime);
                }
        }

*/


        if (subtitle.getMaxLines()>MIN_LINES || v.findViewById(R.id.button_container).getVisibility()==View.VISIBLE) {

            Mlog.d(logTag, " Rrrrrrrrrrr 1 ");


            triggerIntent(pendingIntentList.get(Integer.parseInt(textView_index.getText().toString())), isFloating, v);
            // stopSelf();


        }
        else {

            Mlog.d(logTag, " Rrrrrrrrrrr 2 ");
            Mlog.d(logTag, " Rrrrrrrrrrr subtitle.getLineCount() "+subtitle.getLineCount());

            if ((subtitle.getLineCount() <= MIN_LINES && subtitle.length() < 80) && actionCount.get(Integer.parseInt(textView_index.getText().toString()))>0 && v.findViewById(R.id.button_container).getVisibility()==View.VISIBLE) {



                Mlog.d(logTag, " Rrrrrrrrrrr 3 ");
                Mlog.d(logTag, " Rrrrrrrrrrr MIN_LINES "+MIN_LINES);
                Mlog.d(logTag, " Rrrrrrrrrrr subtitle.length() "+subtitle.length());
                Mlog.d(logTag, " Rrrrrrrrrrr actionCount "+actionCount.get(Integer.parseInt(textView_index.getText().toString())));


                //triggerIntent(pendingIntent, isFloating, v);
                triggerIntent(pendingIntentList.get(Integer.parseInt(textView_index.getText().toString())), isFloating, v);
                // stopSelf();


            } else {

                Mlog.d(logTag, " Rrrrrrrrrrr 4 ");


                //disable right of imageview


                if (val_thm<=6) {

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.text_wrap);

                    RelativeLayout rel_layout = (RelativeLayout) v.findViewById(R.id.rel);


                    rel_layout.setLayoutParams(params);


                }


                //subtitle.setText(mSpannableString_l.get(Integer.parseInt(textView_index.getText().toString())));
                //if (val_thm!=8)
                {
                    subtitle.setText(mSpannableString_l.get(Integer.parseInt(textView_index.getText().toString())));
                }
                //new_edit_bubble
                /*
                else {
                    String boldText =title.getText().toString() ;
                    String normalText = subtitle.getText().toString();
                    SpannableString str = new SpannableString(boldText +"\n"+ normalText.substring(boldText.length()+1));
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    subtitle.setText(str);

                }
                */


                //  isCompact = false;
                subtitle.setMaxLines(MAX_LINES);


                subtitle.setSelected(false);
                subtitle.setFocusable(false);
                subtitle.setFocusableInTouchMode(false);
                subtitle.setHorizontallyScrolling(false);
                subtitle.setMarqueeRepeatLimit(0);
                subtitle.setEllipsize(TextUtils.TruncateAt.END);
                subtitle.setSingleLine(false);


                subtitle.setSelected(false);


                if (actionCount.get(Integer.parseInt(textView_index.getText().toString()))>0)
                    themeClass.showActionButtons(v, -1);
                if (displayTime < MAX_DISPLAY_TIME) {
                    //here handler.removeCallbacks(closeTimer);
                    //here handler.postDelayed(closeTimer, displayTime);
                }
            }






        }






    }

    /*
     * Expand the Screenfly. Returns true if the Screenfly was expanded, false if it was expanded before calling this method.
     */
    private boolean expand(View view) {
        if (!isCompact)
            return false;
        else {
            TextView subtitle = (TextView) view.findViewById(R.id.notification_subtitle);
            if ( (subtitle.getLineCount() <= MIN_LINES && subtitle.length() < 80) && !isActionButtons) {
                return false;
            }
            //isCompact = false;
            subtitle.setMaxLines(MAX_LINES);


            subtitle.setSelected(false);
            subtitle.setFocusable(false);
            subtitle.setFocusableInTouchMode(false);
            subtitle.setHorizontallyScrolling(false);
            subtitle.setMarqueeRepeatLimit(0);
            subtitle.setEllipsize(TextUtils.TruncateAt.END);
            subtitle.setSingleLine(false);


            subtitle.setSelected(false);


            if (isActionButtons)
                themeClass.showActionButtons(view, -1);

            /*
            if (displayTime < MAX_DISPLAY_TIME) {
                handler.removeCallbacks(closeTimer);
                handler.postDelayed(closeTimer, displayTime);
            }

            */
            return true;
        }
    }

    private void triggerIntent(PendingIntent mPendingIntent, boolean isFloating,View view) {

        int i=0;

        if (isLocked() && preferences.getBoolean("dismiss_keyguard", true)) {
            pokeScreenTimer();
            if (preferences.getBoolean("dismiss_keyguard_dirty", false)) {
                Mlog.d(logTag, " triggerIntent 2 ");


                dismissKeyguard();
                i = openIntent(mPendingIntent, isFloating,view);
            } else {

                Mlog.d(logTag, " triggerIntent 3 ");


                startActivity(new Intent(getApplicationContext(), UnlockActivity.class)
                        .putExtra("action", mPendingIntent)
                        .putExtra("floating", isFloating)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );



            }

            Mlog.d(logTag, " Rrrrrrrrrrr 2 ");

            //doFinish(2,view);
        } else {
            i =  openIntent(mPendingIntent, isFloating,view);
        }



        if (i==0)
        {
            stopSelf();
        }

    }

    private int openIntent(PendingIntent mPendingIntent, boolean isFloating,View view) {

        int i=0;
        try {
            Mlog.d(logTag, "sendPending");

            if (mPendingIntent!=null)
            {
                Mlog.d(logTag, " nulllll "+mPendingIntent);


            }

            Intent intent = new Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isFloating) intent.addFlags(FLAG_FLOATING_WINDOW);
            mPendingIntent.send(getApplicationContext(), 0, intent);
            //doFinish(2,view);
        } catch (PendingIntent.CanceledException e) {
            //reportError(e, "App has canceled action", getApplicationContext());
            i=1;
            //Toast.makeText(getApplicationContext(), getString(R.string.pendingintent_cancel_exception), Toast.LENGTH_SHORT).show();
            tv.setText(getString(R.string.pendingintent_cancel_exception));
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();
            //  doFinish(0,view);
        } catch (NullPointerException e) {
            //reportError(e, "No action defined", getApplicationContext());
            i=1;
            // Toast.makeText(getApplicationContext(), getString(R.string.pendingintent_null_exception), Toast.LENGTH_SHORT).show();
            tv.setText( getString(R.string.pendingintent_null_exception));
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();
            // doFinish(0,view);
        }

        return i;

        //stopSelf();
    }

    private void dismissKeyguard() {
        if (Build.VERSION.SDK_INT >= 16) {
            if (!preferences.getBoolean("dismiss_keyguard", false)) return;

            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardLocked()) {
                Mlog.d(logTag, "attempt exit");
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), KeyguardRelock.class);
                intent.setAction(Intent.ACTION_SCREEN_ON);
                startService(intent);
            }
        }
    }

    private final View.OnLongClickListener blockTouchListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {


            View view = ((View)(v.getParent().getParent()));
            Mlog.v(logTag + " VIEW NAME  "+view);

            TextView temp = (TextView)view.findViewById(R.id.pack);
            packageName = temp.getText().toString();
            Mlog.v(logTag + " PACKAGE NAME  "+packageName);


            try {
                Set<String> blacklist = (Set<String>) ObjectSerializer.deserialize(preferences.getString("blacklist", ""));
                if (blacklist == null) blacklist = new HashSet<>();

                final boolean isBlacklistInverted = preferences.getBoolean("blacklist_inverted", false);
                Mlog.v(logTag, isBlacklistInverted);

                if (isBlacklistInverted) {
                    if (blacklist.contains(packageName) && blacklist.remove(packageName))
                    {
                        //Toast.makeText(getApplicationContext(), getText(R.string.blocked_confirmation), Toast.LENGTH_SHORT).show();

                        tv.setText( getString(R.string.blocked_confirmation)+"You will not be shown any notification from this app. To unblock goto Settings->App Filter->unCheck app");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(toastLayout);
                        toast.show();
                    }
                } else if (blacklist.add(packageName))
                {
                    //Toast.makeText(getApplicationContext(), getText(R.string.blocked_confirmation), Toast.LENGTH_SHORT).show();

                    tv.setText( getString(R.string.blocked_confirmation)+"You will not be shown any notification from this app. To unblock goto Settings->App Filter->unCheck app");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();
                }

                Mlog.v(logTag, blacklist);
                final String serialized = ObjectSerializer.serialize((Serializable) blacklist);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("blacklist", serialized);
                editor.apply();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            //doFinish(1,v);
            return true;
        }
    };

    private void doFinish(final int doDismiss, View view) { // 0=ikke fjern 1=fjern 2=åpnet



        /*
        Mlog.v(logTag + "DoFinish", doDismiss);




        if (chatHeads.isEmpty())
            return;

        if (view!=null)
        {

            //View temp = view.getRootView();
            TextView index = (TextView)view.getRootView().findViewById(R.id.index);
            main_view = chatHeads.get(Integer.parseInt(index.getText().toString()));
            if (index==null)
            Mlog.v(logTag + " DoFinish Index ");


        }




       //check
        // create a handler array
      //  handler.removeCallbacks(closeTimer);

        // Integrate with Voiceify
        if (doDismiss == 1 || doDismiss == 2) {
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent("Screenfly.notificationspeaker.STOP_READING");
            intent.putExtra("packageName", packageName);
            intent.putExtra("tag", tag);
            intent.putExtra("id", id);
            intent.setPackage("Screenfly.notificationspeaker");
            try {
                ResolveInfo resolveInfo= packageManager.resolveService(intent, 0);
                if (resolveInfo.serviceInfo != null) {
                    Mlog.d(logTag, "Voiceify found and resolved");
                    startService(intent);
                }
            } catch (NullPointerException ignored) {} // Don't panic! We'll survive without Voiceify
        }

        if (Build.VERSION.SDK_INT >= 12) {
            try {


                View self = main_view.findViewById(R.id.notificationbg);
                ViewPropertyAnimator animator = self.animate()
                        .setDuration(300)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                               // view.setVisibility(View.GONE);
                                if (doDismiss == 1) {

                                    layout.removeView(main_view);
                                    chatHeads.remove(main_view);
                                    doDismiss(true);

                                }
                                else if (doDismiss == 2) {
                                    chatHeads = null;
                                    main_view.setVisibility(View.GONE);
                                    doDismiss(false);
                                }
                                else {
                                    if (wLock != null && wLock.isHeld())
                                        wLock.release();

                                    stopSelf();
                                }
                            }
                        });
                if (doDismiss == 1) animator.translationX(-400);
                else if (doDismiss == 0)
                    switch (position) {
                        case 2:
                        case 1:
                            animator.translationY(-300);
                            break;
                        case -1:
                            animator.translationY(300);
                            break;
                    }
            } catch (Exception e) {
                reportError(e, "", getApplicationContext());
                e.printStackTrace();
                main_view.setVisibility(View.GONE);
                if (doDismiss == 1) {

                    layout.removeView(main_view);
                    chatHeads.remove(main_view);
                    doDismiss(true);

                }
                else if (doDismiss == 2) {
                    chatHeads = null;
                    main_view.setVisibility(View.GONE);
                    doDismiss(false);
                }
                else {
                    if (wLock != null && wLock.isHeld())
                        wLock.release();
                    stopSelf();
                }
            }
        } else {
            main_view.setVisibility(View.GONE);
            if (doDismiss == 1) {

                layout.removeView(main_view);
                chatHeads.remove(main_view);
                doDismiss(true);

            }
            else if (doDismiss == 2) {
                chatHeads = null;
                main_view.setVisibility(View.GONE);
                doDismiss(false);
            }
            else {
                if (wLock != null && wLock.isHeld())
                    wLock.release();
                stopSelf();
            }
        }
        prevPackageName = "0";



        */
    }

    void doDismiss(boolean stopNow) {
        if (stopNow) {
            if (wLock != null && wLock.isHeld())
                wLock.release();
            //stopSelf();
        } else {
            // main_view.setVisibility(View.GONE);
            /*Mlog.v(logTag, "delayStop");
            isDelaying = true;
           //here handler.postDelayed(delayStop, 10000);*/
            //stopSelf();
        }

    }

    /*private final Runnable delayStop = new Runnable() {
        @Override
        public void run() {
            if (wLock != null && wLock.isHeld())
                wLock.release();
            stopSelf();
            isDelaying = false;
        }
    };*/

    @Override
    public void onDestroy () {
        super.onDestroy();
        Mlog.d(logTag, "Destroy");

        // if (isViewAdded)
        // windowManager.removeViewImmediate(view);
        //  if (layout.getChildCount()==0)


        {



            for (int i=0;i<handler.size();i++)
            {


                if (handler.get(i)!=null)
                {
                    handler.get(i).removeCallbacks(closeTimer);

                }

            }

            handler = null;

            // handler.removeCallbacks(closeTimer);


            if (mainlayout.getWindowToken()!=null){


                windowManager.removeView(mainlayout);

            }

            mSpannableString_l = null;
            actionCount = null;

            //addition

            notificationWear_list = null;

            pendingIntentList = null;
            pendingIntentListb = null;

            if (sensorManager != null)
                sensorManager.unregisterListener(this, sensor);
            if (wLock != null && wLock.isHeld())
                wLock.release();

            // themeClass.destroy(view);

            System.gc();



            if (mIsCountDownOn){
                mNoSpeechCountDown.cancel();
            }
            if (mSpeechRecognizer != null){
                mSpeechRecognizer.destroy();
            }
            Log.d(TAG, "onDestroy (service)");

        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            isProximityClose = ( event.values[0] < sensor.getMaximumRange() );


            if (isProximityClose) {

                Mlog.v(logTag + "ProSensors near");
                screenOff();

            }else
            {

                screenOn();

                Mlog.v(logTag + "ProSensors far");

            }
        }





/*
        if(event.values[0] == 0){
            Mlog.v(logTag + "ProSensors near");

        }
        else {
            Mlog.v(logTag + "ProSensors far");

        }


        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -sensor.getMaximumRange() && event.values[0] <= sensor.getMaximumRange()) {

                //near
               // Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();

                screenOff();


                Mlog.v(logTag + "ProSensors near");

            } else {
                //far

                screenOn();


                Mlog.v(logTag + "ProSensors far");

                // Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
            }
        }
*/

/*

       // Mlog.v(logTag + "Sensor", String.valueOf(event.values[0]));
        isProximityClose = ( event.values[0] != sensor.getMaximumRange() );



        if (isProximityClose){

            Mlog.v(logTag + "ProSensors Near");

          //  screenOff();
        }
        else   {
            Mlog.v(logTag + "ProSensors Far");

            //screenOn();
        }
*/

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Mlog.v(logTag + "SensorAccuracy", String.valueOf(accuracy));
    }

    void createWLock() {
        initPowerManager();

        wLock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "Screenfly");
    }

    private void initPowerManager() {
        if (powerManager == null)
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    }

    void screenOn() {
        if (wLock == null) {
            createWLock();
        }
        if (!wLock.isHeld()) {
            Mlog.v(logTag, "wLock not held "+preferences.getBoolean("keep_screen_on_forever", false));
            if (preferences.getBoolean("keep_screen_on_forever", false)) {
                Mlog.v(logTag, "wLock forever");
                wLock.acquire();

            }
            else {

                wLock.acquire(displayTime);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after displayTime'ms
                        Mlog.v(logTag, "wLock for " + displayTime);
                        screenOff();
                    }
                }, displayTime);



            }
        }
    }

    void screenOff() {
        if (wLock != null && wLock.isHeld()) wLock.release();

        initPowerManager();
        if (powerManager.isScreenOn()) {
            if (policyManager == null)
                policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
            if (policyManager.isAdminActive(AdminReceiver.getComponentName(getApplicationContext()))) {
                Mlog.v(logTag, "ADMIN_ACTIVE");
                policyManager.lockNow();
            } else Mlog.v(logTag, "ADMIN_NOT_ACTIVE");
        }
    }

    /**
     * Acquire a WakeLock which ensures the screen is on and then pokes the user activity timer.
     */
    void pokeScreenTimer() {
        initPowerManager();
        powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "pokeScreenTimer").acquire(1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    static void reportError(Exception e, String msg, Context c) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            if (e != null) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                msg = msg.concat(writer.toString());
                editor.putString("lastException", ObjectSerializer.serialize(e));
            }
            editor.putString("lastBug", msg);
            editor.apply();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

}
