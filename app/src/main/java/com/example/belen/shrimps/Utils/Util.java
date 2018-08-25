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
        System.out.println("AFUERITA *********************");
        if(sharedPref.contains(key)){
            System.out.println("EN IF *********************");

            return sharedPref.getInt(key, getDefaultConfigValue(key));
        }
        return 0;
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
}
