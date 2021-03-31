package com.miotlink.android.bluetooth.application;

import android.app.Application;

import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.ble.listener.SmartListener;

public class SmartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MiotSmartBluetoothSDK.setDebug(true);
        MiotSmartBluetoothSDK.getInstance().init(this, "", new SmartListener() {
            @Override
            public void onSmartListener(int i, String s, String s1) throws Exception {

            }
        });
    }
}
