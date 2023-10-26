package com.miotlink.android.bluetooth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.utils.DateUtil;
import com.miotlink.ble.listener.SmartNotifyCMBListener;
import com.miotlink.ble.model.BleModelDevice;

import java.util.Date;

public class BleControllerTestActivity extends BaseActivity implements View.OnClickListener,  SmartNotifyCMBListener {

    private Button startBtn;
    private TextView cleanTv;
    private TextView dataRecevierTv;
    private BleModelDevice bleModelDevice = null;

    public static void startIntent(Context mContext, Bundle bundle){
        Intent intent=new Intent(mContext,BleControllerTestActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
    @Override
    public void initView(){
        bleModelDevice=getIntent().getParcelableExtra("device");
        startBtn=findViewById(R.id.send_ble_btn);
        cleanTv=findViewById(R.id.search_tv);
        dataRecevierTv=findViewById(R.id.test_ble_tv);
        startBtn.setOnClickListener(this);
        cleanTv.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_pro_test_ble;
    }

    @Override
    public void initData()  {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MiotSmartBluetoothSDK.getInstance().regirster(this);

    }
    @Override
    protected void onPause() {
        super.onPause();
        MiotSmartBluetoothSDK.getInstance().unRegirster(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_ble_btn:
                if (bleModelDevice!=null){
                    showProgressDialog();
                    handler.sendEmptyMessageDelayed(1010,10*1000);
                    MiotSmartBluetoothSDK.getInstance().testPro(bleModelDevice.getMacAddress());
                }
                break;
            case R.id.search_tv:
                stringBuffer.delete(0,stringBuffer.length());
                dataRecevierTv.setText("");
                break;
        }
    }


    @Override
    public void onReceiver(String macCode, String data) {
        if (TextUtils.equals(macCode, bleModelDevice.getBleAddress())) {
            Log.e(macCode,data);
            Message message = new Message();
            message.obj = data;
            message.what = 1001;
            handler.sendMessage(message);

        }
    }

    @Override
    public void notify(String macCode, int funCode, int errorCode) throws Exception {
        if (TextUtils.equals(macCode, bleModelDevice.getBleAddress())) {
            Log.e(macCode,errorCode+"");
            Message message = new Message();
            message.obj = errorCode;
            message.what = funCode;
            handler.sendMessage(message);

        }
    }

    StringBuffer stringBuffer=new StringBuffer();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    cancelProgressDialog();
                    String data = msg.obj.toString();
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = JSON.parseObject(data);
                        if (jsonObject != null) {
                            String code = jsonObject.getString("code");
                            if (!TextUtils.isEmpty(code)) {
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                if (TextUtils.equals("productTest",code)){
                                    String value=jsonObjectData.getString("value");
                                    stringBuffer.append(DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI));
                                    stringBuffer.append(":").append("\n").append(value);
                                    stringBuffer.append("\n");
                                    dataRecevierTv.setText(stringBuffer.toString());
                                }
                            }
                        }
                    }
                    break;

                case 0x25:
                    handler.removeMessages(1010);
                    cancelProgressDialog();
                    int error = (int)msg.obj;
                    Toast.makeText(mContext,error==0?"产测开启成功":"产测开启失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1010:
                    handler.removeMessages(1010);
                    cancelProgressDialog();
                    Toast.makeText(mContext,"产测开启超时",Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };
}
