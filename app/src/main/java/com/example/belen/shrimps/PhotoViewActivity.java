package com.example.belen.shrimps;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class PhotoViewActivity extends AppCompatActivity {
    String photo_name;
    Button backBtn, eraseBtn;
    public static FTPClient ftp;
    ImageView photo_iv;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.photo_view);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        photo_name = (String) b.get("photo_name");
        System.out.println("NOMBRE DE IMAGEN RECIBIDO: "+photo_name);
        System.out.println("TAMAÑO DE IMAGEN RECIBIDO: "+photo_name.length());
        backBtn = findViewById(R.id.back_btn);
        eraseBtn = findViewById(R.id.erase_btn);
        photo_iv = findViewById(R.id.photo_iv);
        this.ftp = MainActivity.ftp;
        addListenerOnButton();
        Bitmap bitmap = null;


        try {
            InputStream input = this.ftp.retrieveFileStream(photo_name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int new_width = displayMetrics.widthPixels;
            Bitmap resized_bitmap = fillWidthScreen(new_width,480,640,480,bitmap); //was not before
            photo_iv.setImageBitmap(resized_bitmap);
            buf.close();
            input.close();
            if(!this.ftp.completePendingCommand()) {
                this.ftp.logout();
                this.ftp.disconnect();
                System.err.println("File transfer failed.");
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

    public Bitmap fillWidthScreen(int newWidth, int newHeight, int width, int height, Bitmap bm){
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resized;
    }
}







