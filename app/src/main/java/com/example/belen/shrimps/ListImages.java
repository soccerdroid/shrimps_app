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
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ListImages extends Activity {


        Button button, takephoto_button;
        ImageView image;
        public static FTPClient ftp;
        public static String server;
        public static String username;
        public static String password;
        String working_directory="/";
        public static int port;
        ArrayList<Thumbnail> thumbnails;
        ArrayAdapter<Thumbnail> itemsAdapter;
        ListView listView;
        private ProgressBar spinner;
        LinearLayout linlaHeaderProgress;



        @Override
        public void onCreate(Bundle savedInstanceState) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            super.onCreate(savedInstanceState);
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_listimages);
            button = (Button) findViewById(R.id.btnChangeImage);
            takephoto_button = (Button) findViewById(R.id.btnTakePhoto);
            takephoto_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    arg0.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(arg0.getContext());
                        }
                        private void showToast(Context context) {
                            Toast.makeText(context, "Tomando foto...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        SocketConnection socket = new SocketConnection();
                        String photo_name = socket.takePhoto(); // was not before
                        socket.closeConnection();
                        Intent intent = new Intent(arg0.getContext(), PhotoViewActivity.class); // was not before
                        intent.putExtra("photo_name",photo_name ); // was not before
                        arg0.getContext().startActivity(intent); // was not before
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
            addListenerOnButton();
            this.thumbnails= new ArrayList<>();
            this.itemsAdapter = new ThumbnailAdapter(this, 0, thumbnails);
            this.listView = (ListView) findViewById(R.id.customListView);
            this.listView.setAdapter(itemsAdapter);
            WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String wifi_name = wifiInfo.getSSID();
            System.out.println("WIFI NAME: "+wifi_name);


        }


        public void addListenerOnButton() {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    if(wifiInfo!=null){
                        String wifi_name = wifiInfo.getSSID();
                        if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")){
                            connectAndFillList();
                        }
                        else {
                            Context context = getApplicationContext();
                            CharSequence text = "No está conectado a la red de la raspberry";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                    else {
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "No hay red wifi", Toast.LENGTH_SHORT);
                    }

                }

            });

        }


        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
        /*// Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        ftp = PhotoActivity.ftp;
        ArrayList<String> thumbnails_stringify = savedInstanceState.getStringArrayList("ThumbnailsList");
        for (String thumb_string: thumbnails_stringify){
            thumbnails.add(Thumbnail.restore(thumb_string));
        }
*/
            connectAndFillList();
        }

        @SuppressLint("StaticFieldLeak")
        private void connectAndFillList() {

            linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

            new AsyncTask<Void, Void, Void>() {

                protected void onPreExecute() {
                    // TODO Auto-generated method stub
                    super.onPreExecute();
                    ProgressBar myProgressBar = findViewById(R.id.pBar);
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

                            //itemsAdapter.notifyDataSetChanged();

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
                    itemsAdapter.notifyDataSetChanged();
                    // HIDE THE SPINNER AFTER LOADING FEEDS
                    linlaHeaderProgress.setVisibility(View.GONE);
                }
            }.execute();

        }





}
