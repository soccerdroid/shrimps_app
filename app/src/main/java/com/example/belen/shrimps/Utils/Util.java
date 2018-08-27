package com.example.belen.shrimps.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Util {
    public static void storeConfigValue(Context context, String key, int value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getConfigValue(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.contains(key)){
            return sharedPref.getInt(key, getDefaultConfigValue(key));
        }
        return getDefaultConfigValue(key);
    }

    public static int getDefaultConfigValue(String key){
        switch (key){
            case Constants.BRIGHTNESS_STORED_KEY:
                return Constants.BRIGHTNESS_DEFAULT_VALUE;
            case Constants.CONTRAST_STORED_KEY:
                return Constants.CONTRAST_DEFAULT_VALUE;
            case Constants.SATURATION_STORED_KEY:
                return Constants.SATURATION_DEFAULT_VALUE;
            case Constants.GAMMA_STORED_KEY:
                return Constants.GAMMA_DEFAULT_VALUE;
            case Constants.WHITE_BALANCE_STORED_KEY:
                return Constants.WHITE_BALANCE_DEFAULT_VALUE;
            case Constants.EXPOSURE_STORED_KEY:
                return Constants.EXPOSURE_DEFAULT_VALUE;
            default:
                return Constants.BRIGHTNESS_DEFAULT_VALUE;
        }
    }

    public String getCameraParameters(Context ctx){
        String cameraParameters = "";
        String brightnessVal = Integer.toString(getConfigValue(ctx, Constants.BRIGHTNESS_STORED_KEY));
        String contrastVal = Integer.toString(getConfigValue(ctx, Constants.CONTRAST_STORED_KEY));
        String saturationVal = Integer.toString(getConfigValue(ctx, Constants.SATURATION_STORED_KEY));
        String gammaVal = Integer.toString(getConfigValue(ctx, Constants.GAMMA_STORED_KEY));
        String whiteBalanceVal = Integer.toString(getConfigValue(ctx, Constants.WHITE_BALANCE_STORED_KEY));
        String exposureVal = Integer.toString(getConfigValue(ctx, Constants.EXPOSURE_STORED_KEY));

        String brightnessStr = "brightness";
        String contrastStr = "contrast";
        String saturationStr = "saturation";
        String gammaStr = "gamma";
        String whiteBalanceStr = "white_balance_temperature";
        String exposureStr = "exposure";

        String setParam = "--set ";
        cameraParameters = setParam + brightnessStr + "=" + brightnessVal;
        cameraParameters = cameraParameters + " " + setParam + contrastStr + "=" + contrastVal;
        cameraParameters = cameraParameters + " " + setParam + saturationStr + "=" + saturationVal;
        cameraParameters = cameraParameters + " " + setParam + gammaStr + "=" + gammaVal;
        cameraParameters = cameraParameters + " " + setParam + whiteBalanceStr + "=" + whiteBalanceVal;
        cameraParameters = cameraParameters + " " + setParam + exposureStr + "=" + exposureVal;
        return cameraParameters;
    }
}
