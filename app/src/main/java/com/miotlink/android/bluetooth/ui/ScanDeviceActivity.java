package com.miotlink.android.bluetooth.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.adapter.ScanDeviceAdapter;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.view.RadarView;
import com.miotlink.ble.Ble;
import com.miotlink.ble.callback.BleScanCallback;
import com.miotlink.ble.callback.BleStatusCallback;
import com.miotlink.ble.listener.ILinkBlueScanCallBack;
import com.miotlink.ble.listener.SmartNotifyListener;
import com.miotlink.ble.model.BleEntityData;
import com.miotlink.ble.model.BleModelDevice;
import com.miotlink.ble.model.BluetoothDeviceStore;
import com.miotlink.ble.utils.Utils;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public
class ScanDeviceActivity extends BaseActivity implements ILinkBlueScanCallBack {

    private RecyclerView recyclerView;
    private ScanDeviceAdapter scanDeviceAdapter=null;
    private BluetoothDeviceStore bluetoothDeviceStore=new BluetoothDeviceStore();

    private TextView textView;

    private RadarView radarView=null;
    @Override
    public void initView() throws Exception {

        radarView=findViewById(R.id.radar);
        recyclerView=findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        scanDeviceAdapter=new ScanDeviceAdapter(R.layout.item_scan_device);
        recyclerView.getItemAnimator().setChangeDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);
        recyclerView.setAdapter(scanDeviceAdapter);
        initBleStatus();

        scanDeviceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BleModelDevice item = (BleModelDevice)adapter.getItem(position);
                Bundle bundle=new Bundle();
                bundle.putString("macCode",item.getMacAddress());
                DeviceSmartConfigActivity.startIntent(mContext,bundle);
//                mContext.startActivity(new Intent(mContext,DeviceSmartConfigActivity.class));
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_sacn_device;
    }

    private void initBleStatus() {


    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothDeviceStore.clear();
        scanDeviceAdapter.setNewInstance(bluetoothDeviceStore.getDeviceList());
        checkGpsStatus();
        if (radarView!=null){
            radarView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (radarView!=null){
            radarView.stop();
        }

        MiotSmartBluetoothSDK.getInstance().onStopScan();
    }

    private void checkGpsStatus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Utils.isGpsOpen(mContext)){
            new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage("为了更精确的扫描到Bluetooth设备,请打开GPS定位")
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,1001);
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        }else {

            MiotSmartBluetoothSDK.getInstance().startScan(this::onScanDevice);
        }
    }




    @Override
    public void onScanDevice(BleModelDevice bleModelDevice) throws Exception {

        if (!TextUtils.isEmpty(bleModelDevice.getBleName())){
                if (!bluetoothDeviceStore.getDeviceMap().containsKey(bleModelDevice.getBleAddress())){
                    bluetoothDeviceStore.addDevice(bleModelDevice);
                    scanDeviceAdapter.setNewInstance(bluetoothDeviceStore.getDeviceList());
                }
            }
    }

}
