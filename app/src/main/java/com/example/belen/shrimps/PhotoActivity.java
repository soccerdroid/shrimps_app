package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.skydoves.colorpickerpreference.ColorPickerView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.SocketOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoActivity extends AppCompatActivity  {


    public static FTPClient ftp;
    TextView thumbnail_name;
    Button backBtn;
    Spinner spinner;
    Point size ;
    public static MyCanvasView myCanvasView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_2);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.photo_activity_layout);
        myCanvasView = (MyCanvasView)findViewById(R.id.my_canvas);
        Bundle b = getIntent().getExtras();
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layout.getWidth(),layout.getHeight());
        //System.out.println("Linear layout size"+layout.getWidth()+","+layout.getHeight());
        //Get passed file name
        String name = (String) b.get("name");
        thumbnail_name = findViewById(R.id.thumbnail_name_tv);
        backBtn = findViewById(R.id.back_btn);
        thumbnail_name.setText(name);
        this.ftp = MainActivity.ftp;
        addListenerOnButton();
        spinner = (Spinner) findViewById(R.id.palette_spinner);
        spinner.setOnItemSelectedListener(new MySpinnerListener());
        //Try to open image
        Bitmap bitmap = null;
        size = new Point();

        try {


            InputStream input = this.ftp.retrieveFileStream(name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);

            //Bitmap tempBitmap = Bitmap.createBitmap(image_width, image_height,Bitmap.Config.ARGB_8888);
            //Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            //myCanvasView = new MyCanvasView(this,null);
            myCanvasView.setBitmap(bitmap);

            //layout.addView(myCanvasView);
            buf.close();
            input.close();
            if(!this.ftp.completePendingCommand()) {
                this.ftp.logout();
                this.ftp.disconnect();
                System.err.println("File transfer failed.");
                finish();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListenerOnButton(){
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    if (ftp.isConnected()) {
                        ftp.logout();
                        ftp.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                finish();
            }
        });

    }


}
