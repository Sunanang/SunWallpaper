package com.sunanang.sunwallpaper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;


/**
 * Created by ${Sunanang} on 17/12/8.
 */

public class SystemInfoUtils {
    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取手机imei
     * @param context
     * @return
     */
    public static String  getIMEI(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获取手机宽高
     * @param context
     * @return
     */
    public static int[] getWH(Activity context){
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = outMetrics.widthPixels;
        int height2 = outMetrics.heightPixels;
        return new int[]{width2,height2};
    }
}
