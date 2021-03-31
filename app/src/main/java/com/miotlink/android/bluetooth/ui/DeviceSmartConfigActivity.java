package com.miotlink.android.bluetooth.ui;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.view.RadarView;
import com.miotlink.android.bluetooth.view.StatusBarUtils;
import com.miotlink.ble.listener.ILinkSmartConfigListener;

public class DeviceSmartConfigActivity extends BaseActivity {

    public static void startIntent(Context mContext,Bundle bundle){
        Intent intent=new Intent(mContext,DeviceSmartConfigActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private TextView routeNameTv;
    private EditText routePassEv;
    private Button startButton;

    private RelativeLayout relativeLayout;
    String routeName="";
    String routePass="";
    private boolean isStart=false;
    private String macCode="";
    private RadarView radarView=null;

    @Override
    public void initView() throws Exception {
        StatusBarUtils.setColor(mContext, getResources().getColor(R.color.white));
        StatusBarUtils.setTextDark(mContext,true);
        WifiManager wifiManager= (WifiManager) mContext.getSystemService(WIFI_SERVICE);

        macCode=getIntent().getStringExtra("macCode");
        routeNameTv=findViewById(R.id.route_name_tv);
        routePassEv=findViewById(R.id.route_password_tv);
        startButton=findViewById(R.id.start_smart);
        relativeLayout=findViewById(R.id.scan_blue_rl);
        radarView=findViewById(R.id.radar);
        if (wifiManager!=null){
            if (wifiManager.isWifiEnabled()){
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo!=null){
                    String ssid = connectionInfo.getSSID();
                    if (!TextUtils.isEmpty(ssid)) {
                        ssid=ssid.replaceAll("\"","");
                        routeNameTv.setText(ssid);
                    }
                }
            }
        }
        startButton.setOnClickListener((view)->{
            if (isStart){
                Toast.makeText(mContext,"配网中",Toast.LENGTH_SHORT).show();
                return;
            }

            routeName=routeNameTv.getText().toString();
            routePass=routePassEv.getText().toString();
            if (TextUtils.isEmpty(routeName)){
                Toast.makeText(mContext,"输入路由器账户",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(routePass)){
                Toast.makeText(mContext,"输入路由器密码",Toast.LENGTH_SHORT).show();
                return;
            }
            isStart=true;
            radarView.start();
            relativeLayout.setVisibility(View.VISIBLE);
            MiotSmartBluetoothSDK.getInstance().startSmartConfig(macCode, routeName, routePass, 60, new ILinkSmartConfigListener() {
                @Override
                public void onLinkSmartConfigListener(int i, String s, String s1) throws Exception {
                    isStart=false;

                    MiotSmartBluetoothSDK.getInstance().onDisConnect();
                    if (i==7015){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                radarView.stop();
                                relativeLayout.setVisibility(View.GONE);
                                Toast.makeText(mContext, "配网成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                radarView.stop();
                                relativeLayout.setVisibility(View.GONE);
                                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onLinkSmartConfigTimeOut(int i, String s) throws Exception {
                    isStart=false;
                    radarView.stop();
                    relativeLayout.setVisibility(View.GONE);
                    MiotSmartBluetoothSDK.getInstance().onDisConnect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            radarView.stop();
                            relativeLayout.setVisibility(View.GONE);
                            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int getContentView() {
        return R.layout.activity_wifi_device;
    }
}
