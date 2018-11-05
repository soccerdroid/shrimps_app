package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.belen.shrimps.Utils.Constants;

import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class PhotoActivity extends AppCompatActivity  {

    Toast toast;
    static int new_width, new_height;
    public static FTPClient ftp;
    TextView thumbnail_name;
    ImageButton saveBtn,undoBtn, zoomBtn, redoBtn;
    Spinner spinner;
    public static MyCanvasView myCanvasView;
    String name;
    Context context;
    Boolean notSaved = true;
    ArrayList<ColorSpinnerElement> colorList;
    ColorSpinnerAdapter colorSpinnerAdapter;
    String newname;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this;
        //setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_photo_2);
        myCanvasView = (MyCanvasView)findViewById(R.id.my_canvas);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        name = (String) b.get("name");
        thumbnail_name = findViewById(R.id.thumbnail_name_tv);
        saveBtn = findViewById(R.id.save_btn);
        undoBtn = findViewById(R.id.undoBtn);
        zoomBtn = findViewById(R.id.zoomBtn);
        redoBtn = findViewById(R.id.redoBtn);
        thumbnail_name.setText(name);
        this.ftp = MainActivity.ftp;
        addSaveListener();
        addUndoListener();
        addZoomListener();
        spinner = (Spinner) findViewById(R.id.palette_spinner);
        initColorList(); // fills the list to pass to the spinner adapter
        colorSpinnerAdapter = new ColorSpinnerAdapter(this, this.colorList);
        spinner.setAdapter(colorSpinnerAdapter);
        spinner.setOnItemSelectedListener(new MySpinnerListener());
        addRedoListener();
        Bitmap bitmap = null;


        try {
            //InputStream input = openFileInput(name);
            //BufferedInputStream buf = new BufferedInputStream(input);
            //bitmap = BitmapFactory.decodeStream(buf);
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root +"/"+ "Shrimps-images");
            bitmap = BitmapFactory.decodeFile(myDir+"/"+name);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            new_width = displayMetrics.widthPixels;
            new_height = displayMetrics.heightPixels;

            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
            myCanvasView.setBitmap(resized_bitmap);

        } catch (Exception e) {
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
    public void addUndoListener(){
        this.undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvasView.onClickUndo();
            }
        });

    }

    //Adds a listener to the redo button
    public void addRedoListener(){
        this.redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvasView.onClickRedo();
            }
        });

    }
    //Adds a listener to the backBtn button
    public void addZoomListener(){
        this.zoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCanvasView.zoomStatus==false){
                    myCanvasView.setZoomStatus(true);
                    zoomBtn.setBackgroundResource(R.color.selectedBlue);
                    //zoomBtn.setPressed(true);
                }
                else{
                    myCanvasView.setZoomStatus(false);
                    zoomBtn.setBackgroundResource(R.color.grey);
                    //zoomBtn.setPressed(false);
                }

            }
        });

    }


    //Adds a listener to the saveBtn
    public void addSaveListener(){
        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                myCanvasView.reiniciarZoom(); //gets to original position
                myCanvasView.setDrawingCacheEnabled(true);
                myCanvasView.setDrawingCacheQuality(myCanvasView.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = myCanvasView.getDrawingCache();
                FileOutputStream outputStream;
                try {

                    File directory = ListImages.createFolder("Shrimps-images");
                    File file = new File(directory, name);
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
                    outputStream.close();
                    Toast.makeText(v.getContext(), "Guardado", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void initColorList(){
        this.colorList = new ArrayList<>();
        this.colorList.add(new ColorSpinnerElement(Constants.color0, "1"));
        this.colorList.add(new ColorSpinnerElement(Constants.color1, "2"));
        this.colorList.add(new ColorSpinnerElement(Constants.color2, "3"));
        this.colorList.add(new ColorSpinnerElement(Constants.color3, "4"));
        this.colorList.add(new ColorSpinnerElement(Constants.color4, "5"));

    }


}
