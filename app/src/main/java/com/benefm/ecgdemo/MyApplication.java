package com.benefm.ecgdemo;

import android.app.Application;

import com.befem.sdk.ecg.device.DevManager;

/**
 * Created by Benefm on 2017/5/18 0018.
 */

public class MyApplication extends Application {


    public static MyApplication sInstance;


    public static MyApplication getInstance() {
        return sInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DevManager.init(this);


    }


}
