package com.miotlink.android.bluetooth.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.miotlink.MiotSmartBluetoothSDK;
import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.base.BaseActivity;
import com.miotlink.android.bluetooth.utils.DateUtil;
import com.miotlink.ble.listener.SmartNotifyCMBListener;
import com.miotlink.ble.model.BleModelDevice;

import java.util.Calendar;
import java.util.Date;

public class BleCheckControllerActivity extends BaseActivity implements View.OnClickListener, SmartNotifyCMBListener {

    private EditText mac_edit;

    private EditText sn_edit;

    private EditText cmei_edit;
    private TextView product_date_edit;
    private EditText type_edit;
    private EditText pro_edit;
    private EditText d_token_edit;
    private EditText pro_name_edit;
    private EditText pro_brand_edit;
    private EditText pro_model_edit;
    private EditText pro_power_edit;

    private TextView search_tv;
    private Button set_cmei_and_mac_btn;

    private String macCode = "";
    private String type = "";
    private String productToken = "";
    private String andlinkToken = "";
    private String cmei = "";
    private String sn = "";
    private String date = "";
    private String product = "";
    private String brand = "";
    private String model = "";
    private String power = "";



    private MyThread thread=null;



    private BleModelDevice bleModelDevice = null;

    public static void startIntent(Context mContext, Bundle bundle){
        Intent intent=new Intent(mContext,BleCheckControllerActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
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
    public void initView() {
        bleModelDevice = getIntent().getParcelableExtra("device");
        mac_edit = findViewById(R.id.mac_edit);
        sn_edit = findViewById(R.id.sn_edit);
        cmei_edit = findViewById(R.id.cmei_edit);
        product_date_edit = findViewById(R.id.product_date_edit);
        type_edit = findViewById(R.id.type_edit);
        pro_edit = findViewById(R.id.pro_edit);
        d_token_edit = findViewById(R.id.d_token_edit);
        pro_name_edit = findViewById(R.id.pro_name_edit);
        pro_brand_edit = findViewById(R.id.pro_brand_edit);
        pro_model_edit = findViewById(R.id.pro_model_edit);
        pro_power_edit = findViewById(R.id.pro_power_edit);
        set_cmei_and_mac_btn=findViewById(R.id.set_cmei_and_mac_btn);
        search_tv=findViewById(R.id.search_tv);
        set_cmei_and_mac_btn.setOnClickListener(this);
        search_tv.setOnClickListener(this);
        product_date_edit.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_sn_cmei_ble;
    }
    @Override
    public void initData() {
        if (bleModelDevice != null) {
            if (thread!=null){
                thread.interrupt();
                thread=null;
            }
            thread=new MyThread();
            thread.start();

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.set_cmei_and_mac_btn:
                new XPopup.Builder(mContext)
                        .isDarkTheme(false)
                        .hasShadowBg(true)
                        .customHostLifecycle(getLifecycle())
                        .moveUpToKeyboard(false)
                        .isDestroyOnDismiss(false)
                        .borderRadius(XPopupUtils.dp2px(mContext, 15))
                        .asBottomList("请选择设置数据", new String[]{"TOKEN", "SN/CMEI","厂家信息","MAC地址"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {

                                        if (position == 0) {
                                            type = type_edit.getText().toString();
                                            productToken = pro_edit.getText().toString();
                                            andlinkToken = d_token_edit.getText().toString();
                                            if(TextUtils.isEmpty(type)||TextUtils.isEmpty(productToken)||TextUtils.isEmpty(andlinkToken)){
                                                Toast.makeText(mContext,"传入参数不能为空",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MiotSmartBluetoothSDK.getInstance().setProduct(bleModelDevice.getMacAddress(),type,productToken,andlinkToken);
                                        } else if (position == 1) {
                                            sn = sn_edit.getText().toString();
                                            cmei = cmei_edit.getText().toString();
                                            date = product_date_edit.getText().toString();
                                            if(TextUtils.isEmpty(sn)||TextUtils.isEmpty(cmei)||TextUtils.isEmpty(date)){
                                                Toast.makeText(mContext,"传入参数不能为空",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MiotSmartBluetoothSDK.getInstance().setSnAndCmei(bleModelDevice.getMacAddress(),cmei,sn,date);
                                        }else if (position == 2) {
                                            product = pro_name_edit.getText().toString();
                                            brand = pro_brand_edit.getText().toString();
                                            model = pro_model_edit.getText().toString();
                                            power=pro_power_edit.getText().toString();
                                            if(TextUtils.isEmpty(product)||TextUtils.isEmpty(brand)||TextUtils.isEmpty(model)||TextUtils.isEmpty(power)){
                                                Toast.makeText(mContext,"传入参数不能为空",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MiotSmartBluetoothSDK.getInstance().setFactory(bleModelDevice.getMacAddress(),product,brand,model,power);
                                        }else if (position == 3) {
                                            macCode = mac_edit.getText().toString();
                                           if(TextUtils.isEmpty(macCode)){
                                               Toast.makeText(mContext,"请输入MAC地址",Toast.LENGTH_SHORT).show();
                                               return;
                                            }
                                            if(macCode.length()!=12){
                                                Toast.makeText(mContext,"MAC地址长度错误，请重新输入",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MiotSmartBluetoothSDK.getInstance().setMacCode(bleModelDevice.getMacAddress(),macCode);
                                        }
                                        showProgressDialog();
                                        handler.sendEmptyMessageDelayed(1010,10*1000);
                                    }
                                }).show();


                break;
            case R.id.search_tv:
                initData();
                showProgressDialog();
                break;
            case R.id.product_date_edit:
                dialogShow();
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
    public void notify(String macCode,int type,  int errorCode) {
        if (TextUtils.equals(macCode, bleModelDevice.getBleAddress())) {
            Message message = new Message();
            message.obj = errorCode;
            message.what = type;
            handler.sendMessage(message);
        }
    }
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
                                if (TextUtils.equals("product", code)) {
                                    type = jsonObjectData.getString("type");
                                    productToken = jsonObjectData.getString("productToken");
                                    andlinkToken = jsonObjectData.getString("andlinkToken");
                                    type_edit.setText(type);
                                    pro_edit.setText(productToken);
                                    d_token_edit.setText(andlinkToken);
                                } else if (TextUtils.equals("sn_cmei", code)) {
                                    cmei = jsonObjectData.getString("cmei");
                                    sn = jsonObjectData.getString("sn");
                                    date = jsonObjectData.getString("date");
                                    sn_edit.setText(sn);
                                    cmei_edit.setText(cmei);
                                    product_date_edit.setText(date);
                                } else if (TextUtils.equals("factory", code)) {
                                    product = jsonObjectData.getString("product");
                                    brand = jsonObjectData.getString("brand");
                                    model = jsonObjectData.getString("model");
                                    power = jsonObjectData.getString("power");
                                    pro_name_edit.setText(product);
                                    pro_brand_edit.setText(brand);
                                    pro_model_edit.setText(model);
                                    pro_power_edit.setText(power);
                                } else if (TextUtils.equals("mac", code)) {
                                    macCode = jsonObjectData.getString("macCode");
                                    if (!TextUtils.isEmpty(macCode)){
                                        mac_edit.setText(macCode.toUpperCase());
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 0x20:
                case 0x21:
                case 0x22:
                case 0x25:
                case 0x24:
                    handler.removeMessages(1010);
                    cancelProgressDialog();
                    int error = (int)msg.obj;
                    Toast.makeText(mContext,error==0?"参数设置成功":"参数设置失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1010:
                    cancelProgressDialog();
                    Toast.makeText(mContext,"参数设置超时",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public void dialogShow() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Calendar calendar = Calendar.getInstance();//日历类的实例化
                        calendar.set(year, month , dayOfMonth);//设置日历时间，月份必须减一
                        Date date = calendar.getTime(); // 从一个 Calendar 对象中获取 Date 对象
                        product_date_edit.setText(DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY_MM_DD));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                MiotSmartBluetoothSDK.getInstance().getProduct(bleModelDevice.getMacAddress());
                Thread.sleep(500);
                MiotSmartBluetoothSDK.getInstance().getSnAndCmei(bleModelDevice.getMacAddress());
                Thread.sleep(500);
                MiotSmartBluetoothSDK.getInstance().getDeviceFactory(bleModelDevice.getMacAddress());
                Thread.sleep(500);
                MiotSmartBluetoothSDK.getInstance().getDeviceMacCode(bleModelDevice.getMacAddress());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
