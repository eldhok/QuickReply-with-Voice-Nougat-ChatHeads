
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
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import com.notifications.screen.R;
import com.notifications.screen.util.Mlog;
import com.notifications.screen.util.RoundDrawable;

import static com.notifications.screen.OverlayServiceCommon.preferences;

/**
 * Extend this class and override any methods you need to modify.
 */
public class ThemeClass {

    public ThemeClass (ViewStub stub) {
        stub.setLayoutResource(R.layout.activity_read_inner);
    }

    public ThemeClass () {

    }

    /**
     * Called right after the theme has been inflated
     * @param layout The root layout
     */
    public void init (View layout) {

    }

    /**
     * Fetch the root view of the theme
     * @param layout The root layout
     */
    public ViewGroup getRootView (View layout) {
        return (ViewGroup) layout.findViewById(R.id.linearLayout);
    }

    /**
     * Show the time the notification arrived
     * @param layout The root layout
     * @param time Date object containing the time the notification arrived
     */
    public void showTime(View layout, Date time) {
        final TextView timeView = (TextView) layout.findViewById(R.id.timeView);
        timeView.setText(
                DateFormat.getTimeFormat(layout.getContext().getApplicationContext()).format(time)
        );
        timeView.setVisibility(View.VISIBLE);
    }

    /**
     * The the time view
     * @param layout The root layout
     */
    public void hideTime(View layout) {
        layout.findViewById(R.id.timeView).setVisibility(View.GONE);
    }

    /**
     * Fetch a reference to the action button area
     * @param layout The root layout
     */
    public ViewGroup getActionButtons(View layout) {
        return (ViewGroup) layout.findViewById(R.id.action_buttons);
    }

    /**
    * Remove all action buttons from the layout, in case the layout needs to be re-used.
    */
    public void removeActionButtons (ViewGroup actionButtonViewGroup) {
        while (actionButtonViewGroup.getChildCount() > 0) {
            actionButtonViewGroup.removeViewAt(0);
        }
    }

    /**
     * This notification does have action buttons. Display the action button area.
     * @param layout The root layout
     * @param count The number of action buttons.
     *              If count parameter is -1, only display the action button view
     */
    public void showActionButtons(View layout, int count) {
        layout.findViewById(R.id.button_container).setVisibility(View.VISIBLE);
    }

    /**
     * This notification doesn't have any action buttons. Hide the action button area.
     * @param layout The root layout
     */
    public void hideActionButtons(View layout) {
        layout.findViewById(R.id.button_container).setVisibility(View.GONE);
    }

    /**
     Add an action button to the layout.
     */
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

        //button.setTextColor(Color.parseColor("#1976D2"));
        //button.setTextColor(Color.BLACK);

        if (preferences.getBoolean("pro_color_change", false))  {
            button.setTextColor(preferences.getInt("pro_color_text",Color.BLACK));
            if (icon != null) {
                //  if (!actionTitle.equals("GET PRO")) {
                icon.mutate().setColorFilter(getColorFilter(preferences.getInt("pro_color_text",Color.BLACK)));
                // icon.mutate().setColorFilter(getColorFilter(Color.parseColor("#1976D2")));
                //  }

            /*
            else
            {

                button.setTextColor(Color.parseColor("#1976D2"));


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
        else
        {
            button.setTextColor(Color.BLACK);
            if (icon != null) {
                //  if (!actionTitle.equals("GET PRO")) {
                icon.mutate().setColorFilter(getColorFilter(Color.BLACK));
                // icon.mutate().setColorFilter(getColorFilter(Color.parseColor("#1976D2")));
                //  }

            /*
            else
            {

                button.setTextColor(Color.parseColor("#1976D2"));


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

    /**
     Return the view displaying the notification icon.
     */
    public ImageView getIconView(View layout) {
        return (ImageView) layout.findViewById(R.id.notification_icon);
    }

    /**
     Return the view displaying the small notification icon.
     Should return null if the theme doesn't use small icons.
     */
    public ImageView getSmallIconView(View layout) {
        return (ImageView) layout.findViewById(R.id.notification_icon_small);
    }

    /**
     Set the notification icon from a bitmap.
     */
    public void setIcon(ImageView imageView, Bitmap bitmap, boolean round_icons, int color) {
        if (bitmap == null) return;
        if (round_icons) {
            final double minimumWidthForRoundIcon = imageView.getContext().getResources().
                    getDimension(R.dimen.notification_ic_size) / (2 * Math.cos(Math.toRadians(45)));
            int bitmapWidth = bitmap.getWidth();
            Mlog.v(bitmapWidth, minimumWidthForRoundIcon);

            if (bitmapWidth >= minimumWidthForRoundIcon) {
                try {
                    RoundDrawable roundedDrawable = new RoundDrawable(bitmap);
                    imageView.setImageDrawable(roundedDrawable);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                imageView.setImageBitmap(bitmap);
            }
            imageView.setBackgroundResource(R.drawable.circle_grey);
            setColor(imageView, color);
        } else {
            imageView.setImageBitmap(bitmap);
            setColor(imageView, color);
        }
    }

    /**
     Set the small notification icon.
     */
    public void setSmallIcon(ImageView smallIcon, Drawable drawable, int color) {
        if (drawable != null) {
            smallIcon.setImageDrawable(drawable);
            setColor(smallIcon, color);
        } else {
            smallIcon.setVisibility(View.GONE);
        }
    }

    protected static void setColor(View view, int color) {
        if (color == 0) return;
        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable = drawable.mutate();
            //drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            drawable.setColorFilter(getColorFilter(color));
            if (Build.VERSION.SDK_INT >= 16) view.setBackground(drawable);
            else                             view.setBackgroundDrawable(drawable);
        } else {
            view.setBackgroundColor(color);
        }
    }

    /**
     Fetch the dismiss button.
     */
    public View getDismissButton(View layout) {
        return layout.findViewById(R.id.notification_dismiss);
    }


    /**
     Hide the dismiss button.
     */
    public void hideDismissButton(View dismissButton) {
        dismissButton.setVisibility(View.GONE);
    }

    /**
     In case you need to do something when stopping. Called after the view is removed from the window manager.
     */
    public void destroy(View layout) {
    }

    /**
     * Get a color filter for recoloring any solid drawable.
     * From http://stackoverflow.com/a/11171509
     * @param color The color
     * @return A ColorMatrixColorFilter
     */
    protected static ColorFilter getColorFilter(int color) {
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red
                         , 0, 0, 0, 0, green
                         , 0, 0, 0, 0, blue
                         , 0, 0, 0, 1, 0 };

        return new ColorMatrixColorFilter(matrix);
    }
}