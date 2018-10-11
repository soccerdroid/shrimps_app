package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListImagesActivity extends AppCompatActivity {
    static Button download_btn,erase_btn;
    static LinearLayout linlaHeaderProgress;
    static ProgressBar myProgressBar;
    static ArrayList<Thumbnail> thumbnails;
    static ArrayAdapter<Thumbnail> itemsAdapter;
    ListView listView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        //Verifies that it is connnected to ftp server
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if(wifiInfo!=null){
            String wifi_name = wifiInfo.getSSID();
            if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")==false || MainActivity.ftp==null || !MainActivity.ftp.isConnected()){

                CharSequence text = "No está conectado a la red de la raspberry";
                Toast toast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
            }

        }
        else {
            Toast toast = Toast.makeText(this.getApplicationContext(), "No hay red wifi", Toast.LENGTH_SHORT);
            toast.show();
            this.finish();
        }
        //end of verification
        setContentView(R.layout.activity_list_images);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //get the download and erase buttons
        download_btn = findViewById(R.id.download_btn);
        download_btn.setVisibility(View.VISIBLE);
        erase_btn = findViewById(R.id.erase_btn);
        erase_btn.setVisibility(View.VISIBLE);
        addDownloadListener();
        addEraseButtonListener();
        thumbnails= new ArrayList<>();
        this.listView = (ListView) findViewById(R.id.customListView);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        myProgressBar = findViewById(R.id.pBar);
        this.itemsAdapter = new ThumbnailAdapter(this, 0, thumbnails,1);
        this.listView.setAdapter(itemsAdapter);

        connectAndFillList();
    }

    void addDownloadListener(){

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                byte[] buffer;
                final Dialog ddialog;
                ddialog = new Dialog(context);
                ddialog.setContentView(R.layout.download_dialog);
                ddialog.show();
                for(Thumbnail thumb: thumbnails){
                    String filename = thumb.getName();
                    InputStream input = null;
                    if(thumb.isDownloaded()){
                        try {
                            TextView percentage_tv = ddialog.findViewById(R.id.percentage_tv);
                            FTPFile file = MainActivity.ftp.mlistFile(filename);
                            long size = file.getSize();
                            percentage_tv.setText("0/"+size);
                            //resize photo
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int new_width = displayMetrics.widthPixels;
                            //read from server
                            input = MainActivity.ftp.retrieveFileStream(filename);
                            ByteArrayOutputStream baos= new ByteArrayOutputStream();
                            //progress bar
                            /*int sent = 0;
                            int bytesRead;
                            buffer = new byte[8092];
                            while((bytesRead=input.read(buffer))>0){
                                sent+=bytesRead;
                                System.out.println("bytes leidos "+sent);
                                baos.write(buffer, 0, bytesRead);
                                percentage_tv.setText(sent+"/"+size);
                            }
                            baos.flush();
                            input.close();
                            input = new ByteArrayInputStream(baos.toByteArray());*/
                            BufferedInputStream buf = new BufferedInputStream(input);
                            bitmap = BitmapFactory.decodeStream(buf);
                            //Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
                            //save it in internal memory
                            File directory = ListImages.createFolder("Shrimps-images");
                            if(directory!=null){
                                File photo = new File(directory,filename);
                                FileOutputStream fos = new FileOutputStream(photo);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , fos);
                                Toast.makeText(v.getContext(), "Descarga exitosa", Toast.LENGTH_SHORT).show();
                                fos.close();
                                input.close();
                                bitmap.recycle(); //was not before
                                if(!MainActivity.ftp.completePendingCommand()) {
                                    MainActivity.ftp.logout();
                                    MainActivity.ftp.disconnect();
                                    Toast.makeText(v.getContext(), "Transferencia de imagen "+filename+ " sin éxito", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            else{
                                Toast.makeText(v.getContext(), "Carpeta de imágenes2 no disponible", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(v.getContext(), "Hubo un error con la descarga"+filename, Toast.LENGTH_SHORT).show();
                        }

                    }


                }
                ddialog.dismiss();

            }
        });
    }

    void addEraseButtonListener(){
        erase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage("Se procederá a borrar las imágenes en el servidor");
                        alertDialogBuilder.setPositiveButton("Seguir",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        for(Thumbnail thumb: thumbnails){
                                            String filename = thumb.getName();
                                            InputStream input = null;
                                            if(thumb.isDownloaded()){
                                                try {
                                                    MainActivity.ftp.deleteFile(filename);
                                                   Activity a= (Activity)context;
                                                   a.recreate();
                                                    //connectAndFillList();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "No se pudo eliminar la imagen"+filename, Toast.LENGTH_SHORT).show();
                                                }

                                            }


                                        }
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
    public Bitmap fillWidthScreen(int newWidth, int newHeight, int width, int height, Bitmap bm){
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resized;
    }

    @SuppressLint("StaticFieldLeak")
    private void connectAndFillList() {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                int color = Color.parseColor("#007DD6");
                myProgressBar.getIndeterminateDrawable()
                        .setColorFilter(color, PorterDuff.Mode.SRC_IN);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }

            protected Void doInBackground(Void... params) {

                try {

                    int reply;
                    int it =1;
                    FTPFile[] files = MainActivity.ftp.listFiles();
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
                System.out.println("ESTOY EN ON POST EXECUTE");
                itemsAdapter.notifyDataSetChanged();
                // HIDE THE SPINNER AFTER LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

    }
    static void isSomethingChecked(){

        for(Thumbnail thumb:thumbnails){
            if (thumb.isDownloaded()){
                download_btn.setVisibility(View.VISIBLE);
                return;
            }
        }

        download_btn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        connectAndFillList();
    }


}
