package com.example.belen.shrimps;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class MySpinnerListener implements AdapterView.OnItemSelectedListener {
    String color;
    String[] palette = new String[]{
            "#ffdb99",
            "#ffab91",
            "#ffa500",
            "#ff7400",
            "#ff0000"
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(position);
        this.color = palette[position];
        PhotoActivity.myCanvasView.setColor(this.color);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
