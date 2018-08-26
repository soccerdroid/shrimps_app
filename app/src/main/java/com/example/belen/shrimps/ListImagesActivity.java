package com.example.belen.shrimps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ListImagesActivity extends AppCompatActivity {
    static Button download_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_images);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.inflateMenu(R.menu.item);
        //getSupportActionBar().setTitle("Imágenes");

        download_btn = findViewById(R.id.download_btn);
        addDownloadListener();
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                this.getApplicationContext()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    void addDownloadListener(){

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Thumbnail thumb: PageFragment.thumbnails){
                    System.out.println("Reading");
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
                            if(!MainActivity.status){
                                MainActivity.status = MainActivity.ftp.login(MainActivity.username, MainActivity.password);
                            }
                            input = MainActivity.ftp.retrieveFileStream(filename);
                            BufferedInputStream buf = new BufferedInputStream(input);
                            Bitmap bitmap = BitmapFactory.decodeStream(buf);
                            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
                            //save it in internal memory
                            File directory = v.getContext().getFilesDir();
                            File photo = new File(directory,filename);
                            FileOutputStream fos = new FileOutputStream(photo);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , fos);
                            Toast.makeText(v.getContext(), "Descarga exitosa", Toast.LENGTH_SHORT).show();
                            fos.close();
                            input.close();
                            if(!MainActivity.ftp.completePendingCommand()) {
                                MainActivity.ftp.logout();
                                MainActivity.ftp.disconnect();
                                System.err.println("File transfer failed.");
                                finish();
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
}
