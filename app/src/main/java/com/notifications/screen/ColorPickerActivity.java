package com.notifications.screen;

/**
 * Created by eldho on 7/11/17.
 */

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

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ColorPickerActivity extends Activity {
    GridView gridView;
    ImageView imageView;
    SeekBar colorSeekBar;
    TextView redTextView;
    TextView greenTextView;
    TextView blueTextView;
    Button showColor ;
    private ArrayList<ColorPicker> colorPickerArray;
    private ColorPicker colorPickerObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker);
        gridView = (GridView)findViewById(R.id.color_grid);
        imageView = (ImageView)findViewById(R.id.selected_color);
        colorSeekBar = (SeekBar)findViewById(R.id.color_seeker);
        redTextView = (TextView)findViewById(R.id.selected_r_value);
        greenTextView = (TextView)findViewById(R.id.selected_g_value) ;
        blueTextView = (TextView)findViewById(R.id.selected_b_value);
        showColor = (Button)findViewById(R.id.reset_color);
        intializeColorArray();
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(colorPickerArray);
        gridView.setAdapter(colorPickerAdapter);
        showColor.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(colorPickerObject != null)
                {
                    Toast.makeText(getApplicationContext(),colorPickerObject.toString(),Toast.LENGTH_LONG).show()
                    ;
                }
            }
        });
        colorSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean
                    fromUser)
            {
                int alpha = 254 - progress;
                int r = colorPickerObject.getRed();
                int g = colorPickerObject.getGreen();
                int b = colorPickerObject.getBlue();
                colorPickerObject.setAlpha(alpha);
                imageView.setBackgroundColor(Color.argb(alpha, r, g, b));
            }
        });
    }
    private class ColorPickerAdapter extends BaseAdapter
    {
        ArrayList<ColorPicker> colorAdapterArray;
        public ColorPickerAdapter(ArrayList<ColorPicker> colorPickerArray)
        {
            super();
            this.colorAdapterArray = colorPickerArray;
        }
        @Override
        public int getCount() {
            return colorAdapterArray.size();
        }
        @Override
        public Object getItem(int arg0)
        {
            return colorAdapterArray.get(arg0);
        }
        @Override
        public long getItemId(int arg0) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView  ;
            ImageButton imageView = null;
            if (convertView == null){
                gridView = inflater.inflate(R.layout.grid_component,null);
                imageView = (ImageButton)gridView.findViewById(R.id.color_image);
            }
            else
            {
                gridView = (View)convertView;
            }
            final ColorPicker colorPicker = colorAdapterArray.get(position);
            int r = colorPicker.getRed();
            int g = colorPicker.getGreen();
            int b = colorPicker.getBlue();
            int alpha = colorPicker.getAlpha();
            if(imageView != null){
                imageView.setBackgroundColor(Color.argb(alpha, r, g, b));
            }
            else
            {
//do nothing
            }
            if(imageView != null)
            {
                imageView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        handleColorClick(colorPicker);
                    }
                });
            }
            return gridView;
        }
    }
    private void intializeColorArray()
    {
        colorPickerArray = new ArrayList<ColorPicker>();

    /*1*/
        colorPickerArray.add(new ColorPicker(255,0,0,255));    colorPickerArray.add(new
            ColorPicker(255,170,0,255));  colorPickerArray.add(new ColorPicker(170,255,0,255));
        colorPickerArray.add(new ColorPicker(0,255,0,255));    colorPickerArray.add(new
            ColorPicker(0,255,170,255));  colorPickerArray.add(new ColorPicker(0,170,255,255));
        colorPickerArray.add(new ColorPicker(0,0,255,255));    colorPickerArray.add(new
            ColorPicker(170,0,255,255));  colorPickerArray.add(new ColorPicker(255,0,170,255));
    /*2*/
        colorPickerArray.add(new ColorPicker(255,36,36,255));  colorPickerArray.add(new
            ColorPicker(255,182,36,255)); colorPickerArray.add(new ColorPicker(182,255,36,255));
        colorPickerArray.add(new ColorPicker(36,255,36,255));  colorPickerArray.add(new
            ColorPicker(36,255,182,255)); colorPickerArray.add(new ColorPicker(36,182,255,255));
        colorPickerArray.add(new ColorPicker(36,36,255,255));  colorPickerArray.add(new
            ColorPicker(182,36,255,255)); colorPickerArray.add(new ColorPicker(255,36,182,255));
    /*3*/
        colorPickerArray.add(new ColorPicker(255,73,73,255));  colorPickerArray.add(new
            ColorPicker(255,194,73,255)); colorPickerArray.add(new ColorPicker(194,255,73,255));
        colorPickerArray.add(new ColorPicker(73,255,73,255));  colorPickerArray.add(new
            ColorPicker(73,255,194,255)); colorPickerArray.add(new ColorPicker(73,194,255,255));
        colorPickerArray.add(new ColorPicker(73,73,255,255));  colorPickerArray.add(new
            ColorPicker(194,73, 255,255));colorPickerArray.add(new ColorPicker(255,73,194,255));
    /*4*/
        colorPickerArray.add(new ColorPicker(255,109,109,255));colorPickerArray.add(new
            ColorPicker(255,206,109,255));colorPickerArray.add(new
            ColorPicker(206,255,109,255));colorPickerArray.add(new
            ColorPicker(109,255,109,255));colorPickerArray.add(new
            ColorPicker(109,255,206,255));colorPickerArray.add(new
            ColorPicker(109,206,255,255));colorPickerArray.add(new
            ColorPicker(109,109,255,255));colorPickerArray.add(new
            ColorPicker(206,109,255,255));colorPickerArray.add(new ColorPicker(255,109,206,255));
    /*5*/
        colorPickerArray.add(new ColorPicker(255,146,146,255));colorPickerArray.add(new
            ColorPicker(255,219,146,255));colorPickerArray.add(new
            ColorPicker(219,255,146,255));colorPickerArray.add(new
            ColorPicker(146,255,146,255));colorPickerArray.add(new
            ColorPicker(146,255,219,255));colorPickerArray.add(new
            ColorPicker(146,219,255,255));colorPickerArray.add(new
            ColorPicker(146,146,255,255));colorPickerArray.add(new
            ColorPicker(219,146,255,255));colorPickerArray.add(new ColorPicker(255,146,219,255));
    /*6*/
        colorPickerArray.add(new ColorPicker(255,182,182,255));colorPickerArray.add(new
            ColorPicker(255,231,182,255));colorPickerArray.add(new
            ColorPicker(231,255,182,255));colorPickerArray.add(new
            ColorPicker(182,255,182,255));colorPickerArray.add(new
            ColorPicker(182,255,231,255));colorPickerArray.add(new
            ColorPicker(182,231,255,255));colorPickerArray.add(new
            ColorPicker(182,182,255,255));colorPickerArray.add(new
            ColorPicker(231,182,255,255));colorPickerArray.add(new ColorPicker(255,182,231,255));
    /*7*/
        colorPickerArray.add(new ColorPicker(255,219,219,255));colorPickerArray.add(new
            ColorPicker(255,243,219,255));colorPickerArray.add(new
            ColorPicker(243,255,219,255));colorPickerArray.add(new
            ColorPicker(219,255,219,255));colorPickerArray.add(new
            ColorPicker(219,255,243,255));colorPickerArray.add(new
            ColorPicker(219,243,255,255));colorPickerArray.add(new
            ColorPicker(219,219,255,255));colorPickerArray.add(new
            ColorPicker(243,219,255,255));colorPickerArray.add(new ColorPicker(255,219,243,255));
        handleColorClick(colorPickerArray.get(0));
    }
    private void handleColorClick(ColorPicker colorPicker)
    {
        colorSeekBar.setProgress(0);
        this.colorPickerObject = colorPicker;
        int r = colorPicker.getRed();
        int g = colorPicker.getGreen();
        int b = colorPicker.getBlue();
        int alpha = colorPicker.getAlpha();
        imageView.setBackgroundColor(Color.argb(alpha, r, g, b));
        setColoronText(r, g, b);
    }
    private void setColoronText(int r , int g , int b)
    {
        redTextView.setText("R:"+String.valueOf(r));
        greenTextView.setText("R:"+String.valueOf(g));
        blueTextView.setText("R:"+String.valueOf(b));
    }
}