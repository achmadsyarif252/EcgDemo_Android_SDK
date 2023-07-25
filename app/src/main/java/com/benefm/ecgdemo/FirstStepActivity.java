package com.benefm.ecgdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.benefm.ecgdemo.scan.CustomCaptureActivityV2;
import com.benefm.ecgdemo.util.permissions.ActivityPermissionsListener;
import com.benefm.ecgdemo.util.permissions.PermissionsManager;

/**
 * Created by Benefm on 2017/5/22 0022.
 */

public class FirstStepActivity extends AppCompatActivity {

    private Button btnNext;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //扫码绑定
                PermissionsManager.requestPermissions(FirstStepActivity.this, new String[]{Manifest.permission.CAMERA}, new ActivityPermissionsListener() {
                    @Override
                    public void onGranted(Activity activity1) {
                        // Have permission, do the thing!
                        FirstStepActivity.this.finish();
                        Intent intent = new Intent(FirstStepActivity.this, CustomCaptureActivityV2.class);
                       startActivity(intent);
                    }

                    @Override
                    public void onDenied(Activity activity1) {
                    }
                });
            }
        });

    }

    //动态授权所必须
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.onRequestPermissionsResult(this, grantResults);
    }

}
