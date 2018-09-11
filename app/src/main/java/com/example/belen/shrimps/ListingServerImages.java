package com.example.belen.shrimps;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;

public class ListingServerImages extends AppCompatActivity {


    static LinearLayout linlaHeaderProgress;
    static ProgressBar myProgressBar;
    static ArrayList<Thumbnail> thumbnails;
    static ArrayAdapter<Thumbnail> itemsAdapter;
    ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thumbnails= new ArrayList<>();
        setContentView(R.layout.activity_listimages);
        this.listView = (ListView) findViewById(R.id.customListView);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        myProgressBar = findViewById(R.id.pBar);
        this.itemsAdapter = new ThumbnailAdapter(this, 0, thumbnails,1);
        this.listView.setAdapter(itemsAdapter);

        connectAndFillList();

    }


    @SuppressLint("StaticFieldLeak")
    private void connectAndFillList() {

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

                try {

                    int reply;
                    int it =1;
                    FTPFile[] files = MainActivity.ftp.listFiles();
                    System.out.println("NUMERO DE ELEMENTOS: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        //for (FTPFile file: files){
                        if(files[i].isFile()){
                            String filename = files[i].getName();
                            Thumbnail thumbnail = new Thumbnail(filename);
                            thumbnails.add(thumbnail);
                            System.out.println("FILENAME: " + filename);
                            System.out.println("Iteracion: " + it);
                            it += 1;
                        }

                    }

                }
                catch (IOException e)
                {
                    Log.d("ERROR","Could not connect to host");
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                System.out.println("ESTOY EN ON POST EXECUTE");
                itemsAdapter.notifyDataSetChanged();
                // HIDE THE SPINNER AFTER LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

    }


   /* static void isSomethingChecked(){

        for(Thumbnail thumb:thumbnails){
            if (thumb.isDownloaded()){
                ListImagesActivity.download_btn.setVisibility(View.VISIBLE);
                return;
            }
        }

        ListImagesActivity.download_btn.setVisibility(View.INVISIBLE);
    }*/




}
