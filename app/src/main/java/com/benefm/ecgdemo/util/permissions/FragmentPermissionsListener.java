package com.benefm.ecgdemo.util.permissions;

import androidx.fragment.app.Fragment;

/**
 * Created by zhuleiyue on 16/3/17.
 * 权限请求监听类
 */
public interface FragmentPermissionsListener {
    /**
     * 权限请求成功后回调
     */
    void onGranted(Fragment fragment);

    /**
     * 权限请求失败后回调
     */
    void onDenied(Fragment fragment);
}
