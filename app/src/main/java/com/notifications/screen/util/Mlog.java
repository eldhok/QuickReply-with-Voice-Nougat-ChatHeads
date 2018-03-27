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

package com.notifications.screen.util;

import android.util.Log;

import com.notifications.screen.BuildConfig;

/**
 * Log wrapper for easily enabling and disabling debugging.
 */
public class Mlog {
    public static final boolean isLogging = BuildConfig.DEBUG;

    public static void v (Object tag, Object msg) {
        if (isLogging)
            Log.v(String.valueOf(tag), String.valueOf(msg));
    }

    public static void v (Object msg) {
        if (isLogging)
            Log.v("DUMP DATA", String.valueOf(msg));
    }

    public static void d (Object tag, Object msg) {
        if (isLogging)
            Log.d(String.valueOf(tag), String.valueOf(msg));
    }

    public static void i (Object tag, Object msg) {
        if (isLogging)
            Log.i(String.valueOf(tag), String.valueOf(msg));
    }

    public static void w (String tag, String msg) {
        if (isLogging)
            Log.w(tag, msg);
    }

    public static void e (String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void wtf (String tag, String msg) {
        Log.wtf(tag, msg);
    }
}
