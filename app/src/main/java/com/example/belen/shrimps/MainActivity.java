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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;


import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

import static com.example.belen.shrimps.ListImages.server;


public class MainActivity extends Activity {

    Button button, shutdown_button, takephoto_button;
    ImageView image;
    int port;
    static String username,password;
    public static FTPClient ftp;
    static boolean status;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btnImages);
        addListenerOnButton();
        takephoto_button = (Button) findViewById(R.id.btnTakePhoto);
        takephoto_button.setOnClickListener(new OnClickListener() {
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
                    System.out.println("TRATANDO DE TOMAR FOTO");
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
        shutdown_button = (Button) findViewById(R.id.btnRaspberry);
        shutdown_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                try {
                    arg0.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(arg0.getContext());
                        }
                        private void showToast(Context context) {
                            Toast.makeText(context, "Apagando...", Toast.LENGTH_SHORT).show();
                        }

                    });
                    SocketConnection socket = new SocketConnection();
                    socket.shutdownPi();
                    socket.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if(wifiInfo!=null){
            String wifi_name = wifiInfo.getSSID();
            if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")){
                connectToFTPAsync(this.getApplicationContext());
            }
            else {
                CharSequence text = "No está conectado a la red de la raspberry";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getApplicationContext(), text, duration);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(this.getApplicationContext(), "No hay red wifi", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void addListenerOnButton() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), ListImagesActivity.class);
                arg0.getContext().startActivity(intent); // was not before
            }

        });

    }
    @SuppressLint("StaticFieldLeak")
    private void connectToFTPAsync(final Context context) {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            protected Void doInBackground(Void... params) {

                connectToFTP(context);
                return null;
            }

            protected void onPostExecute(Void result) {

            }
        }.execute();

    }


    public void connectToFTP(Context context){
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
            status = ftp.login(username, password);
            //set timeout to 15 min

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();

        }
        catch (IOException e)
        {
            Log.d("ERROR","Could not connect to host");
            e.printStackTrace();

        }

    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        ArrayList<String> thumbnails_stringify = new ArrayList<String>();
        for (Thumbnail thumbnail: thumbnails){
            String thumb_string = thumbnail.stringify();
            thumbnails_stringify.add(thumb_string);
        }
        outState.putStringArrayList("ThumbnailsList", thumbnails_stringify );

        super.onSaveInstanceState(outState);

    }
*/
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

    }





}
