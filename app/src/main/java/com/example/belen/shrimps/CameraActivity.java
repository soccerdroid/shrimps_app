package com.example.belen.shrimps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private final int MAX_BRIGHTNESS = 64;
    private final int MIN_BRIGHTNESS = -64;
    private final int MAX_CONTRAST = 30;
    private final int MIN_CONTRAST = 0;
    private final int MAX_SATURATION = 127;
    private final int MIN_SATURATION = 0;
    private final int MAX_GAMMA = 250;
    private final int MIN_GAMMA = 20;
    private final int MAX_WHITE_BALANCE = 6500;
    private final int MIN_WHITE_BALANCE = 2800;
    private final int MAX_EXPOSURE = 5000;
    private final int MIN_EXPOSURE = 2;

    private final String DISABLE_BTN = "disable_btn";
    private final String ENABLE_BTN = "enable_btn";

    private final String SUCCESS_ON_STORAGE = "success_on_storage";
    private final String FAIL_ON_STORAGE = "fail_on_storage";

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
        setHints();
        setOnValuesChangeListeners();
        setSaveConfigBtnListener();
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

    public void changeSaveBtnStatus(String statusOrder){
        if(statusOrder.equals(ENABLE_BTN)){
            this.saveConfigBtn.setEnabled(true);
            this.saveConfigBtn.setBackgroundColor(getResources().getColor(R.color.light_turquoise));
            return;
        }
        this.saveConfigBtn.setEnabled(false);
        this.saveConfigBtn.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    public void setOnValueChangeListener(EditText valueEt){
        valueEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                changeSaveBtnStatus(ENABLE_BTN);
            }
        });
    }

    public void setOnValuesChangeListeners(){
        setOnValueChangeListener(brightnessEt);
        setOnValueChangeListener(contrastEt);
        setOnValueChangeListener(saturationEt);
        setOnValueChangeListener(gammaEt);
        setOnValueChangeListener(whiteBalanceEt);
        setOnValueChangeListener(exposureEt);
    }

    public void setHints(){
        this.brightnessEt.setHint(Integer.toString(MIN_BRIGHTNESS) + " - " + Integer.toString(MAX_BRIGHTNESS));
        this.contrastEt.setHint(Integer.toString(MIN_CONTRAST) + " - " + Integer.toString(MAX_CONTRAST));
        this.saturationEt.setHint(Integer.toString(MIN_SATURATION) + " - " + Integer.toString(MAX_SATURATION));
        this.gammaEt.setHint(Integer.toString(MIN_GAMMA) + " - " + Integer.toString(MAX_GAMMA));
        this.whiteBalanceEt.setHint(Integer.toString(MIN_WHITE_BALANCE) + " - " + Integer.toString(MAX_WHITE_BALANCE));
        this.exposureEt.setHint(Integer.toString(MIN_EXPOSURE) + " - " + Integer.toString(MAX_EXPOSURE));
    }

    public void setSaveConfigBtnListener(){
        saveConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSave()){
                    saveConfig();
                }
            }
        });
    }

    public boolean areEmptyFields(){
        int brightnessLen = brightnessEt.getText().toString().trim().length();
        int contrastLen = contrastEt.getText().toString().trim().length();
        int saturationLen = saturationEt.getText().toString().trim().length();
        int gammaLen = gammaEt.getText().toString().trim().length();
        int whiteBalanceLen = whiteBalanceEt.getText().toString().trim().length();
        int exposureLen = exposureEt.getText().toString().trim().length();
        if(brightnessLen < 1 || contrastLen < 1 || saturationLen < 1 || gammaLen < 1 ||
                whiteBalanceLen < 1 || exposureLen < 1){
            return true;
        }
        return false;
    }

    public boolean isValidSave(){
        if(areEmptyFields()){
            return false;
        }
        return true;
    }

    public void saveConfig(){
        try{
            int brightness = Integer.parseInt(brightnessEt.getText().toString().trim());
            int contrast = Integer.parseInt(contrastEt.getText().toString().trim());
            int saturation = Integer.parseInt(saturationEt.getText().toString().trim());
            int gamma = Integer.parseInt(gammaEt.getText().toString().trim());
            int whiteBalance = Integer.parseInt(whiteBalanceEt.getText().toString().trim());
            int exposure = Integer.parseInt(exposureEt.getText().toString().trim());
            Util.storeConfigValue(getApplicationContext(), Constants.BRIGHTNESS_STORED_KEY, brightness);
            Util.storeConfigValue(getApplicationContext(), Constants.CONTRAST_STORED_KEY, contrast);
            Util.storeConfigValue(getApplicationContext(), Constants.SATURATION_STORED_KEY, saturation);
            Util.storeConfigValue(getApplicationContext(), Constants.GAMMA_STORED_KEY, gamma);
            Util.storeConfigValue(getApplicationContext(), Constants.WHITE_BALANCE_STORED_KEY, whiteBalance);
            Util.storeConfigValue(getApplicationContext(), Constants.EXPOSURE_STORED_KEY, exposure);
            changeSaveBtnStatus(DISABLE_BTN);
        } catch (Exception e){

        }
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
                        String photo_name = socket.takePhoto(); // was not before --- SEND CAMERA PARAMETERS HERE
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
