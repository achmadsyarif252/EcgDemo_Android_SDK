package com.benefm.ecgdemo.util.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

/**
 * Created by zhuleiyue on 16/3/17.
 */
public class PermissionsManager {
    private static final SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(6);
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
    }

    private static ActivityPermissionsListener mActivityListener;
    private static FragmentPermissionsListener mFragmentListener;

    /**
     * 检查是否具有一组或一个权限
     *
     * @param context     当前上下文
     * @param permissions 权限数组
     * @return
     */
    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (permissionExists(permission) && !hasSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 当前系统版本是否存在该权限
     *
     * @param permission 权限
     * @return
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
    }

    /**
     * 检查是否具有该权限
     *
     * @param context    当前上下文
     * @param permission 权限
     * @return
     */
    private static boolean hasSelfPermission(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 验证请求权限结果
     *
     * @param grantResults 请求权限结果
     * @return
     */
    public static boolean verifyPermissions(int... grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     *
     * @param activity    当前activity
     * @param permissions 权限数组
     * @param listener    权限请求监听回调
     */
    public static void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, @Nullable ActivityPermissionsListener listener) {
        mActivityListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mActivityListener != null) {
                if (hasSelfPermissions(activity, permissions)) {
                    mActivityListener.onGranted(activity);
                } else {
                    mActivityListener.onDenied(activity);
                }
            }
        } else {
            if (hasSelfPermissions(activity, permissions)) {
                if (mActivityListener != null) {
                    mActivityListener.onGranted(activity);
                }
            } else {
                activity.requestPermissions(permissions, 1);
            }
        }
    }

    /**
     * 请求权限
     *
     * @param fragment    当前fragment
     * @param permissions 权限数组
     * @param listener    权限请求监听回调
     */
    public static void requestPermissions(@NonNull Fragment fragment, @NonNull String[] permissions, @Nullable FragmentPermissionsListener listener) {
        mFragmentListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mFragmentListener != null) {
                if (hasSelfPermissions(fragment.getActivity(), permissions)) {
                    mFragmentListener.onGranted(fragment);
                } else {
                    mFragmentListener.onDenied(fragment);
                }
            }
        } else {
            if (hasSelfPermissions(fragment.getActivity(), permissions)) {
                if (mFragmentListener != null) {
                    mFragmentListener.onGranted(fragment);
                }
            } else {
                fragment.requestPermissions(permissions, 1);
            }
        }
    }

    /**
     * 请求权限结果回调处理
     *
     * @param grantResults
     */
    public static void onRequestPermissionsResult(@NonNull Activity activity, @NonNull int[] grantResults) {
        if (mActivityListener != null) {
            if (verifyPermissions(grantResults)) {
                mActivityListener.onGranted(activity);
            } else {
                mActivityListener.onDenied(activity);
            }
        }
    }

    /**
     * 请求权限结果回调处理
     *
     * @param grantResults
     */
    public static void onRequestPermissionsResult(@NonNull Fragment fragment, @NonNull int[] grantResults) {
        if (mFragmentListener != null) {
            if (verifyPermissions(grantResults)) {
                mFragmentListener.onGranted(fragment);
            } else {
                mFragmentListener.onDenied(fragment);
            }
        }
    }

}
