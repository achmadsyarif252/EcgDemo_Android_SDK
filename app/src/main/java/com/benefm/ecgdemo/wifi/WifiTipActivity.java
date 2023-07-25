package com.benefm.ecgdemo.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.DevManager;
import com.benefm.ecgdemo.R;
import com.benefm.ecgdemo.util.ToastUitl;

/**
 * Created by Benefm on 2017/5/22 0022.
 */

public class WifiTipActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_wifi_tip);


        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DevManager.getInstance().isBindMac()) {
                    ToastUitl.showToast(WifiTipActivity.this, getString(R.string.ss143));
                    return;
                }

                if (!DevManager.getInstance().getDeviceConnState()) {
                    ToastUitl.showToast(WifiTipActivity.this, getString(R.string.ss151));
                    return;
                }

                startActivity(new Intent(WifiTipActivity.this, WifiSelectActivity.class));
                finish();


            }
        });



    }




    private Button btnLogin;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
