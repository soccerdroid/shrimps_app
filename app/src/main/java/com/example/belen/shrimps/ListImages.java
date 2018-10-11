package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;
import android.app.Activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class ListImages extends Activity {


    static LinearLayout linlaHeaderProgress;
    static ProgressBar myProgressBar;
    static ArrayList<Thumbnail> thumbnails;
    static ArrayAdapter<Thumbnail> itemsAdapter;
    ListView listView;
    Context context;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.context = this;
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
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++){
            String file_name = files[i].getName();
            if (file_name.contains(".jpg")){
                filenames.add(file_name);}
            // you can store name to arraylist and use it later

        }
        return filenames;
    }


    static File createFolder(String albumName) {
        // Get the directory for the user's public pictures directory.
        if(isExternalStorageReadable() && isExternalStorageWritable()){
            //File file = new File(Environment.getExternalStoragePublicDirectory(
                    //Environment.DIRECTORY_PICTURES),"Shrimps-images");
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root +"/"+ albumName);
            if (!myDir.exists()) {
                myDir.mkdirs();
                if (!myDir.mkdirs()) {
                    System.out.println("No se puede crear carpeta "+root);
                    return null;
                }
            }

            System.out.println("Created or existing folder");
            return myDir;
        }
        System.out.println("No se puede acceder al almacenamiento externo");
        return null;
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
                File directory = createFolder("Shrimps-images");
                if(directory!=null){
                    ArrayList<String> files = fetchImages(directory);
                    for (int i = 0; i < files.size(); i++) {
                        Thumbnail thumbnail = new Thumbnail(files.get(i));
                        thumbnails.add(thumbnail);
                        System.out.println("FILENAME: " + thumbnail.getName());

                    }
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

    /* Checks if external storage is available for read and write */
    static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    @Override
    public void onResume(){
        super.onResume();
        //fillDownloadedImages(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fillDownloadedImages(this.context);
    }
}
