package com.benefm.ecgdemo.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benefm.ecgdemo.R;
import com.uuzuche.lib_zxing.DisplayUtil;


/**
 * 类名称：ToastUitl
 * 创建者：Create by liujc
 * 创建时间：Create on 2016/11/15 15:39
 * 描述：Toast统一管理类
 * 最近修改时间：2016/11/15 15:39
 * 修改人：Modify by liujc
 */
public class ToastUitl {
    private static Toast toast2;
    private static Toast mToast;
    private static Toast mToastTop;
    private static Toast mToastCenter;
    private static Toast mToastBottom;

    public static void showToast(Context context, CharSequence msg) {
        showToastLong(context,msg.toString());
    }

    /**
     * 短时间中下位置显示。线程安全，可以在非UI线程调用。
     */
    public static void showToast(Context context, final int resId) {
        showToast(context,context.getResources().getString(resId));
    }

    /**
     * 短时间中下位置显示。
     */
    public static void showToast(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_SHORT, Gravity.BOTTOM);
    }

    /**
     * 长时间中下位置显示。
     */
    public static void showToastLong(Context context, final int resId) {
        showToastLong(context, context.getResources().getString(resId));
    }

    /**
     * 长时间中下位置显示。
     */
    public static void showToastLong(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_LONG, Gravity.BOTTOM);
    }


    /**
     * 短时间居中位置显示。
     */
    public static void showToastCenter(Context context, final int resId) {
        showToastCenter(context, context.getResources().getString(resId));
    }

    /**
     * 短时间居中位置显示。
     */
    public static void showToastCenter(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    /**
     * 长时间居中位置显示。
     */
    public static void showToastCenterLong(Context context, final int resId) {
        showToastCenterLong(context,context.getResources().getString(resId));
    }

    /**
     * 长时间居中位置显示。
     */
    public static void showToastCenterLong(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_LONG, Gravity.CENTER);
    }

    /**
     * 短时间居中位置显示。
     */
    public static void showToastTop(Context context, final int resId) {
        showToastTop(context, context.getResources().getString(resId));
    }

    /**
     * 短时间居中位置显示。
     */
    public static void showToastTop(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_SHORT, Gravity.TOP);
    }

    /**
     * 长时间居中位置显示。
     */
    public static void showToastTopLong(Context context, final int resId) {
        showToastTopLong(context, context.getResources().getString(resId));
    }

    /**
     * 长时间居中位置显示。
     */
    public static void showToastTopLong(Context context, final String str) {
        showToast(context, str, Toast.LENGTH_LONG, Gravity.TOP);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(Context context, String message, int duration) {
        showToast(context, message, duration, Gravity.BOTTOM);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param strResId
     * @param duration
     */
    public static void show(Context context, int strResId, int duration) {
        showToast(context, context.getResources().getString(strResId), duration, Gravity.BOTTOM);
    }

    /**
     * 显示有image的toast
     *
     * @param tvStr
     * @param imageResource
     * @return
     */
    public static void showToastWithImg(Context context, final String tvStr, final int imageResource) {
        if (toast2 == null) {
            toast2 = new Toast(context);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
        TextView tv = (TextView) view.findViewById(R.id.toast_custom_tv);
        tv.setText(TextUtils.isEmpty(tvStr) ? "" : tvStr);
        ImageView iv = (ImageView) view.findViewById(R.id.toast_custom_iv);
        if (imageResource > 0) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(imageResource);
        } else {
            iv.setVisibility(View.GONE);
        }
        toast2.setView(view);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        mToast = toast2;
        mToast.show();
    }

    public static void showToastLayout(Context context, final int toastLayout) {
        showToastLayout(context, toastLayout, Gravity.CENTER);
    }

    /**
     * @param toastLayout 自定义布局
     * @param gravity     显示位置
     * @return
     */
    public static void showToastLayout(Context context, final int toastLayout, int gravity) {
        if (toast2 == null) {
            toast2 = new Toast(context);
        }
        View view = LayoutInflater.from(context).inflate(toastLayout, null);
        toast2.setView(view);
        toast2.setGravity(gravity, 0, 0);
        mToast = toast2;
        mToast.show();
    }

    /**
     * 对toast的简易封装。线程不安全，不可以在非UI线程调用。
     */
    private static void showToast(Context context, String str, int showTime, int gravity) {
        if (context == null) {
            throw new RuntimeException("DialogUIUtils not initialized!");
        }
        int layoutId = R.layout.dialogui_toast;
        if (gravity == Gravity.TOP) {
            if (mToastTop == null) {
                mToastTop = Toast.makeText(context, str, showTime);
                LayoutInflater inflate = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflate.inflate(layoutId, null);
                mToastTop.setView(view);
                mToastTop.setGravity(gravity, 0,  DisplayUtil.dip2px(context,64));
            }
            mToast = mToastTop;
            mToast.setText(str);
            mToast.show();
        } else if (gravity == Gravity.CENTER) {
            if (mToastCenter == null) {
                mToastCenter = Toast.makeText(context, str, showTime);
                LayoutInflater inflate = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflate.inflate(layoutId, null);
                mToastCenter.setView(view);
                mToastCenter.setGravity(gravity, 0, 0);
            }
            mToast = mToastCenter;
            mToast.setText(str);
            mToast.show();
        } else if (gravity == Gravity.BOTTOM) {
            if (mToastBottom == null) {
                mToastBottom = Toast.makeText(context, str, showTime);
                LayoutInflater inflate = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflate.inflate(layoutId, null);
                mToastBottom.setView(view);
                mToastBottom.setGravity(gravity, 0, DisplayUtil.dip2px(context,64));
            }
            mToast = mToastBottom;
            mToast.setText(str);
            mToast.show();
        }

    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
