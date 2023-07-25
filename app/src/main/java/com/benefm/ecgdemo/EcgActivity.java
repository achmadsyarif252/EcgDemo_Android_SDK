package com.benefm.ecgdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.EcgWaveData;
import com.befem.sdk.ecg.device.model.HrBatteryLevelData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class EcgActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EXTRA = "param";
    public Vector<Integer> leads = new Vector<>();//导联过滤
    long lastTime = 0;
    Boolean isFirst = true;
    int N = 52;
    float[] hfdata = new float[N];
    int hfptr = 0;
    float hfy = 0;
    long lastTime2 = 0;
    boolean firstRead = true;
    Vector<Integer> hrArray = new Vector<>();
    Integer count = 0;
    private String mExtra;
    private TextView tvHeartRate;
    private Spinner spYaxis;
    private Spinner spSpeed;
    private TextView tvLeadSelect;
    private EcgSurfaceView ecgView;
    private View vBattyRemain;
    private ImageView ivCharge;
    private Spinner spSelect;
    private LinearLayout llContainer11;
    private LinearLayout llBatty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg_monitor);


        tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
        spYaxis = (Spinner) findViewById(R.id.sp_yaxis);
        spSpeed = (Spinner) findViewById(R.id.sp_speed);


        spSelect = (Spinner) findViewById(R.id.sp_select);
        ivCharge = (ImageView) findViewById(R.id.ivCharge);


        llBatty = (LinearLayout) findViewById(R.id.ll_batty);

        tvLeadSelect = (TextView) findViewById(R.id.tv_lead_select);
        vBattyRemain = findViewById(R.id.v_batty_remain);
        tvLeadSelect.setOnClickListener(this);
        tvLeadSelect.setVisibility(View.INVISIBLE);

        final String[] enhanceValue = getResources().getStringArray(R.array.ecg_yaxis);
        String[] enhance = new String[enhanceValue.length];
        for (int i = 0; i < enhanceValue.length; i++) {
            enhance[i] = enhanceValue[i] + "mm/mv";
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, R.id.tv_spinner, enhance);
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spYaxis.setAdapter(adapter1);


        final String[] xSpeedValue = getResources().getStringArray(R.array.ecg_speed);
        String[] xSpeed = new String[xSpeedValue.length];
        for (int i = 0; i < xSpeedValue.length; i++) {
            xSpeed[i] = xSpeedValue[i] + "mm/s";
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, R.id.tv_spinner, xSpeed);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spSpeed.setAdapter(adapter2);


        final String[] xSpeedValue1 = getResources().getStringArray(R.array.ecg_speed1);
        final String[] xSpeed1 = new String[xSpeedValue1.length];
        for (int i = 0; i < xSpeedValue1.length; i++) {
            xSpeed1[i] = xSpeedValue1[i];
        }
        ArrayAdapter<String> adapter21 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, R.id.tv_spinner, xSpeed1);
        adapter21.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spSelect.setAdapter(adapter21);

        spYaxis.setSelection(2);
        spSpeed.setSelection(1);
        spSelect.setSelection(1);
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ecgView.clearData();
                if (!isFirst) {
                    DevManager.getInstance().sendLeadSelectionCommand((position + 1));
                } else {
                    isFirst = false;
                }
                ecgView.labelText = xSpeed1[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spYaxis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ecgView.switchEnhance(Float.valueOf(enhanceValue[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ecgView.switchXspeed(Float.valueOf(xSpeedValue[position]));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ecgView = findViewById(R.id.ecg_view);

        DevManager.getInstance().setOnECGWaveDataListener(new DevManager.OnECGWaveDataListener() {
            @Override
            public void onECGWaveData(ArrayList<EcgWaveData> data) {
                ArrayList<EcgWaveData> data1 = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    EcgWaveData temp = data.get(i);
                    data1.add(new EcgWaveData(temp.type, lvbo(temp.value)));
                }
                ecgView.notifyData(data1);
            }
        });

        DevManager.getInstance().setOnHrBatteryLevelListener(new DevManager.OnHrBatteryLevelDataListener() {
            @Override
            public void onHrBatteryLevelData(HrBatteryLevelData event) {
                if (event.heartRate != -1) {

                    if (hrArray.size() == 10) {

                        Collections.sort(hrArray);
                        int dd = (hrArray.get(4) + hrArray.get(5)) / 2;
                        tvHeartRate.setText(String.valueOf(dd));
                        hrArray.set(count, event.heartRate);
                        count++;
                        if (count == 10) {
                            count = 0;
                        }
                    } else if (hrArray.size() < 10) {
                        hrArray.add(event.heartRate);
                    }
                }

                if (System.currentTimeMillis() - lastTime > 1000 * 60) {
                    if (event.batteryLevel < 2) {
                        event.batteryLevel = 2;
                    }
                    setBatteryLevel(event.batteryLevel);
                    Log.d("qwert", "onHRData: " + event.batteryLevel);
                    lastTime = System.currentTimeMillis();
                }

            }
        });


        DevManager.getInstance().setOnChargingStateListener(new DevManager.OnChargingStateListener() {
            @Override
            public void onChargingStateOn() {
                ivCharge.setVisibility(View.VISIBLE);
                llBatty.setVisibility(View.GONE);
            }

            @Override
            public void onChargingStateOff() {
                ivCharge.setVisibility(View.GONE);
                llBatty.setVisibility(View.VISIBLE);
                setBatteryLevel(0);
            }
        });

        DevManager.getInstance().setOnOneKeyAlertListener(new DevManager.OnOneKeyAlertListener() {
            @Override
            public void onOneKeyAlert() {
                //"收到一键报警"
            }
        });
    }

    float lvbo(Float dataIn) {

        int halfPtr;
        float out = 0;
        hfy = hfy + dataIn - hfdata[hfptr];
        halfPtr = hfptr - N / 2;

        if (halfPtr < 0) {
            halfPtr = halfPtr + N;
        }
        out = hfdata[halfPtr] - (hfy / N);

        hfdata[hfptr] = dataIn;
        hfptr = hfptr + 1;
        if (hfptr == N) {
            hfptr = 0;
        }

        return out;
    }

    public void setBatteryLevel(int percent) {
        int parentWidth = (int) (getResources().getDimensionPixelSize(R.dimen.battery_width) * 0.8f);
        int width = parentWidth * (percent) / 100;
        vBattyRemain.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
        if (percent <= 20) {
            vBattyRemain.setBackgroundColor(Color.RED);
        } else {
            vBattyRemain.setBackgroundColor(Color.WHITE);
        }
        vBattyRemain.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

    }
}
