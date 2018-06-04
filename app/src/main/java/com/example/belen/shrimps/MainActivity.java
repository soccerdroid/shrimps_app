package com.example.belen.shrimps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
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

    Button button;
    ImageView image;
    FTPClient ftp;
    String server, username, password;
    String working_directory="/";
    int port;

    public void connectToFTP(){
        port = 21;
        server = "10.10.1.116";
        username = "usuario";
        password = "0000";
        this.ftp = new FTPClient();
        try
        {
            int reply;
            this.ftp.connect(server,port);
            // After connection attempt, you should check the reply code to verify
            // success.
            Log.d("SUCCESS","Connected to " + server + ".");
            Log.d("FTP_REPLY",this.ftp.getReplyString());
            reply = this.ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                this.ftp.disconnect();
                Log.d("REPLY_ERROR","FTP server refused connection.");
            }
            boolean status = this.ftp.login(username, password);
            /*
             * Set File Transfer Mode
             * To avoid corruption issue you must specified a correct
             * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
             * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
             * transferring text, image, and compressed files.
             */
            this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftp.enterLocalPassiveMode();
            //this.ftp.changeWorkingDirectory(working_directory);
        }
        catch (IOException e)
        {
            Log.d("ERROR","Could not connect to host");
            e.printStackTrace();
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        String LOG_TAG = "File error";
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();


    }

    public void addListenerOnButton() {

        image = (ImageView) findViewById(R.id.imageView1);
        button = (Button) findViewById(R.id.btnChangeImage);
        button.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View arg0) {
                //image.setImageResource(R.drawable.comensales);
                connectToFTP();
                if(ftp.isConnected()){
                    Toast.makeText(getApplicationContext(),
                            "ftp connected",
                            Toast.LENGTH_LONG).show();
                    try {
                        FTPFile[] files = ftp.listFiles();
                        for (FTPFile file: files){
                            String filename = file.getName();
                            ImageView thumbnail= null;
                            Bitmap bitmap = null;
                            //reading the image file
                            InputStream input = ftp.retrieveFileStream(filename);
                            //try with input ???
                            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(input));
                            thumbnail.setImageBitmap(bitmap);
                            input.close();
                            if(!ftp.completePendingCommand()) {
                                ftp.logout();
                                ftp.disconnect();
                                Log.e("FILE_ERROR","File transfer failed.");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "error in connection",
                            Toast.LENGTH_LONG).show();
                }
            }

        });

    }

}
