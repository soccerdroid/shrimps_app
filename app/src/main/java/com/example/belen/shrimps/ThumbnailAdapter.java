package com.example.belen.shrimps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailAdapter extends ArrayAdapter<Thumbnail>{
    private Context context;
    private List<Thumbnail> thumbnails;

    public ThumbnailAdapter(@NonNull Context context, int resource, ArrayList<Thumbnail> thumbnails) {
        super(context, resource, thumbnails);
        this.context = context;
        this.thumbnails = thumbnails;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        final Thumbnail thumbnail = thumbnails.get(position);
        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.thumbnails, null);
        //System.out.println("TO STRING: " + thumbnail.getName().toString());
        TextView thumbnail_tv = (TextView) view.findViewById(R.id.thumbnail_tv);
        thumbnail_tv.setText(thumbnail.getName());
        //        ImageView image = (ImageView) view.findViewById(R.id.image);

        //get the image associated with this property

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("name", thumbnail.getName());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
