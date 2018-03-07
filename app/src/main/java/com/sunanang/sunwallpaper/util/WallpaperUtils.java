package com.sunanang.sunwallpaper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.IOException;

/**
 * Created by ${Sunanang} on 17/11/29.
 */

public class WallpaperUtils {

    /**
     * 判断一个动态壁纸是否已经在运行(根据service名来判断,因为一个动态壁纸主要就是一个service)
     *
     * @param context
     *            上下文
     * @param tagetServiceName
     *            要判断的动态壁纸的服务名
     * @return
     */
    public static boolean isLiveWallpaperServiceRunning(Context context, String tagetServiceName) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);// 得到壁纸管理器
        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();// 如果系统使用的壁纸是动态壁纸话则返回该动态壁纸的信息,否则会返回null
        if (wallpaperInfo != null) { // 如果是动态壁纸,则得到该动态壁纸的service名,并与想知道的动态壁纸<span style="font-size: 1em; line-height: 1.5;">service</span><span style="font-size: 1em; line-height: 1.5;">名做比较</span>
            String currentLiveWallpaperServiceName = wallpaperInfo.getServiceName();
            if (currentLiveWallpaperServiceName.equals(tagetServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取屏幕宽高
     */
    public static int[] getScreenParams(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new int[]{screenWidth,screenHeight};
    }

    /**
     * 设置图片壁纸
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    public static void setWallPaper(Activity context, int width, int height, String imagePath){
        try {
            clearWall(context);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int desiredMinimumWidth = dm.widthPixels;
            int desiredMinimumHeight = dm.heightPixels;
            wallpaperManager.suggestDesiredDimensions(desiredMinimumWidth,desiredMinimumHeight);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            wallpaperManager.setBitmap(bitmap);
            SharedUtils.setFileShared(context,imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearWall(Context context){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            wallpaperManager.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
