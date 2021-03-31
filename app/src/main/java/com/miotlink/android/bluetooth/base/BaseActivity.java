package com.miotlink.android.bluetooth.base;

import android.content.Context;
import android.os.Bundle;


import com.miotlink.android.bluetooth.R;
import com.miotlink.android.bluetooth.view.StatusBarUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(getContentView());
            mContext=this;
            initView();
            StatusBarUtils.setColor(mContext, getResources().getColor(R.color.white));
            StatusBarUtils.setTextDark(mContext,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void initView()throws Exception;

    public abstract int getContentView();
}
