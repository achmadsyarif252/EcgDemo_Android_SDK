package com.benefm.ecgdemo.wifi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.ACache;
import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.WifiCmdResponse;
import com.benefm.ecgdemo.R;
import com.benefm.ecgdemo.util.ToastUitl;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Benefm on 2017/5/22 0022.
 */

public class WifiOverActivity extends AppCompatActivity {
    public static final String TYPE = "TYPE";

    private Button btnLogin;

    private Dialog conndialog;



    private TextView tv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_wifi_over);



        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        String s = ACache.get(this).getAsString("wifi");;

        tv = (TextView) findViewById(R.id.tv);
        tv.setText(getString(R.string.ss184) + " " + s + " " + getString(R.string.ss185));
    }


    void showDialog() {

        if (conndialog == null) {
            conndialog = new CBDialogBuilder(this, R.layout.layout_custom_dialog_layout_dark2, 0.8f)
                    .setTouchOutSideCancelable(false)
                    .showCancelButton(false)
                    .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                    .setTitle(getString(R.string.ss168))
                    .setMessageTextColor(Color.parseColor("#333333"))
                    .setCancelable(true)
                    .setMessage(getString(R.string.ss169))
                    .setConfirmButtonText("OK")
                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                    .create();
        }

        conndialog.findViewById(R.id.dialog_posi_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conndialog.dismiss();
                if (!DevManager.getInstance().isBindMac()) {
                    ToastUitl.showToast(WifiOverActivity.this, getString(R.string.ss143));
                    return;
                }

                if (!DevManager.getInstance().getDeviceConnState()) {
                    ToastUitl.showToast(WifiOverActivity.this, getString(R.string.ss151));
                    return;
                }

//                DevManager.getInstance().sendUnWifiConn();
                ACache.get(WifiOverActivity.this).put("wifi", "");


                Intent kk = new Intent(WifiOverActivity.this, WifiTipActivity.class);
                startActivity(kk);
                finish();

            }
        });

        conndialog.findViewById(R.id.dialog_posi_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conndialog.dismiss();
            }
        });
        conndialog.show();
        Window window = conndialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0f;
        window.setAttributes(lp);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiCmdResponse(WifiCmdResponse event) {
        if ((event.data[2] & 0xff) == 0xb4) {

            if ((event.data[4] & 0xff) == 0xa0) {


            } else {
//                ToastUitl.showToastCenterLong(WifiOverActivity.this,"断开失败，请重试");
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
