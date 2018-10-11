package com.example.belen.shrimps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.belen.shrimps.PhotoActivity;
import com.example.belen.shrimps.R;

import java.util.ArrayList;
import java.util.List;

public class ColorSpinnerAdapter extends ArrayAdapter<ColorSpinnerElement> {



    public ColorSpinnerAdapter(@NonNull Context context, ArrayList<ColorSpinnerElement> colors) {
        super(context,0,colors);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.color_item,parent,false);
        }

        TextView color_tv = (TextView) convertView.findViewById(R.id.color_tv);
        ImageView color_image = (ImageView)convertView.findViewById(R.id.color_iv);
        ColorSpinnerElement element = getItem(position);
        if(element!=null){
            color_tv.setText(element.getClass_name());
            //Drawable[] drawables = color_tv.getCompoundDrawables();
            System.out.println(element.getImage_color());
            color_image.setColorFilter(Color.parseColor(element.getImage_color()));
            //drawables[0].setColorFilter( Color.parseColor(element.getImage_color()), PorterDuff.Mode.SRC_ATOP);

        }
        return convertView;
    }
}
