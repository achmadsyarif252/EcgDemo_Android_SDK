package com.benefm.ecgdemo.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.befem.sdk.ecg.device.DevManager;
import com.benefm.ecgdemo.MainActivity;
import com.benefm.ecgdemo.R;

import java.util.regex.Pattern;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;


public class CustomCaptureActivityV2 extends AppCompatActivity implements QRCodeView.Delegate {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture1);


        initView();
    }


    private ImageView ivLeft1;
    private ZBarView mZBarView;
    private TextView skip;

    private void initView() {






        mZBarView = (ZBarView) findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private static final String REGEX_MAC = "([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}";
    private static final Pattern PATTERN_REGEX_MAC = Pattern.compile(REGEX_MAC);

    public static boolean isMac(String str) {
        return isMatch(PATTERN_REGEX_MAC, str);
    }

    public static boolean isMatch(Pattern pattern, String str) {
        return (!TextUtils.isEmpty(str)) && pattern.matcher(str).matches();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        if (isMac(result)) {
            vibrate();


            DevManager.getInstance().bindMac(result);


            startActivity(new Intent(CustomCaptureActivityV2.this, MainActivity.class));

//        mZBarView.startSpot(); // 开始识别
        } else {
            finish();
        }
    }


    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
       /* // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZBarView.getScanBoxView().setTipText(tipText);
            }
        }*/
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
//        Log.e(TAG, "打开相机出错");
        finish();
    }
}