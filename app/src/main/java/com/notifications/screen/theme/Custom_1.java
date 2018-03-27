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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifications.screen.OverlayServiceCommon;
import com.notifications.screen.R;
import com.notifications.screen.util.Mlog;

import static android.R.attr.text;
import static com.notifications.screen.OverlayServiceCommon.preferences;
import static com.notifications.screen.UnlockActivity.logTag;


/**
 * Custom_1 phone theme
 */
public class Custom_1 extends ThemeClass {

    public Custom_1(ViewStub stub) {
        super(stub);
        stub.setLayoutResource(R.layout.chat_receive_msg_item);
    }

    @Override
    public void setIcon(ImageView imageView, Bitmap bitmap, boolean round_icons, int color) {
        super.setIcon(imageView, bitmap, true, 0);
    }

    @Override
    public void setSmallIcon(ImageView smallIcon, Drawable drawable, int color) {
        super.setSmallIcon(smallIcon, drawable, 0);
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



      //  button.setBackgroundColor(button.getContext().getResources().getColor(R.color.ChatGreen));


        /*
        button.setBackgroundResource(R.drawable.buttonbackground);


        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);



*/

     //   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(button.getLayoutParams());

        /*

        if (OverlayServiceCommon.size==1)
        {
           // button.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        else if (OverlayServiceCommon.size==2)
        {
            if (OverlayServiceCommon.i==1)
            {
               // button.setGravity(Gravity.LEFT);


            }
            else
            {
               // button.setGravity(Gravity.RIGHT);

            }

        }
        else if (OverlayServiceCommon.size==3)
        {
            if (OverlayServiceCommon.i==1)
            {
              //  button.setGravity(Gravity.LEFT);

            }
            else if (OverlayServiceCommon.i==2)
            {
               // button.setGravity(Gravity.CENTER_HORIZONTAL);

            }
            else
            {
               // button.setGravity(Gravity.RIGHT);

            }
        }

*/
/*
       if (OverlayServiceCommon.size==2 && OverlayServiceCommon.i==2)
        {


                params.setMargins(0,0,5,0);

        }
        else if (OverlayServiceCommon.size==3 && (OverlayServiceCommon.i==3 || OverlayServiceCommon.i==2))
        {
            params.setMargins(0,0,5,0);

        }


        button.setLayoutParams(params);


*/



            button.setBackgroundResource(R.drawable.buttonbackground);

        if (OverlayServiceCommon.preferences.getBoolean("pro_color_change", false))
        ((GradientDrawable)button.getBackground()).setColor(OverlayServiceCommon.preferences.getInt("pro_color",Color.WHITE));

     //   button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontMultiplier * button.getTextSize());


        if (preferences.getBoolean("pro_color_change", false)) {

            Mlog.d(logTag, " pro_color_change true ");

            button.setTextColor(preferences.getInt("pro_color_text",Color.WHITE));
            if (icon != null) {
                //  if (!actionTitle.equals("GET PRO")) {
                icon.mutate().setColorFilter(getColorFilter(preferences.getInt("pro_color_text",Color.WHITE)));
                //}
            /*
            else
            {

                ObjectAnimator rotate = ObjectAnimator.ofFloat(button, "rotation", 0f, 4f, 0f, -4f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.
// animateView (View object)
                rotate.setRepeatCount(Animation.INFINITE); // repeat the loop 20 times
                rotate.setDuration(800); // animation play time 100 ms
                rotate.start();




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
                //}
            /*
            else
            {

                ObjectAnimator rotate = ObjectAnimator.ofFloat(button, "rotation", 0f, 4f, 0f, -4f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.
// animateView (View object)
                rotate.setRepeatCount(Animation.INFINITE); // repeat the loop 20 times
                rotate.setDuration(800); // animation play time 100 ms
                rotate.start();




            }
            */
                button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

            }



        }



        blayout.setOnClickListener(clickListener);
    }

}
