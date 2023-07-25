package com.benefm.ecgdemo.wifi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.ACache;
import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.WifiCmdResponse;
import com.benefm.ecgdemo.R;
import com.benefm.ecgdemo.util.ToastUitl;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Benefm on 2017/5/22 0022.
 */

public class WifiConnActivity extends AppCompatActivity {


    private TextView name;
    private TextView name1;
    private EditText et;
    private Button btnLogin;
    private KProgressHUD kProgressHUD;
    String s;
    private CheckBox ivShow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connnn);

        EventBus.getDefault().register(this);
        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.ss150))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setWindowColor(Color.parseColor("#5BA2D6"))
                .setDimAmount(0.5f);
        name = (TextView) findViewById(R.id.name);
        name1 = (TextView) findViewById(R.id.name1);

        s = getIntent().getStringExtra("ssid");
        name.setText(s);
        name1.setText(getString(R.string.ss184) + " " + s + " " + getString(R.string.ss185));


        ivShow = (CheckBox) findViewById(R.id.ivShow);
        ivShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //如果选中，显示密码
                    et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });


        et = (EditText) findViewById(R.id.et);
        if (!TextUtils.isEmpty(getWifiPass(s))) {
            et.setText(getWifiPass(s));
        }

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                doReSett();

            }
        });

    }


    public String getWifiPass(String ssid) {
        return ACache.get(this).getAsString(ssid);
    }

    void doReSett() {
        if (!DevManager.getInstance().isBindMac()) {
            ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss143));
            return;
        }

        if (!DevManager.getInstance().getDeviceConnState()) {
            ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss151));
            return;
        }


        if (TextUtils.isEmpty(et.getText().toString())) {

            Toast.makeText(WifiConnActivity.this, getString(R.string.ss163), Toast.LENGTH_SHORT).show();
            return;
        }
        if (et.getText().toString().length() < 8) {
            Toast.makeText(WifiConnActivity.this, getString(R.string.ss164), Toast.LENGTH_SHORT).show();
            return;
        }

      /*  Intent intent=new Intent(WifiConnActivity.this, WifiConnActivity.class);
        intent.putExtra("pass",etPassword.getText().toString());
        intent.putExtra("name",etPhone.getText().toString());
        WifiConnActivity.this.startActivityForResult(intent, 100);*/

        onlyOnce = true;
        btnLogin.setEnabled(false);
        kProgressHUD.show();
        new Thread() {
            @Override
            public void run() {

                DevManager.getInstance().sendWifiSSID(s);
                SystemClock.sleep(1000);
                DevManager.getInstance().sendWifiPass(et.getText().toString());
                SystemClock.sleep(1000);
                DevManager.getInstance().sendWifiConn();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (WifiConnActivity.this) {

                                    if (!isOk && onlyOnce) {
                                        onlyOnce = false;
                                        kProgressHUD.dismiss();
                                        btnLogin.setEnabled(true);
                                        ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss152));
                                    }
                                }

                            }
                        });

                    }
                }, 50 * 1000);

            }
        }.start();
    }

    boolean onlyOnce = true;
    private boolean isOk = false;
    long time = 0;

    public void saveWifiTip(String ssid) {
        ACache.get(this).put("wifi", ssid);
    }

    public  void saveWifiPass(String ssid, String pass) {
        ACache.get(this).put(ssid, pass);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiCmdResponse(WifiCmdResponse event) {


        if ((event.data[2] & 0xff) == 0xb3) {

            synchronized (WifiConnActivity.this) {

                if (System.currentTimeMillis() - time > 15 * 1000 && onlyOnce) {
                    onlyOnce = false;
                    time = System.currentTimeMillis();
                    kProgressHUD.dismiss();
                    btnLogin.setEnabled(true);

                    if ((event.data[4] & 0xff) == 0xa0) {
                        isOk = true;
                        saveWifiTip(s);
                        saveWifiPass(s, et.getText().toString());

                        WifiConnActivity.this.startActivity(new Intent(WifiConnActivity.this, WifiOverActivity.class));
                        finish();
                    } else if ((event.data[4] & 0xff) == 0xa6) {
                        ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss165));
                /* setResult(-101);
                finish();*/
                    } else if ((event.data[4] & 0xff) == 0xa5) {
               /* setResult(-100);
                finish();*/
                        ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss166));
                    } else {
                /*setResult(-102);
                finish();*/
                        ToastUitl.showToast(WifiConnActivity.this, getString(R.string.ss167));
                    }

                }
            }
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -100) {
            showDialog(msg);
        } else  if (resultCode == -101){
            showDialog(msg1);
        } else  if (resultCode == -102){
            showDialog(msg2);
        }else  if (resultCode == -103){
            finish();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private Dialog conndialog;

    void showDialog(String msg) {

        if (conndialog == null) {
            conndialog = new CBDialogBuilder(this, R.layout.layout_custom_dialog_layout_dark1, 0.8f)
                    .setTouchOutSideCancelable(false)
                    .showCancelButton(false)
                    .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                    .setTitle(null)
                    .setMessageTextColor(Color.parseColor("#333333"))
                    .setCancelable(true)
                    .setMessage(msg)
                    .setConfirmButtonText(getString(R.string.ss119))
                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                    .create();
        }


        conndialog.findViewById(R.id.dialog_posi_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conndialog.dismiss();
//                doReSett();
            }
        });
        conndialog.show();
        Window window = conndialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.3f;
        window.setAttributes(lp);
    }


}
