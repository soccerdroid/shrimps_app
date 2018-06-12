package com.example.belen.shrimps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PhotoActivity extends AppCompatActivity implements View.OnTouchListener {
    FTPClient ftp;
    String server, username, password;
    int port;
    TextView thumbnail_name;
    Button backBtn;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        String name = (String) b.get("name");
        thumbnail_name = findViewById(R.id.thumbnail_name_tv);
        //Set image view and button
        imageView = findViewById(R.id.iv);
        backBtn = findViewById(R.id.back_btn);
        thumbnail_name.setText(name);
        //connect to ftp server
        //connectToFTP();
        this.ftp = MainActivity.ftp;
        addListenerOnButton();
        //Try to open image
        Bitmap bitmap = null;
        try {
            //File file = new File(this.getCacheDir(),name);
            //OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(file));
            //boolean success = this.ftp.retrieveFile(name, outputStream1);
            System.out.println("FILENAME: "+name);
            InputStream input = this.ftp.retrieveFileStream(name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            imageView.setImageBitmap(bitmap);
            //imageView.setOnTouchListener(this);
            //outputStream1.close();
            buf.close();
            input.close();
            if(!this.ftp.completePendingCommand()) {
                this.ftp.logout();
                this.ftp.disconnect();
                System.err.println("File transfer failed.");
                System.exit(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListenerOnButton(){
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ftp.isConnected()) {
                        ftp.logout();
                        ftp.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                finish();
            }
        });

    }

    public void connectToFTP(){
        port = MainActivity.port;
        server = MainActivity.server;
        username = MainActivity.username;
        password = MainActivity.password;
        this.ftp = new FTPClient();
        try
        {
            int reply;
            this.ftp.connect(server,port);
            // After connection attempt, you should check the reply code to verify
            // success.
            reply = this.ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                this.ftp.disconnect();
                Log.d("SUCCESS","Connected to " + server + ".");
            }
            boolean status = this.ftp.login(username, password);
            this.ftp.enterLocalPassiveMode();
            this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch (IOException e)
        {
            Log.d("ERROR","Could not connect to host");
            e.printStackTrace();
        }

    }

    public void tagBitmap(Bitmap bitmap){
        int mPhotoWidth = bitmap.getWidth();
        int mPhotoHeight = bitmap.getHeight();

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
