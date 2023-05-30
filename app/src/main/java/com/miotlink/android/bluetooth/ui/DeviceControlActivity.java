package com.miotlink.android.bluetooth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.view.LoadingViewDialog;
import com.miotlink.ble.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.ble.listener.SmartNotifyOTAListener;
import com.miotlink.ble.listener.SmartNotifyUartDataListener;
import com.miotlink.ble.model.BleModelDevice;

public class DeviceControlActivity extends BaseActivity implements  View.OnClickListener, SmartNotifyDeviceConnectListener, SmartNotifyUartDataListener {


    private TextView ble_state_tv;

    private Button send_hex_btn,get_wifiinfo_btn,ota_btn;

    private EditText send_ble_hex_et;

    private TextView receiver_ble_hex;

    private BleModelDevice bleModelDevice=null;

    private boolean isconnect=false;

    private LoadingViewDialog loadingViewDialog=null;

    private String uartdata="";

    public static void startIntent(Context mContext, Bundle bundle){
        Intent intent=new Intent(mContext,DeviceControlActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
    @Override
    public void initView() throws Exception {
        loadingViewDialog=new LoadingViewDialog(mContext);
        ble_state_tv=findViewById(R.id.ble_state_tv);
        send_hex_btn=findViewById(R.id.send_hex_btn);
        get_wifiinfo_btn=findViewById(R.id.get_wifiinfo_btn);
        ota_btn=findViewById(R.id.ota_btn);
        send_ble_hex_et=findViewById(R.id.send_ble_hex_et);
        receiver_ble_hex=findViewById(R.id.receiver_ble_hex);
        send_hex_btn.setOnClickListener(this);
        get_wifiinfo_btn.setOnClickListener(this);
        ota_btn.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_ble_control;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initData() throws Exception {
        bleModelDevice=getIntent().getParcelableExtra("device");
        if (bleModelDevice!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingViewDialog.show();
                }
            },1000);

            MiotSmartBluetoothSDK.getInstance().connect(bleModelDevice.getMacAddress(),this);
            MiotSmartBluetoothSDK.getInstance().setSmartNotifyUartDataListener(this);
        }
    }

    @Override
    public void notifyDeviceConnectListener(int code, BleModelDevice bleModelDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (code==200){
                    if (loadingViewDialog.isShow()){
                        loadingViewDialog.cancel();
                    }
                    isconnect=true;
                    ble_state_tv.setText("断开");
                }else {
                    if (!loadingViewDialog.isShow()){
                        loadingViewDialog.show();
                    }
                    isconnect=false;
                    ble_state_tv.setText("连接");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_hex_btn:
                if (TextUtils.isEmpty(send_ble_hex_et.getText())){
                    Toast.makeText(mContext,"请输入串口数据",Toast.LENGTH_SHORT).show();
                    return;
                }
                MiotSmartBluetoothSDK.getInstance().setSmartNotifyUartDataListener(this);
                String s = send_ble_hex_et.getText().toString();
                MiotSmartBluetoothSDK.getInstance().sendUartData(bleModelDevice.getMacAddress(),s.getBytes(),this);
                break;
            case R.id.ota_btn:
                MiotSmartBluetoothSDK.getInstance().setSmartNotifyOTAListener(new SmartNotifyOTAListener() {
                    @Override
                    public void notifyOtaData(String data) {

                    }
                });
                MiotSmartBluetoothSDK.getInstance().startOtaOnline(bleModelDevice.getMacAddress(), 0, "", "53D161924A09210C8735867705E01E50", "https://yair.51miaomiao.cn/miotlink/thirdFactory/upgrade", 0);
                break;
            case R.id.get_wifiinfo_btn:
                MiotSmartBluetoothSDK.getInstance().getDeviceInfo(bleModelDevice.getMacAddress());
                break;
        }
    }


    @Override
    public void notifyDeviceWifiInfo(String macCode, String uart) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                receiver_ble_hex.setText(uart);
            }
        });
    }

    @Override
    public void notifyUartData(String macCode, String uart) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                uartdata+=uart+"\n";
                receiver_ble_hex.setText(uartdata);
            }
        });
    }

    @Override
    public void notifyFail(int errorCode, String errorMessage) {

    }

    SmartNotifyUartDataListener smartNotifyUartDataListener=new SmartNotifyUartDataListener() {
        @Override
        public void notifyUartData(String macCode, String uart) {

        }

        @Override
        public void notifyDeviceWifiInfo(String macCode, String uart) {

        }

        @Override
        public void notifyFail(int errorCode, String errorMessage) {

        }
    }

}
