package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.security.AccessController.getContext;

public class PhotoActivity extends AppCompatActivity  {

    Toast toast;
    public static FTPClient ftp;
    TextView thumbnail_name;
    Button backBtn, eraseBtn, saveBtn;
    Spinner spinner;
    public static MyCanvasView myCanvasView;
    String name;
    Boolean notSaved = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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


        try {
            InputStream input = this.ftp.retrieveFileStream(name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int new_width = displayMetrics.widthPixels;
            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
            addEraseListener();
            myCanvasView.setBitmap(resized_bitmap); //was not before
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

    public Bitmap fillWidthScreen(int newWidth, int newHeight, int width, int height, Bitmap bm){
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resized;
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
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast(v.getContext());
                    }
                    private void showToast(Context context) {
                        Toast.makeText(context, "Guardando...", Toast.LENGTH_SHORT).show();
                    }

                });

                myCanvasView.setDrawingCacheEnabled(true);
                myCanvasView.setDrawingCacheQuality(myCanvasView.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = myCanvasView.getDrawingCache();
                //Transform bitmap to inputstream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                try {
                    String edited_image_name = "edited_"+name;
                    boolean changes_directory = ftp.changeWorkingDirectory("edited");
                    boolean was_saved= ftp.storeFile(edited_image_name,bs);
                    bs.close();
                    boolean return_directory = ftp.changeToParentDirectory();
                    if (changes_directory && was_saved && return_directory) {
                        notSaved = false;
                        System.out.println("The file was uploaded successfully.");
                    }
                    else{
                        System.out.println("could not changed directory");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }




}
