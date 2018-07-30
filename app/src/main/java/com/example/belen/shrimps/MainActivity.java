package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.view.Window;
import android.widget.*;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.TrustManagerUtils;


public class MainActivity extends Activity {

    Button button, shutdown_button, takephoto_button;
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
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btnChangeImage);
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
                        Toast.makeText(context, "Tomando foto+...", Toast.LENGTH_SHORT).show();
                    }

                });
                try {
                    SocketConnection socket = new SocketConnection();
                    socket.takePhoto();
                    socket.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        shutdown_button = (Button) findViewById(R.id.btnShutdown);
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
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        addListenerOnButton();
        this.thumbnails= new ArrayList<>();
        this.itemsAdapter = new ThumbnailAdapter(this, 0, thumbnails);
        this.listView = (ListView) findViewById(R.id.customListView);
        this.listView.setAdapter(itemsAdapter);

    }


    public void addListenerOnButton() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                connectAndFillList();
            }

        });

    }

    @Override
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

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        ftp = PhotoActivity.ftp;
        ArrayList<String> thumbnails_stringify = savedInstanceState.getStringArrayList("ThumbnailsList");
        for (String thumb_string: thumbnails_stringify){
            thumbnails.add(Thumbnail.restore(thumb_string));
        }

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
                    /*
                     * Set File Transfer Mode
                     * To avoid corruption issue you must specified a correct
                     * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                     * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                     * transferring text, image, and compressed files.
                     */
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.enterLocalPassiveMode();
                    /*Toast.makeText(getApplicationContext(),
                            "ftp connected",
                            Toast.LENGTH_LONG).show();
*/
                    int it = 1;
                    FTPFile[] files = ftp.listFiles();
                    System.out.println("NUMERO DE ELEMENTOS: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        //for (FTPFile file: files){
                        String filename = files[i].getName();
                        Thumbnail thumbnail = new Thumbnail(filename);
                        thumbnails.add(thumbnail);
                        System.out.println("FILENAME: " + filename);
                        System.out.println("Iteracion: " + it);
                        it += 1;
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
