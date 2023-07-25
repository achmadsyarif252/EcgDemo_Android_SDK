package com.benefm.ecgdemo;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestLock {
    public synchronized void test() {
        try {
            Log.d("test", "3"+Thread.currentThread().getName() + "开始:" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Thread.sleep(2000);
            Log.d("test", "3"+Thread.currentThread().getName() + "结束:" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
