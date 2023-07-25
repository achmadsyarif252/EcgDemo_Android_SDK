package com.benefm.ecgdemo.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benefm.ecgdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kqw on 2016/8/2.
 * Wifi列表的数据适配器
 */
public class WifiListAdapter extends BaseAdapter {

    private static final String TAG = "WifiListAdapter";
    private List<ScanResult> scanResults;
    private Context mContext;

    public WifiListAdapter(Context context) {
        mContext = context.getApplicationContext();
        this.scanResults = new ArrayList<>();
    }

    public static ArrayList<ScanResult> excludeRepetition(List<ScanResult> scanResults) {
        HashMap<String, ScanResult> hashMap = new HashMap<>();

        for (ScanResult scanResult : scanResults) {
            String ssid = scanResult.SSID;

            if (TextUtils.isEmpty(ssid)) {
                continue;
            }

            ScanResult tempResult = hashMap.get(ssid);
            if (null == tempResult) {
                hashMap.put(ssid, scanResult);
                continue;
            }

            if (WifiManager.calculateSignalLevel(tempResult.level, 100) < WifiManager.calculateSignalLevel(scanResult.level, 100)) {
                hashMap.put(ssid, scanResult);
            }
        }

        ArrayList<ScanResult> results = new ArrayList<>();
        for (Map.Entry<String, ScanResult> entry : hashMap.entrySet()) {
            results.add(entry.getValue());
        }

        return results;
    }


    public void refreshData(List<ScanResult> scanResults) {
        if (null != scanResults) {
            Log.i(TAG, "refreshData 1 : " + scanResults.size());
            // 去重
            scanResults = excludeRepetition(scanResults);
            Log.i(TAG, "refreshData 2 : " + scanResults.size());
            // 清空数据
            this.scanResults.clear();
            // 更新数据
            this.scanResults.addAll(scanResults);
        }
        // 更新显示
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_wifi, null);
            holder = new ViewHolder();
            holder.ssid = (TextView) (convertView).findViewById(R.id.ssid);
            holder.ssid1 = (TextView) (convertView).findViewById(R.id.ssid1);
            holder.iv = (ImageView) (convertView).findViewById(R.id.iv);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = scanResults.get(position);

        holder.ssid.setText(scanResult.SSID);

        String s = mContext.getString(R.string.ss179);
        if (getMode(scanResult)) {

        } else {
            s = mContext.getString(R.string.ss180);
        }

        int le = WifiManager.calculateSignalLevel(scanResult.level, 4);
        holder.ssid1.setText(s);

        if (getMode(scanResult)) {
            switch (le) {
                case 1:
                    holder.iv.setImageResource(R.drawable.s1);
                    break;
                case 2:
                    holder.iv.setImageResource(R.drawable.s2);
                    break;
                case 3:
                    holder.iv.setImageResource(R.drawable.s3);
                    break;
                case 4:
                    holder.iv.setImageResource(R.drawable.s4);
                    break;
            }
        } else {
            switch (le) {
                case 1:
                    holder.iv.setImageResource(R.drawable.o1);
                    break;
                case 2:
                    holder.iv.setImageResource(R.drawable.o2);
                    break;
                case 3:
                    holder.iv.setImageResource(R.drawable.o3);
                    break;
                case 4:
                    holder.iv.setImageResource(R.drawable.o4);
                    break;
            }
        }

        return convertView;
    }


    boolean getMode(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (!TextUtils.isEmpty(capabilities)) {

            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return true;

            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    private class ViewHolder {
        private TextView ssid;
        private TextView ssid1;
        private ImageView iv;
    }
}
