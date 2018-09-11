package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ListImages extends Activity {


    static LinearLayout linlaHeaderProgress;
    static ProgressBar myProgressBar;
    public static FTPClient ftp;
    static ArrayList<Thumbnail> thumbnails;
    static ArrayAdapter<Thumbnail> itemsAdapter;
    ListView listView;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            thumbnails= new ArrayList<>();
            setContentView(R.layout.activity_list_images);
            this.listView = (ListView) findViewById(R.id.customListView);
            linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
            myProgressBar = findViewById(R.id.pBar);
            this.itemsAdapter = new ThumbnailAdapter(this,0,thumbnails,2);
            this.listView.setAdapter(itemsAdapter);
            fillDownloadedImages(this);



        }


    static ArrayList<String> fetchImages(File directory) {
        //Function that fecthes images of a folder
        ArrayList<String> filenames = new ArrayList<>();
        System.out.println("DIRECTORIO:"+directory.getAbsolutePath());
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++){

            String file_name = files[i].getName();
            if (file_name.contains(".jpg")){
                filenames.add(file_name);}
            // you can store name to arraylist and use it later

        }
        return filenames;
    }


    @SuppressLint("StaticFieldLeak")
    static void fillDownloadedImages(final Context context) {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                int color = Color.parseColor("#007DD6");
                myProgressBar.getIndeterminateDrawable()
                        .setColorFilter(color, PorterDuff.Mode.SRC_IN);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }

            protected Void doInBackground(Void... params) {
                File directory  = context.getFilesDir();
                ArrayList<String> files = fetchImages(directory);
                for (int i = 0; i < files.size(); i++) {
                    Thumbnail thumbnail = new Thumbnail(files.get(i));
                    thumbnails.add(thumbnail);
                    System.out.println("FILENAME: " + thumbnail.getName());

                }
                return null;
            }

            protected void onPostExecute(Void result) {
                itemsAdapter.notifyDataSetChanged();
                // HIDE THE SPINNER AFTER LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();
    }




}
