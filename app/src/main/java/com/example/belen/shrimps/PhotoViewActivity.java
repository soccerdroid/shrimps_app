package com.example.belen.shrimps;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.belen.shrimps.Utils.Util;

import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class PhotoViewActivity extends AppCompatActivity {
    String photo_name;
    //Button backBtn;
    public static FTPClient ftp;
    ImageView photo_iv;
    Button erase_btn, tomar_de_nuevo_btn;
    Context ctx;
    SocketConnection socket;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.photo_view);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        photo_name = (String) b.get("photo_name");
        //backBtn = findViewById(R.id.back_btn);
        photo_iv = findViewById(R.id.photo_iv);
        ctx = this;
        //addListenerOnButton();
        Bitmap bitmap = null;
        erase_btn = findViewById(R.id.borrar_btn);
        tomar_de_nuevo_btn = findViewById(R.id.tomar_de_nuevo_btn);
        addEraseButtonListener();
        addTakePhotoAgainListener();


        try {
            //si no hay conexión con el servidor...
            if(MainActivity.ftp==null || !MainActivity.ftp.isConnected()){
                Toast.makeText(this.getApplicationContext(), "No hay conexión con el servidor", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            //caso contrario
            InputStream input = MainActivity.ftp.retrieveFileStream(photo_name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int new_width = displayMetrics.widthPixels;
            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
            photo_iv.setImageBitmap(resized_bitmap);
            buf.close();
            input.close();
            if(!MainActivity.ftp.completePendingCommand()) {
                MainActivity.ftp.logout();
                MainActivity.ftp.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //Función para tomar foto de nuevo
    private void addTakePhotoAgainListener() {

        tomar_de_nuevo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Util util = new Util();
                    socket = new SocketConnection();
                    String params = util.getCameraParameters(getWindow().getDecorView().getRootView().getContext());
                    String photo_name = socket.takePhotoWithParams(params); // --- SEND CAMERA PARAMETERS HERE
                    socket.closeConnection();
                    Toast.makeText(getApplicationContext(), photo_name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PhotoViewActivity.class);
                    intent.putExtra("photo_name",photo_name );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No se pudo tomar la imagen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public Bitmap fillWidthScreen(int newWidth, int newHeight, int width, int height, Bitmap bm){
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resized;
    }

    void addEraseButtonListener(){
        erase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage("Se procederá a borrar la imagen en el servidor");
                alertDialogBuilder.setPositiveButton("Seguir",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                    InputStream input = null;

                                        try {
                                            MainActivity.ftp.deleteFile(photo_name);
                                            //connectAndFillList();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(ctx, "No se pudo eliminar la imagen"+photo_name, Toast.LENGTH_SHORT).show();
                                        }
                                        finish();

                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }
}







