package com.example.belen.shrimps;

import android.view.View;
import android.widget.AdapterView;


public class MySpinnerListener implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ColorSpinnerElement element = (ColorSpinnerElement) parent.getItemAtPosition(position);
        PhotoActivity.myCanvasView.setColor(element.getImage_color());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
