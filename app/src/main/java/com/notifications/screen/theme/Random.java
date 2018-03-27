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

package com.notifications.screen.theme;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifications.screen.R;

import static com.notifications.screen.OverlayServiceCommon.color_md;

/**
 * A random, psychedelic theme. I don't know why anyone would use this, but it's here.
 * Consider it a weird easter egg.
 */
public class Random extends HoloDark {

    static int red,green,blue;
    private ThemeClass subTheme;
    static double lum;


    public Random(ViewStub stub) {
        super(stub);
        stub.setLayoutResource(R.layout.activity_read_inner_holo);
    }

    @Override
    public void init(View layout) {
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.contentContainer);


        /*
        final int red = randomColorValue();
        final int green = randomColorValue();
        final int blue = randomColorValue();
        final double brightness = (0.299*red + 0.587*green + 0.114*blue);

        Mlog.v(red+blue+green, brightness);
        linearLayout.setBackgroundColor(Color.rgb(red, green, blue));

        if (brightness > 130) {
            subTheme = new HoloLight();
            ((TextView)linearLayout.findViewById(R.id.notification_title)).setTextColor(Color.BLACK);
            ((TextView)linearLayout.findViewById(R.id.notification_subtitle)).setTextColor(Color.BLACK);
        }
        else {
            subTheme = new HoloDark();
            ((TextView)linearLayout.findViewById(R.id.notification_title)).setTextColor(Color.WHITE);
            ((TextView)linearLayout.findViewById(R.id.notification_subtitle)).setTextColor(Color.WHITE);
        }

        */







// create your own material color palette
        linearLayout.setBackgroundColor(color_md);
        subTheme = new HoloDark();



         red = Color.red(color_md);
         green = Color.green(color_md);
         blue = Color.blue(color_md);
         lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));

       ((TextView)linearLayout.findViewById(R.id.notification_title)).setTextColor(lum > 186 ? Color.BLACK : Color.WHITE);
       // ((TextView)linearLayout.findViewById(R.id.notification_title)).setTextColor(Color.WHITE);
        ((TextView)linearLayout.findViewById(R.id.notification_subtitle)).setTextColor(lum > 186 ? Color.BLACK : Color.WHITE);
        ((TextView)linearLayout.findViewById(R.id.timeView)).setTextColor(lum > 186 ? Color.BLACK : Color.WHITE);
        //((TextView)linearLayout.findViewById(R.id.notification_subtitle)).setTextColor(Color.WHITE);



    }

    private int randomColorValue() {
        return (int) (Math.random() * 200);
    }

    @Override
    public void addActionButton(ViewGroup actionButtons,int indexb, String actionTitle, Drawable icon, View.OnClickListener clickListener, float fontMultiplier) {
        subTheme.addActionButton(actionButtons,indexb, actionTitle, icon, clickListener, fontMultiplier);
    }



}
