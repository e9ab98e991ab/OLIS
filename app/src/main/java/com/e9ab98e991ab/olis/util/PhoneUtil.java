package com.e9ab98e991ab.olis.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/** 
 * @author gaoxin 2020/6/9 16:35
 * @version V1.0.0
 * @name PhoneUtil
 * @mail godfeer@aliyun.com
 * @description  TODO
 */
public class PhoneUtil {

    public static int getPhoneWid(Context context) {
        WindowManager windowManager =
            (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getPhoneHei(Context context) {
        WindowManager windowManager =
            (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

}