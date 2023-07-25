package com.benefm.ecgdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.DeviceConnState;
import com.befem.sdk.ecg.device.model.LeadStateData;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LeadStateActivity extends AppCompatActivity {


    private String mExtra;
    private TextView tvHeartRate;
    private RelativeLayout rlTimeSelect;
    private RelativeLayout rlGoWave;
    private TextView tvTimeRemain;
    private Button tvStart;
    private TextView tvHeartRateHint;


    private boolean isStart = false;
    private DownTimer mc;
    private int time = 0;


    private boolean isWarningOpen = false;

    private String warnUp;

    private String warnDown;
    private TextView tvSetting;
    private KProgressHUD kProgressHUD;
    private KProgressHUD kProgressHUD1;
    private TextView tvConn;
    private ImageView imConn;
    private ImageView ra;
    private ImageView la;
    private ImageView v1;
    private ImageView v2;
    private ImageView v3;
    private ImageView v4;
    private ImageView v5;
    private ImageView v6;
    private ImageView rl;
    private ImageView ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg_lead_off1);
        EventBus.getDefault().register(this);

        imConn = (ImageView) findViewById(R.id.imConn);



        tvConn = (TextView) findViewById(R.id.tvConn);
        ra = (ImageView) findViewById(R.id.ra);
        la = (ImageView) findViewById(R.id.la);
        v1 = (ImageView) findViewById(R.id.v1);
        v2 = (ImageView) findViewById(R.id.v2);
        v3 = (ImageView) findViewById(R.id.v3);
        v4 = (ImageView) findViewById(R.id.v4);
        v5 = (ImageView) findViewById(R.id.v5);
        v6 = (ImageView) findViewById(R.id.v6);
        rl = (ImageView) findViewById(R.id.rl);
        ll = (ImageView) findViewById(R.id.ll);

        if(DevManager.getInstance().getDeviceConnState()){
            tvConn.setText(getString(R.string.ss173));
            imConn.setImageResource(R.drawable.blecon);
        }
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceConnState(DeviceConnState event) {

        if (event.connState == 2) {
            tvConn.setText(getString(R.string.ss173));
            imConn.setImageResource(R.drawable.blecon);
        }

        if (event.connState == 1) {
            tvConn.setText(getString(R.string.ss172));
            imConn.setImageResource(R.drawable.blenot);
        }
    }


    private static byte getBitVale(byte b, int index) {
        if (index < 0 || index > 7) {
            throw new RuntimeException("下标越界");
        }
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array[7 - index];
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDaoLianData(LeadStateData event) {
        if (event != null) {

            int raV = getBitVale(event.low, 0);
            int laV = getBitVale(event.low, 1);
            int llV = getBitVale(event.low, 2);
            int v1V = getBitVale(event.low, 3);
            int v2V = getBitVale(event.low, 4);
            int v3V = getBitVale(event.low, 5);
            int v4V = getBitVale(event.low, 6);
            int v5V = getBitVale(event.low, 7);
            int v6V = getBitVale(event.high, 0);
            int rlV = getBitVale(event.high, 1);

            if(raV==1){
                ra.setImageResource(R.drawable.ra);
            }else{
                ra.setImageResource(R.drawable.cra);
            }
            if(laV==1){
                la.setImageResource(R.drawable.la);
            }else{
                la.setImageResource(R.drawable.cla);
            }
            if(llV==1){
                ll.setImageResource(R.drawable.ll);
            }else{
                ll.setImageResource(R.drawable.cll);
            }
            if(v1V==1){
                v1.setImageResource(R.drawable.v1);
            }else{
                v1.setImageResource(R.drawable.cv1);
            }
            if(v2V==1){
                v2.setImageResource(R.drawable.v2);
            }else{
                v2.setImageResource(R.drawable.cv2);
            }
            if(v3V==1){
                v3.setImageResource(R.drawable.v3);
            }else{
                v3.setImageResource(R.drawable.cv3);
            }
            if(v4V==1){
                v4.setImageResource(R.drawable.v4);
            }else{
                v4.setImageResource(R.drawable.cv4);
            }
            if(v5V==1){
                v5.setImageResource(R.drawable.v5);
            }else{
                v5.setImageResource(R.drawable.cv5);
            }
            if(v6V==1){
                v6.setImageResource(R.drawable.v6);
            }else{
                v6.setImageResource(R.drawable.cv6);
            }
            if(rlV==1){
                rl.setImageResource(R.drawable.rl);
            }else{
                rl.setImageResource(R.drawable.crl);
            }


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
