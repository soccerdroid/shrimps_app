package com.example.belen.shrimps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.belen.shrimps.Utils.Constants;
import com.example.belen.shrimps.Utils.Util;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText brightnessEt;
    private EditText contrastEt;
    private EditText saturationEt;
    private EditText gammaEt;
    private EditText whiteBalanceEt;
    private EditText exposureEt;
    private Button saveConfigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        saveConfigBtn = (Button) findViewById(R.id.save_config_btn);
        brightnessEt = (EditText) findViewById(R.id.brightness_edtxt);
        contrastEt = (EditText) findViewById(R.id.contrast_edtxt);
        saturationEt = (EditText) findViewById(R.id.saturation_edtxt);
        gammaEt = (EditText) findViewById(R.id.gamma_edtxt);
        whiteBalanceEt = (EditText) findViewById(R.id.white_balance_edtxt);
        exposureEt = (EditText) findViewById(R.id.exposure_edtxt);
        setDefaultValues();
        setSupportActionBar(toolbar);
    }

    public void setDefaultValues(){
        this.brightnessEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.BRIGHTNESS_STORED_KEY)));
        this.contrastEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.CONTRAST_STORED_KEY)));
        this.saturationEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.SATURATION_STORED_KEY)));
        this.gammaEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.GAMMA_STORED_KEY)));
        this.whiteBalanceEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.WHITE_BALANCE_STORED_KEY)));
        this.exposureEt.setText(Integer.toString(Util.getConfigValue(getApplicationContext(), Constants.EXPOSURE_STORED_KEY)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_photo:
                // User chose the "Settings" item, show the app settings UI...
                if(MainActivity.ftp!=null && MainActivity.ftp.isConnected()) {
                    getWindow().getDecorView().getRootView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Tomando foto...", Toast.LENGTH_SHORT).show();

                        }
                    });
                    try {
                        SocketConnection socket = null;
                        socket = new SocketConnection();
                        String photo_name = socket.takePhoto(); // was not before
                        socket.closeConnection();
                        Intent intent = new Intent(getApplicationContext(), PhotoViewActivity.class); // was not before
                        intent.putExtra("photo_name",photo_name ); // was not before
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent); // was not before
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "No hay conexión con el servidor aún", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }
}
