package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;


import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;



public class MainActivity extends Activity {

    Button button, shutdown_button,restart_button, takephoto_button, tagimages_button;
    ImageView image;
    int port;
    static String username,password,server;
    static FTPClient ftp;
    static boolean status;
    static TextView status_tv;
    LinearLayout pBarContainer;
    private final String DISABLE_BTN = "disable_btn";
    private final String ENABLE_BTN = "enable_btn";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btnImages);
        addListenerOnButton();
        status_tv = findViewById(R.id.status_tv);
        pBarContainer = findViewById(R.id.connectionpBarContainer);
        //botón para tomar foto
        takephoto_button = (Button) findViewById(R.id.btnTakePhoto);
        tagimages_button = findViewById(R.id.btnTagImages);
        setCameraOpListener();
        setTagImagesListener();
        shutdown_button = (Button) findViewById(R.id.btnTurnoffRasp);
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
                    status_tv.setTextColor(getResources().getColor(R.color.opaque_orange));
                    status_tv.setText("Desconectado");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        //reiniciar la raspberry
        restart_button = (Button) findViewById(R.id.btnRestartRasp);
        restart_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                try {
                    arg0.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(arg0.getContext());
                        }
                        private void showToast(Context context) {
                            Toast.makeText(context, "Reiniciando...", Toast.LENGTH_SHORT).show();
                        }

                    });
                    SocketConnection socket = new SocketConnection();
                    socket.restartPi();
                    socket.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        status = false;
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if(wifiInfo!=null){
            String wifi_name = wifiInfo.getSSID();

            if(wifi_name.equalsIgnoreCase("\"Pi_AP\"") == false){
                //connectToFTPAsync(this);
                CharSequence text = "No está conectado a la red de la raspberry";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getApplicationContext(), text, duration);
                toast.show();
            }

        }
        else {
            Toast toast = Toast.makeText(this.getApplicationContext(), "No hay red wifi", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("ENTRÉ AL ON RESUME DEL MAIN");
        //status = false;
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if(wifiInfo!=null){
            String wifi_name = wifiInfo.getSSID();
            if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")){
                if(this.ftp==null || !this.ftp.isConnected()){
                    connectToFTPAsync(this);
                }
            }
            else {
                CharSequence text = "No está conectado a la red de la raspberry";
                this.ftp=null;
                status_tv.setTextColor(getResources().getColor(R.color.opaque_orange));
                status_tv.setText("Desconectado");
                changeButtonsStatus(DISABLE_BTN);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getApplicationContext(), text, duration);
                toast.show();

            }
        }
        else {
            Toast toast = Toast.makeText(this.getApplicationContext(), "No hay red wifi", Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    public void setCameraOpListener(){
        this.takephoto_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setTagImagesListener(){
        this.tagimages_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListImages.class);
                startActivity(intent);
            }
        });
    }


    public void addListenerOnButton() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(ftp!=null && ftp.isConnected()) {
                    Intent intent = new Intent(arg0.getContext(), ListImagesActivity.class);
                    arg0.getContext().startActivity(intent); // was not before
                }
                else{
                    Toast.makeText(arg0.getContext(), "No hay conexión con el servidor aún", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
    @SuppressLint("StaticFieldLeak")
    private void connectToFTPAsync(final Activity activity) {

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            protected Void doInBackground(Void... params) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "Estableciendo conexión con servidor...", Toast.LENGTH_SHORT).show();
                        //change status text and color
                        status_tv.setTextColor(getResources().getColor(R.color.darkGrey));
                        status_tv.setText("Estableciendo conexión...");
                        pBarContainer.setVisibility(View.VISIBLE);
                    }
                });

                connectToFTP(activity.getApplicationContext());
                return null;
            }

            protected void onPostExecute(Void result) {
                pBarContainer.setVisibility(View.GONE);
            }
        }.execute();

    }


    public void connectToFTP(Context context){
        port = 21;
        server = "192.168.20.1";
        username = "usuario";
        password = "0000";
        ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(server, port);
            // After connection attempt, you should check the reply code to verify
            // success.
            Log.d("SUCCESS", "Connected to " + server + ".");
            Log.d("FTP_REPLY", ftp.getReplyString());
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                status_tv.setTextColor(getResources().getColor(R.color.opaque_orange));
                status_tv.setText("Desconectado");
                changeButtonsStatus(DISABLE_BTN); //puts in grey buttons in menu
                Log.d("REPLY_ERROR", "FTP server refused connection.");

            }
            status = ftp.login(username, password);
            //send messages every 5 min to keep alive ftp connection
            ftp.setControlKeepAliveTimeout(300);
            //set file type and mode to receive and donwload
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            this.runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(getWindow().getDecorView().getRootView().getContext(), "Conectado", Toast.LENGTH_SHORT).show();
                    status_tv.setTextColor(getResources().getColor(R.color.connectedGreen));
                    status_tv.setText("Conectado");
                    changeButtonsStatus(ENABLE_BTN);
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
            status_tv.setTextColor(getResources().getColor(R.color.opaque_orange));
            status_tv.setText("Desconectado");
            changeButtonsStatus(DISABLE_BTN);

        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void changeButtonsStatus(String statusOrder){
        boolean status = false;
        int disable_color = getResources().getColor(R.color.grey);
        if(statusOrder.equals(ENABLE_BTN)){

             status = true;
            //take photo button
            this.takephoto_button.setEnabled(status);
            this.takephoto_button.setBackgroundDrawable(ContextCompat.getDrawable((Activity)this,R.drawable.rounded_btn));
            //shutdown button
            this.shutdown_button.setEnabled(status);
            this.shutdown_button.setBackgroundDrawable(ContextCompat.getDrawable((Activity)this,R.drawable.turnoff_rasp_btn));
            //restart button
            this.restart_button.setEnabled(status);
            this.restart_button.setBackgroundDrawable(ContextCompat.getDrawable((Activity)this,R.drawable.restart_rasp_btn));
            //list images button
            this.button.setEnabled(status);
            this.button.setBackgroundDrawable(ContextCompat.getDrawable((Activity)this,R.drawable.rounded_btn));
            return;
        } else{
            //take photo button
            this.takephoto_button.setEnabled(status);
            this.takephoto_button.setBackgroundColor(disable_color);
            //shutdown buttona
            this.shutdown_button.setEnabled(status);
            this.shutdown_button.setBackgroundColor(disable_color);
            //restart button
            this.restart_button.setEnabled(status);
            this.restart_button.setBackgroundColor(disable_color);
            //list images button
            this.button.setEnabled(status);
            this.button.setBackgroundColor(disable_color);
            return;
        }

    }



}
