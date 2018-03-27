package com.notifications.screen.util;
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
import android.app.PendingIntent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import java.util.ArrayList;


public class NotificationWear {

    public ArrayList<PendingIntent> pendingIntent = new ArrayList<>();
    public ArrayList<RemoteInput> remoteInputs = new ArrayList<>();
    public Bundle bundle;
    public Integer action_size;

}

