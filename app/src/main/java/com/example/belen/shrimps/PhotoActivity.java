package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoActivity extends AppCompatActivity  {


    public static FTPClient ftp;
    TextView thumbnail_name;
    Button backBtn, eraseBtn, saveBtn;
    Spinner spinner;
    Point size ;
    public static MyCanvasView myCanvasView;
    String name;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_2);
        myCanvasView = (MyCanvasView)findViewById(R.id.my_canvas);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        name = (String) b.get("name");
        thumbnail_name = findViewById(R.id.thumbnail_name_tv);
        backBtn = findViewById(R.id.back_btn);
        eraseBtn = findViewById(R.id.erase_btn);
        saveBtn = findViewById(R.id.save_btn);
        thumbnail_name.setText(name);
        this.ftp = MainActivity.ftp;
        addListenerOnButton();
        addSaveListener();
        spinner = (Spinner) findViewById(R.id.palette_spinner);
        spinner.setOnItemSelectedListener(new MySpinnerListener());
        Bitmap bitmap = null;
        size = new Point();

        try {
            InputStream input = this.ftp.retrieveFileStream(name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            addEraseListener();
            myCanvasView.setBitmap(bitmap);
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

    //Adds a listener to the backBtn button
    public void addListenerOnButton(){
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //Adds a listener to the eraseBtn
    public void addEraseListener(){
        this.eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvasView.onClickUndo();
            }
        });
    }

    //Adds a listener to the saveBtn
    public void addSaveListener(){
        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvasView.setDrawingCacheEnabled(true);
                myCanvasView.setDrawingCacheQuality(myCanvasView.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = myCanvasView.getDrawingCache();
                //Transform bitmap to inputstream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0 , bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                try {
                    String edited_image_name = "edited_"+name;
                    boolean was_saved= ftp.storeFile(edited_image_name,bs);
                    bs.close();
                    if (was_saved) {
                        System.out.println("The file was uploaded successfully.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                /*String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");*/
            }
        });
    }

}
