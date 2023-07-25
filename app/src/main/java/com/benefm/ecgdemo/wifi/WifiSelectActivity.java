package com.benefm.ecgdemo.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benefm.ecgdemo.R;
import com.benefm.ecgdemo.util.permissions.ActivityPermissionsListener;
import com.benefm.ecgdemo.util.permissions.PermissionsManager;

import java.util.List;

/**
 * Created by Benefm on 2017/5/22 0022.
 */

public class WifiSelectActivity extends AppCompatActivity {



    private ListView mWifiList;
    private SwipeRefreshLayout mSwipeLayout;
    private WifiListAdapter mWifiListAdapter;
    WifiManager wifiManager;
    TextView empty_view;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_select);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mWifiList = (ListView) findViewById(R.id.listView);


        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                empty_view.setText(getString(R.string.ss150));


                doScan();


            }
        });
        empty_view = findViewById(R.id.empty_view);
        mWifiList.setEmptyView(empty_view);
        mWifiListAdapter = new WifiListAdapter(getApplicationContext());
        mWifiList.setAdapter(mWifiListAdapter);

        mWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
                if (!TextUtils.isEmpty(scanResult.SSID)) {
                    if (isChinese(scanResult.SSID)) {
                        Toast.makeText(WifiSelectActivity.this, getString(R.string.ss181), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (scanResult.SSID.length() > 14) {
                        Toast.makeText(WifiSelectActivity.this, getString(R.string.ss182), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(WifiSelectActivity.this, WifiConnActivity.class);
                    intent.putExtra("ssid", scanResult.SSID);
                    startActivity(intent);
                    finish();
                }

            }
        });


        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (ok) {//开了定位服务
                @SuppressLint("MissingPermission") List<ScanResult> scanResults = wifiManager.getScanResults();
                refreshData(scanResults);
            } else {
                Toast.makeText(this, getString(R.string.ss178), Toast.LENGTH_SHORT).show();
                empty_view.setText(getString(R.string.ss178));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1315);
            }

        } else {
            doScan();
        }

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);


    }

    private LocationManager lm;//【位置管理】

    public boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    public boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    void doScan() {


        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务

            PermissionsManager.requestPermissions(WifiSelectActivity.this, PERMISSIONS, new ActivityPermissionsListener() {
                @Override
                public void onGranted(Activity activity1) {

                    boolean success = wifiManager.startScan();
                    if (!success) {
                        // scan failure handling
                        scanFailure();
                    }
                }

                @Override
                public void onDenied(Activity activity1) {
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(WifiSelectActivity.this, getString(R.string.ss145), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            mSwipeLayout.setRefreshing(false);
            Toast.makeText(this, getString(R.string.ss178), Toast.LENGTH_SHORT).show();
            empty_view.setText(getString(R.string.ss178));
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }


    }


    BroadcastReceiver wifiScanReceiver;

    private void scanSuccess() {
        @SuppressLint("MissingPermission") List<ScanResult> results = wifiManager.getScanResults();
        Log.d("debug", "scanSuccess: " + results.toString());
        refreshData(results);
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        @SuppressLint("MissingPermission") List<ScanResult> results = wifiManager.getScanResults();
        Log.d("debug", "scanFailure: " + results.toString());
        refreshData(results);
    }

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE
    };


    /**
     * 刷新页面
     *
     * @param scanResults WIFI数据
     */
    public void refreshData(List<ScanResult> scanResults) {
        mSwipeLayout.setRefreshing(false);
        if (scanResults != null && scanResults.size() == 0) {
            empty_view.setText(getString(R.string.ss177));
        } else {
            Toast.makeText(this, getString(R.string.ss176), Toast.LENGTH_SHORT).show();
        }
        // 刷新界面
        mWifiListAdapter.refreshData(scanResults);

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

        unregisterReceiver(wifiScanReceiver);
    }
}
