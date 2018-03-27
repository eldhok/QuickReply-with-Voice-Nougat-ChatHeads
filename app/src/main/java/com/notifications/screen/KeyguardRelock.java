

package com.notifications.screen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.notifications.screen.util.Mlog;
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
public class KeyguardRelock extends Service {

    public static final String logTag = "KeyguardRelock";

    public KeyguardRelock() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // not bindable
    }

    private BroadcastReceiver mReceiver;
    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock keyguardLock = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        super.onStartCommand(intent, flags, startid);

        if (intent != null) {
            final String action = intent.getAction();
            Mlog.d(logTag, action);

            if      (Intent.ACTION_SCREEN_ON.equals(action))  waitForLock();
            else if (Intent.ACTION_SCREEN_OFF.equals(action)) doLock();
            else                                              stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void waitForLock() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        //noinspection deprecation
        keyguardLock = keyguardManager.newKeyguardLock("heads-up");
        try {
            keyguardLock.disableKeyguard();

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            mReceiver = new OffReceiver();
            registerReceiver(mReceiver, intentFilter);
        } catch (SecurityException se) {
            keyguardLock = null;
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit()
                    .putBoolean("dismiss_keyguard", false)
                    .apply();
            stopSelf();
        }
    }

    private void doLock() {
        try {
            unregisterReceiver(mReceiver);
        } catch (RuntimeException rte) {
            Mlog.w(logTag, rte.getMessage());
        }
        if (keyguardLock != null)
            keyguardLock.reenableKeyguard();
        keyguardLock = null;
        stopSelf();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (keyguardLock != null) {
            unregisterReceiver(mReceiver);
            keyguardLock.reenableKeyguard();
            keyguardLock = null;
        }
    }

    class OffReceiver extends BroadcastReceiver {
        public OffReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent i = new Intent(context, KeyguardRelock.class);
                i.setAction(Intent.ACTION_SCREEN_OFF);
                context.startService(i);
            }
        }
    }
}
