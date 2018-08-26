package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.util.ArrayList;


public class OtherFragment extends Fragment {

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_listimages, container, false);

        this.listView = (ListView) view.findViewById(R.id.customListView);

        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        myProgressBar = view.findViewById(R.id.pBar);

        System.out.println("MPAGE 2");
        this.itemsAdapter = new ThumbnailAdapter(view.getContext(),0,thumbnails,2);
        this.listView.setAdapter(itemsAdapter);
        fillDownloadedImages(view.getContext());


        return view;
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
