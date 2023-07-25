package com.benefm.ecgdemo.util.permissions;

import android.app.Activity;

/**
 * Created by zhuleiyue on 16/3/17.
 * 权限请求监听类
 */
public interface ActivityPermissionsListener {
    /**
     * 权限请求成功后回调
     */
    void onGranted(Activity activity);

    /**
     * 权限请求失败后回调
     */
    void onDenied(Activity activity);
}
