package com.miotlink.android.bluetooth.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.adapter.ScanDeviceAdapter;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.view.RadarView;
import com.miotlink.ble.BleLog;
import com.miotlink.ble.listener.ILinkBlueScanCallBack;
import com.miotlink.ble.model.BleModelDevice;
import com.miotlink.ble.model.BluetoothDeviceStore;
import com.miotlink.ble.utils.Utils;
import com.miotlink.protocol.BluetoothProtocol;
import com.miotlink.protocol.BluetoothProtocolImpl;
import com.miotlink.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

public
class ScanDeviceActivity extends BaseActivity implements ILinkBlueScanCallBack {

    private RecyclerView recyclerView;
    private ScanDeviceAdapter scanDeviceAdapter=null;


    private TextView textView;

    private List<BleModelDevice> list=new ArrayList<>();

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


        scanDeviceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BleModelDevice item = (BleModelDevice)adapter.getItem(position);
                Bundle bundle=new Bundle();
                bundle.putParcelable("device",item);
                bundle.putString("macCode",item.getMacAddress());
                DeviceInfoActivity.startIntent(mContext,bundle);
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_sacn_device;
    }



    @Override
    public void initData() throws Exception {

    }

    @Override
    protected void onResume() {
        super.onResume();
       list.clear();
        scanDeviceAdapter.setNewInstance(list);
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
        list.add(bleModelDevice);
        scanDeviceAdapter.setNewInstance(list);
        scanDeviceAdapter.notifyDataSetChanged();
    }

}
