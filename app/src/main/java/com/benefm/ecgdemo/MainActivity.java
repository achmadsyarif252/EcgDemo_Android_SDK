package com.benefm.ecgdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.WifiCmdResponse;
import com.benefm.ecgdemo.util.permissions.ActivityPermissionsListener;
import com.benefm.ecgdemo.util.permissions.PermissionsManager;
import com.benefm.ecgdemo.wifi.WifiTipActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private TextView tvh;
    private Button btn;

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn22;
    private Button btn23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        btn = findViewById(R.id.btn);
        btn22 = findViewById(R.id.btn22);
        btn23 = findViewById(R.id.btn23);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DevManager.getInstance().isBindMac()) {
                    if (!DevManager.getInstance().getDeviceConnState()) {

                        btn.setEnabled(false);
                        connect();


                    }
                } else {
                    tvh.append("请先绑定设备...\n");
                }
            }
        });

        btn22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DevManager.getInstance().isBindMac()) {
                    if (DevManager.getInstance().getDeviceConnState()) {

                        //需要增加超时判断，设备有可能未响应
                        DevManager.getInstance().getWifiConn();//获取wifi配置状态


                    }
                } else {
                    tvh.append("请先绑定设备...\n");
                }
            }
        });
        btn23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DevManager.getInstance().isBindMac()) {
                    if (DevManager.getInstance().getDeviceConnState()) {

                        //需要增加超时判断，设备有可能未响应
                        DevManager.getInstance().getWifiName();//获取wifi底座连接的wifi名字

                    }
                } else {
                    tvh.append("请先绑定设备...\n");
                }
            }
        });


        btn3 = (Button) findViewById(R.id.btn3);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);


        btn4 = (Button) findViewById(R.id.btn4);

        btn.setEnabled(false);
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn22.setEnabled(false);
        btn23.setEnabled(false);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EcgActivity.class));
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LeadStateActivity.class));
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HolterActivity.class));
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WifiTipActivity.class));
            }
        });

        tvh = (TextView) findViewById(R.id.tvh);


        if (DevManager.getInstance().isBindMac()) {
            if (!DevManager.getInstance().getDeviceConnState()) {
//                startService(new Intent(this, DevService.class));
                connect();
                tvh.append("正在尝试连接,请稍后...\n");
            }

        } else {
            tvh.append("未绑定设备...\n");
        }


        DevManager.getInstance().setOnDeviceConnStateListener(new DevManager.OnDeviceConnStateListener() {
            @Override
            public void onDeviceNotFound() {
                tvh.append("未找到设备\n");
                btn.setEnabled(true);
            }

            @Override
            public void onDeviceConnSucc() {
                tvh.append("连接成功\n");
                btn1.setEnabled(true);
                btn2.setEnabled(true);
                btn3.setEnabled(true);
                btn4.setEnabled(true);
                btn22.setEnabled(true);
                btn23.setEnabled(true);
            }

            @Override
            public void onDeviceConnFail() {
                tvh.append("连接失败\n");
                btn.setEnabled(true);
                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);
                btn4.setEnabled(false);
                btn22.setEnabled(false);
                btn23.setEnabled(false);

            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiCmdResponse(WifiCmdResponse event) {


        if ((event.data[2] & 0xff) == 0xb0) {
            if ((event.data[4] & 0xff) == 0xa0) {
//              wifi底座wifi连接状态：成功
                tvh.append(" wifi底座wifi状态：连接成功\n");
            } else {
//                wifi底座wifi连接状态：连接失败
                tvh.append("wifi底座wifi状态：连接失败\n");
            }
        }

        //wifi名字
        if ((event.data[2] & 0xff) == 0xb5) {

            if ((event.data[4] & 0xff) == 0x01) {
                int lengh = (event.data[3] & 0xff) - 1;
                byte[] data = new byte[lengh];
                for (int i = 0; i < data.length; i++) {
                    data[i] = event.data[5 + i];
                }


                String wifiname = new String(data);
                tvh.append(wifiname + "\n");
            }

        }
    }

    void connect() {
        //扫码绑定
        PermissionsManager.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, new ActivityPermissionsListener() {
            @Override
            public void onGranted(Activity activity1) {
                // Have permission, do the thing!
                DevManager.getInstance().connect();
            }

            @Override
            public void onDenied(Activity activity1) {
                Toast.makeText(MainActivity.this, getString(R.string.ss145), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //动态授权所必须
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.onRequestPermissionsResult(this, grantResults);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //关闭设备
        DevManager.getInstance().close();

        //解绑后，清除本地记录，下次需要重新扫描mac地址
        DevManager.getInstance().unBindMac();

    }
}
