package com.example.belen.shrimps;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;


public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Servidor", "Descargadas"};
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
       Fragment fragment =null;
        switch (position) {
            case 0:
                WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                if(wifiInfo!=null){
                    String wifi_name = wifiInfo.getSSID();
                    if(wifi_name.equalsIgnoreCase("\"Pi_AP\"")){
                        fragment = Fragment.instantiate(context, PageFragment.class.getName());
                        break;
                    }
                    else {
                        CharSequence text = "No está conectado a la red de la raspberry";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                else {
                    Toast toast = Toast.makeText(context, "No hay red wifi", Toast.LENGTH_SHORT);
                    toast.show();
                }

            case 1:
                fragment = Fragment.instantiate(context, OtherFragment.class.getName());
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }


}
