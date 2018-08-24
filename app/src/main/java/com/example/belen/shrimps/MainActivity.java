package com.example.belen.shrimps;

import android.content.Context;
import android.content.pm.ActivityInfo;

import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.*;


import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import java.io.IOException;



public class MainActivity extends Activity {

    Button button, shutdown_button, takephoto_button;
    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btnImages);
        addListenerOnButton();
        takephoto_button = (Button) findViewById(R.id.btnTakePhoto);
        takephoto_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                arg0.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast(arg0.getContext());
                    }
                    private void showToast(Context context) {
                        Toast.makeText(context, "Tomando foto...", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    SocketConnection socket = new SocketConnection();
                    String photo_name = socket.takePhoto(); // was not before
                    socket.closeConnection();
                    Intent intent = new Intent(arg0.getContext(), PhotoViewActivity.class); // was not before
                    intent.putExtra("photo_name",photo_name ); // was not before
                    arg0.getContext().startActivity(intent); // was not before
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        shutdown_button = (Button) findViewById(R.id.btnRaspberry);
        shutdown_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                try {
                    arg0.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(arg0.getContext());
                        }
                        private void showToast(Context context) {
                            Toast.makeText(context, "Apagando...", Toast.LENGTH_SHORT).show();
                        }

                    });
                    SocketConnection socket = new SocketConnection();
                    socket.shutdownPi();
                    socket.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void addListenerOnButton() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), ListImagesActivity.class);
                arg0.getContext().startActivity(intent); // was not before
            }

        });

    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        ArrayList<String> thumbnails_stringify = new ArrayList<String>();
        for (Thumbnail thumbnail: thumbnails){
            String thumb_string = thumbnail.stringify();
            thumbnails_stringify.add(thumb_string);
        }
        outState.putStringArrayList("ThumbnailsList", thumbnails_stringify );

        super.onSaveInstanceState(outState);

    }
*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*// Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        ftp = PhotoActivity.ftp;
        ArrayList<String> thumbnails_stringify = savedInstanceState.getStringArrayList("ThumbnailsList");
        for (String thumb_string: thumbnails_stringify){
            thumbnails.add(Thumbnail.restore(thumb_string));
        }
*/

    }





}
