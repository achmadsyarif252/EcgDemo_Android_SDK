package com.benefm.ecgdemo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.aigestudio.wheelpicker.WheelPicker;
import com.befem.sdk.ecg.device.ACache;
import com.befem.sdk.ecg.device.DevManager;
import com.befem.sdk.ecg.device.model.DeviceConnState;
import com.befem.sdk.ecg.device.model.HolterCmdRespose;
import com.befem.sdk.ecg.device.model.HolterSyncResponse;
import com.befem.sdk.ecg.device.model.HrBatteryLevelData;
import com.befem.sdk.ecg.device.model.UserInfoCmdResponse;
import com.benefm.ecgdemo.util.ToastUitl;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.uuzuche.lib_zxing.DisplayUtil;
import com.zhl.cbdialog.CBDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import me.shaohui.bottomdialog.BottomDialog;


public class HolterActivity extends AppCompatActivity {

    final TestLock testLock = new TestLock();
    Handler handler = new Handler();
    Vector<Integer> hrArray = new Vector<>();
    Integer count = 0;
    private String mExtra;
    private TextView tvHeartRate;
    private RelativeLayout rlTimeSelect;
    private RelativeLayout rlGoWave;
    private TextView tvTimeRemain;
    private Button tvStart;
    private TextView tvHeartRateHint;
    private volatile boolean isStart = false;
    private volatile boolean cha = false;
    private volatile int chaFailCount = 0;
    private DownTimer mc;
    private int time = 0;
    private TextView tvSetting;
    private KProgressHUD kProgressHUD;
    Runnable chaRun = new Runnable() {
        @Override
        public void run() {
            synchronized (testLock) {

                if (!cha) {
                    chaFailCount++;
                    kProgressHUD.dismiss();
//                    tvStart.setEnabled(true);
                    ToastUitl.showToast(HolterActivity.this, getString(R.string.ss152));
                }
            }
        }
    };
    Runnable startRun = new Runnable() {
        @Override
        public void run() {
            synchronized (testLock) {

                if (!isStart) {
                    doCha(false);
                }
            }
        }
    };
    Runnable stopRun = new Runnable() {
        @Override
        public void run() {
            synchronized (testLock) {

                if (isStart) {
                    doCha(false);
                }
            }
        }
    };
    private KProgressHUD kProgressHUD1;
    private RelativeLayout rlCc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg_holter);
        EventBus.getDefault().register(this);

        tvHeartRate = findViewById(R.id.tv_heart_rate);
        rlTimeSelect = findViewById(R.id.rl_time_select);
        rlGoWave = findViewById(R.id.rl_go_wave);
        rlGoWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HolterActivity.this, EcgActivity.class));
            }
        });


        tvSetting = findViewById(R.id.tvSetting);

        tvTimeRemain = findViewById(R.id.tv_time_remain);
        tvHeartRateHint = findViewById(R.id.tv_heart_rate_hint);

        tvTimeRemain.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));


        tvStart = (Button) findViewById(R.id.iv_start);

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!DevManager.getInstance().isBindMac()) {
                    ToastUitl.showToast(HolterActivity.this, getString(R.string.ss143));
                    return;
                }

                if (!DevManager.getInstance().getDeviceConnState()) {
                    ToastUitl.showToast(HolterActivity.this, getString(R.string.ss151));
                    return;
                }

                if (!cha) {

                    if (chaFailCount >= 3) {
                        ToastUitl.showToast(HolterActivity.this, getString(R.string.ss189));
                    } else {
                        ToastUitl.showToast(HolterActivity.this, getString(R.string.ss190));
                        doCha(true);
                    }

                    return;
                }

                if (!isStart) {
                    doStart();
                } else {
                    doStop();
                }
            }
        });


        rlTimeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!DevManager.getInstance().isBindMac()) {
                    ToastUitl.showToast(HolterActivity.this, getString(R.string.ss143));
                    return;
                }

                if (!DevManager.getInstance().getDeviceConnState()) {
                    ToastUitl.showToast(HolterActivity.this, getString(R.string.ss151));
                    return;
                }

                if (!cha) {

                    if (chaFailCount >= 3) {
                        ToastUitl.showToast(HolterActivity.this, getString(R.string.ss189));
                    } else {
                        ToastUitl.showToast(HolterActivity.this, getString(R.string.ss192));
                        doCha(true);
                    }

                    return;
                }

                if (isStart) return;


                final BottomDialog bottomDialog = BottomDialog.create(HolterActivity.this.getSupportFragmentManager());
                bottomDialog.setLayoutRes(R.layout.dialog_number_picker)
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v) {
                                final WheelPicker wheelPicker = (WheelPicker) v.findViewById(R.id.wheel_time_picker_hour);
                                final TextView textView = (TextView) v.findViewById(R.id.tvUnit);
                                textView.setText(getString(R.string.ss139));
                                final List<String> data = new ArrayList<>();
                                for (int i = 10; i <= 72; i++) {
                                    data.add(String.valueOf(i));
                                }
                                wheelPicker.setData(data);
                                wheelPicker.setItemAlign(WheelPicker.ALIGN_CENTER);
                                wheelPicker.setSelectedItemPosition(11);
                                Button btnCancle = (Button) v.findViewById(R.id.btnCancle);
                                btnCancle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomDialog.dismiss();
                                    }
                                });
                                Button btnOk = (Button) v.findViewById(R.id.btnOk);
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomDialog.dismiss();

                                        time = (wheelPicker.getCurrentItemPosition() + 10) * 60 * 60 * 1000;
                                        ACache.get(HolterActivity.this).put("timeselect", "" + time);
                                        tvTimeRemain.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));
                                        tvSetting.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));
                                    }
                                });

                            }
                        }).setDimAmount(0.1f).setHeight(DisplayUtil.dip2px(HolterActivity.this, 220)).show();
            }
        });


        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.ss150))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setWindowColor(Color.parseColor("#5BA2D6"))
                .setDimAmount(0.0f);


        doCha(true);


        DevManager.getInstance().setOnHrBatteryLevelListener(new DevManager.OnHrBatteryLevelDataListener() {
            @Override
            public void onHrBatteryLevelData(HrBatteryLevelData event) {
//                tvh.setText("心率："+event.heartRate+"  电量："+event.batteryLevel);


                if (isStart) {
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
                }
            }
        });


    }

    private void initTimeCount() {

        mc = new DownTimer();//实例化
        mc.setTotalTime(time);//设置毫秒数
        mc.setIntervalTime(1000);//设置间隔数
        mc.setTimerLiener(new DownTimer.TimeListener() {
            @Override
            public void onFinish() {
                doCha(false);

            }

            @Override
            public void onInterval(long remainTime) {
                tvTimeRemain.setText(String.format("%02d", ((remainTime / 1000) / 60 / 60)) + ":" + String.format("%02d", ((remainTime / 1000) / 60 % 60)) + ":" + String.format("%02d", ((remainTime / 1000) % 60)));
            }
        });

    }

    private void doStart() {

        if (getString(R.string.ss111).equals(tvSetting.getText().toString())) {
            ToastUitl.showToast(HolterActivity.this, getString(R.string.ss111));
            return;
        } else {
            String timeS = tvSetting.getText().toString();
            String[] ims = timeS.split(":");
            time = Integer.parseInt(ims[0]) * 60 * 60 * 1000 + Integer.parseInt(ims[1]) * 60 * 1000 + Integer.parseInt(ims[2]) * 1000;
        }
        kProgressHUD.show();

        isStart = false;
        new Thread() {
            @Override
            public void run() {
                super.run();
                DevManager.getInstance().sendHolterUserInfoCommand("12345678901234561234567890123456");//测试用，需修改
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DevManager.getInstance().sendHolterTimeCommand(time / 1000 / 60 / 60, 1);

                handler.postDelayed(startRun, 5000);
            }
        }.start();

    }

    private void doStop() {

        final Dialog conndialog2 = new CBDialogBuilder(HolterActivity.this, R.layout.layout_custom_dialog_layout1, 0.8f)
                .setTouchOutSideCancelable(true)
                .showCancelButton(true)
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setTitle(getString(R.string.ss127))
                .setConfirmButtonText(getString(R.string.ss119))
                .setMessage(getString(R.string.ss153)
                )
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .create();


        ((TextView) conndialog2.findViewById(R.id.dialog_title)).setText(getString(R.string.ss127));
        conndialog2.findViewById(R.id.dialog_posi_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conndialog2.dismiss();

//                tvStart.setEnabled(false);
                kProgressHUD.show();

                DevManager.getInstance().sendHolterTimeCommand(time / 1000 / 60 / 60, 2);


                handler.postDelayed(stopRun, 5000);


            }
        });
        conndialog2.findViewById(R.id.dialog_nege__btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conndialog2.dismiss();
            }
        });


        conndialog2.show();

    }


    void doCha(boolean show) {
//        tvStart.setEnabled(false);

        if (!DevManager.getInstance().isBindMac()) {
            ToastUitl.showToast(HolterActivity.this, getString(R.string.ss143));
            return;
        }

        if (!DevManager.getInstance().getDeviceConnState()) {
            ToastUitl.showToast(HolterActivity.this, getString(R.string.ss151));
            return;
        }


        cha = false;

        if (show)
            kProgressHUD.show();

        DevManager.getInstance().getHolterTimeCommand();
        handler.postDelayed(chaRun, 5000);


      /*  handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 100);*/
    }


    void isStart() {
        synchronized (testLock) {
            handler.removeCallbacks(startRun);
            String timetemp = ACache.get(HolterActivity.this).getAsString("timeselect");

            if (!TextUtils.isEmpty(timetemp)) {
                int time = Integer.parseInt(timetemp);
                tvSetting.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));
            }

            kProgressHUD.dismiss();
            isStart = true;
            tvStart.setText(getString(R.string.ss127));
            if (mc != null) {
                mc.cancel();
            }
            initTimeCount();
            mc.start();

        }
    }


    private void isResume(HolterSyncResponse event) {

        synchronized (testLock) {

            time = event.hours * 60 * 60 * 1000 + event.minutes * 60 * 1000 + event.sec * 1000;

            String timetemp = ACache.get(HolterActivity.this).getAsString("timeselect");

            if (!TextUtils.isEmpty(timetemp)) {
                int time = Integer.parseInt(timetemp);
                tvSetting.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));
            }


            isStart = true;
            tvStart.setText(getString(R.string.ss127));
            if (mc != null) {
                mc.cancel();
            }
            initTimeCount();
            mc.start();

        }
    }


    private void isOver() {
        synchronized (testLock) {

            handler.removeCallbacks(stopRun);
            kProgressHUD.dismiss();
            isStart = false;
            tvStart.setText(getString(R.string.ss100));
            tvSetting.setText(getString(R.string.ss111));
            ACache.get(HolterActivity.this).put("timeselect", "");
            if (mc != null) {
                mc.pause();
            }
            time = 0;
            tvTimeRemain.setText(String.format("%02d", ((time / 1000) / 60 / 60)) + ":" + String.format("%02d", ((time / 1000) / 60 % 60)) + ":" + String.format("%02d", ((time / 1000) % 60)));
            tvHeartRate.setText("0");
            tvHeartRate.setTextColor(Color.WHITE);


        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHolterSetRepose(HolterCmdRespose event) {//holter 开关命令响应

        if (event.mode == 1) {
            if (event.mode1 == 1) {

                isStart();

            } else if (event.mode1 == 2) {
                isOver();

            } else {
                ToastUitl.showToast(HolterActivity.this, getString(R.string.ss154));
                handler.removeCallbacks(stopRun);
                handler.removeCallbacks(startRun);
                kProgressHUD.dismiss();
            }

        } else {
            ToastUitl.showToast(HolterActivity.this, getString(R.string.ss154));
            handler.removeCallbacks(stopRun);
            handler.removeCallbacks(startRun);
            kProgressHUD.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onECGModeSettingData(HolterSyncResponse event) {//holter 时间同步响应
        kProgressHUD.dismiss();
        handler.removeCallbacks(chaRun);
        cha = true;
        chaFailCount = 0;
        ToastUitl.showToast(HolterActivity.this, getString(R.string.ss191));
        if (event.mode == 1) {


            isResume(event);

        } else {

            isOver();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceConnState(DeviceConnState event) {

        if (event.connState == 2) {

        }

        if (event.connState == 1) {
            cha = false;
            chaFailCount = 0;
            if (mc != null) {
                mc.pause();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mc != null) {
            mc.cancel();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoCmdResponse(UserInfoCmdResponse event) {
        ToastUitl.showToast(HolterActivity.this, "用户信息命令相应成功\n");
    }


}
