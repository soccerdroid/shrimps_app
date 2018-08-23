package com.example.belen.shrimps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
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
        TextView thumbnail_tv = view.findViewById(R.id.thumbnail_tv);
        thumbnail_tv.setText(thumbnail.getName());
        CheckBox thumbnail_chb = view.findViewById(R.id.thumbnail_chb);

        //        ImageView image = (ImageView) view.findViewById(R.id.image);

        //get the image associated with this property

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("name", thumbnail.getName());
                context.startActivity(intent);
            }
        });*/

        thumbnail_chb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();
                if (checked){
                    thumbnail.setDownloaded(true);
                }
            }
        });
        return view;
    }

    public LinkedList<Thumbnail> downloadThumbnails(){
        //returns a linked list of all checked thumbnails
        //in the thumbnail array adapter
        LinkedList<Thumbnail> downloaded_tb = new LinkedList<>();
        for (Thumbnail thumb: this.thumbnails){
            if (thumb.downloaded){
                downloaded_tb.add(thumb);
            }
        }
        return downloaded_tb;

    }
}
