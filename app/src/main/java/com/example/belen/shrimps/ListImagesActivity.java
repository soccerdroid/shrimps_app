package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListImagesActivity extends AppCompatActivity {
    static Button download_btn;
    static LinearLayout linlaHeaderProgress;
    static ProgressBar myProgressBar;
    static ArrayList<Thumbnail> thumbnails;
    static ArrayAdapter<Thumbnail> itemsAdapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Verifies that it is connnected to ftp server
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if(wifiInfo!=null){
            String wifi_name = wifiInfo.getSSID();
            if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")==false || MainActivity.ftp==null){
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
        download_btn = findViewById(R.id.download_btn);
        addDownloadListener();
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
                for(Thumbnail thumb: thumbnails){
                    String filename = thumb.getName();
                    InputStream input = null;
                    if(thumb.isDownloaded()){
                        try {
                            System.out.println("Downloading");
                            //resize photo
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int new_width = displayMetrics.widthPixels;
                            //read from server
                            input = MainActivity.ftp.retrieveFileStream(filename);
                            BufferedInputStream buf = new BufferedInputStream(input);
                            Bitmap bitmap = BitmapFactory.decodeStream(buf);
                            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
                            //save it in internal memory
                            File directory = ListImages.createFolder("Shrimps-images");
                            if(directory!=null){
                                File photo = new File(directory,filename);
                                FileOutputStream fos = new FileOutputStream(photo);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , fos);
                                Toast.makeText(v.getContext(), "Descarga exitosa", Toast.LENGTH_SHORT).show();
                                fos.close();
                                input.close();
                                if(!MainActivity.ftp.completePendingCommand()) {
                                    MainActivity.ftp.logout();
                                    MainActivity.ftp.disconnect();
                                    System.out.println("File transfer failed.");
                                    finish();
                                }
                            }
                            else{
                                Toast.makeText(v.getContext(), "Almacenamiento no disponible", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(v.getContext(), "Hubo un error con la foto"+filename, Toast.LENGTH_SHORT).show();
                        }

                    }


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
