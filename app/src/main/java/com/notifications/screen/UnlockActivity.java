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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.notifications.screen.util.Mlog;


public class UnlockActivity extends Activity {
    public static final String logTag = "UnlockActivity";

    private boolean isPendingIntentStarted = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // User present is usually sent after the device has been unlocked
        IntentFilter userUnlock = new IntentFilter (Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockDone, userUnlock);

        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Mlog.v(logTag, "creating dismiss window");

        // In case we don't get an user present broadcast, just move on
        handler.postDelayed(timeoutRunnable, 2000);
    }

    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Mlog.d(logTag, "Timeout runnable run");
            if (!isFinishing()) {
                startPendingIntent();
                finish();
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        Mlog.d(logTag, "Intent received: " + intent);
        setIntent(intent);
    }

    BroadcastReceiver unlockDone = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Double-check we got the right intent
            if (!intent.getAction().equals(Intent.ACTION_USER_PRESENT)) return;

            Mlog.v(logTag, "Unlocked!");

            startPendingIntent();

            finish();
        }
    };

    private void startPendingIntent() {
        Mlog.d(logTag, "Start pending intent requested");
        if (isPendingIntentStarted) return;
        isPendingIntentStarted = true;
        Intent intent;Bundle extras = getIntent().getExtras();
        PendingIntent pendingIntent = (PendingIntent) extras.get("action");

        try {
            intent = new Intent();
            if (extras.getBoolean("floating", false))
                intent.addFlags(OverlayServiceCommon.FLAG_FLOATING_WINDOW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pendingIntent.send(getApplicationContext(), 0, intent);
            Mlog.d(logTag, "Pending intent started successfully");
        } catch (PendingIntent.CanceledException e) {
            OverlayServiceCommon.reportError(e, "App has canceled action", getApplicationContext());
            Toast.makeText(getApplicationContext(), getString(R.string.pendingintent_cancel_exception), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            OverlayServiceCommon.reportError(e, "No action defined", getApplicationContext());
            Toast.makeText(getApplicationContext(), getString(R.string.pendingintent_null_exception), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        startPendingIntent();
        super.onDestroy();
        Mlog.v(logTag, "Destroy");
        unregisterReceiver(unlockDone);
    }
}
