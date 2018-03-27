
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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;

import com.notifications.screen.util.Mlog;

/*
 * Created as a separate class because some devices running 4.2 and earlier failed to
 * load classes containing references to NotificationListener
 */
public class OverlayService extends OverlayServiceCommon {
    private int onBindAction = 0; // 0=nothing, 1=remove, 2=check existence

    private boolean serviceBound = false;

    @Override
    public void doDismiss (boolean mStopNow) {
        Mlog.d(logTag, packageName + tag + id);

        onBindAction = 1;


        Intent intent = new Intent(this, NotificationListenerService.class);
        intent.setAction(NotificationListenerService.ACTION_CUSTOM);
        serviceBound = bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {


            NotificationListenerService.LocalBinder binder = (NotificationListenerService.LocalBinder) service;
            NotificationListenerService listenerService = binder.getService();
            Mlog.d(logTag, "serviceConnected");

            switch (onBindAction) {
                case 1:
                    if (Build.VERSION.SDK_INT >= 20) listenerService.doRemove(key);
                    else                             listenerService.doRemove(packageName, tag, id);
                    stopSelf();
                    break;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Ensure we unbind the service properly
        if (serviceBound)
            unbindService(mConnection);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
