package com.example.belen.shrimps;


import android.annotation.SuppressLint;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.belen.shrimps.ListImages.server;

public class PageFragment extends Fragment {

    int port;
    String username,password;
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

        System.out.println("MPAGE 1");
        this.itemsAdapter = new ThumbnailAdapter(view.getContext(), 0, thumbnails,1);
        this.listView.setAdapter(itemsAdapter);
        connectAndFillList();

        return view;
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
                port = 21;
                server = "192.168.20.1";
                username = "usuario";
                password = "0000";
                ftp = new FTPClient();
                try {
                    int reply;
                    ftp.connect(server, port);
                    // After connection attempt, you should check the reply code to verify
                    // success.
                    Log.d("SUCCESS", "Connected to " + server + ".");
                    Log.d("FTP_REPLY", ftp.getReplyString());
                    reply = ftp.getReplyCode();

                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftp.disconnect();
                        Log.d("REPLY_ERROR", "FTP server refused connection.");
                    }
                    boolean status = ftp.login(username, password);
                    //set timeout to 15 min
                    ftp.setConnectTimeout(1800000);
                    ftp.setSoTimeout(1800000);
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.enterLocalPassiveMode();
                    int it = 1;
                    FTPFile[] files = ftp.listFiles();
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




}
