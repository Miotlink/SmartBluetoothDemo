package com.miotlink.android.bluetooth.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;


import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.ble.Ble;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;


public class LoadingActivity extends BaseActivity {


    @Override
    public void initView() throws Exception {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                PermissionX.init(LoadingActivity.this).permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted){
                            if (MiotSmartBluetoothSDK.getInstance().checkPermissions()==3){
                                MiotSmartBluetoothSDK.getInstance().turnOnBlueToothNo();
                            }
                            mContext.startActivity(new Intent(mContext,ScanDeviceActivity.class));
                            finish();

                        }
                    }
                });
            }
        },3000);


    }

    @Override
    public int getContentView() {
        return R.layout.activity_loading;
    }


}
