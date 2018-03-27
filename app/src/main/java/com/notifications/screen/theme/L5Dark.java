

package com.notifications.screen.theme;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifications.screen.R;

import static com.notifications.screen.OverlayServiceCommon.preferences;

/**
 * Android L theme
 */
public class L5Dark extends ThemeClass {

    public L5Dark(ViewStub stub) {
        super(stub);
        stub.setLayoutResource(R.layout.activity_read_inner_dark);
    }

    @Override
    public void setIcon(ImageView imageView, Bitmap bitmap, boolean round_icons, int color) {
        if (round_icons) imageView.setBackgroundResource(R.drawable.circle);
        super.setIcon(imageView, bitmap, round_icons, color);
    }

    @Override
    public void addActionButton(ViewGroup actionButtons,int indexb, String actionTitle, Drawable icon, View.OnClickListener clickListener, float fontMultiplier) {
        LayoutInflater inflater = LayoutInflater.from(actionButtons.getContext());


        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.button_notification, actionButtons);


        LinearLayout blayout = (LinearLayout)v.getChildAt(v.getChildCount() - 1).findViewById(R.id.blayout);

        TextView bindex = (TextView) blayout.findViewById(R.id.bindex);
        bindex.setText(Integer.toString(indexb));

        Button button = (Button) blayout.findViewById(R.id.button);
        button.setText(actionTitle);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontMultiplier * button.getTextSize());
       // button.setTextColor(Color.WHITE);
        if (preferences.getBoolean("pro_color_change", false)){

            button.setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
            if (icon != null) {
                //  if (!actionTitle.equals("GET PRO")) {
                icon.mutate().setColorFilter(getColorFilter(preferences.getInt("pro_color_text",Color.WHITE)));
          /*
            }
            else
            {

                AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                blinkanimation.setDuration(600); // duration - half a second
                blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                blinkanimation.setRepeatMode(Animation.REVERSE);

                button.setAnimation(blinkanimation);



            }
            */
                button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            }
        }

        else {



            button.setTextColor(Color.WHITE);

            if (icon != null) {
                //  if (!actionTitle.equals("GET PRO")) {
                icon.mutate().setColorFilter(getColorFilter(Color.WHITE));
          /*
            }
            else
            {

                AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                blinkanimation.setDuration(600); // duration - half a second
                blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                blinkanimation.setRepeatMode(Animation.REVERSE);

                button.setAnimation(blinkanimation);



            }
            */
                button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            }
        }



        blayout.setOnClickListener(clickListener);
    }


}
