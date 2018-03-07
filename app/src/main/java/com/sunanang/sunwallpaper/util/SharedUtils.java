package com.sunanang.sunwallpaper.util;


import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ${Sunanang} on 17/11/16.
 * sharedPreferences工具类
 */

public class SharedUtils {

    /**
     * 设置录音权限的状态
     * @param context
     * @param isfirstStart
     */
    public static void setFirstShared(Context context, boolean isfirstStart){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstCamera", isfirstStart).commit();

    }

    /**
     * 获得存储的录音的状态
     * @param context
     * @return
     */
    public static boolean getFirstShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("isFirstCamera", true);
        return firstStart;
    }

    /**
     * 存储设置的壁纸
     * @param context
     * @param path
     */
    public static void setFileShared(Context context, String path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString("filePath", path).commit();
    }

    /**
     * 获取设置的壁纸
     * @param context
     * @return
     */
    public static String getFileShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        String path = sharedPreferences.getString("filePath", "");
        return path;
    }

    /**
     * 存储声音状态
     * @param context
     * @param path
     */
    public static void setVolumeShared(Context context, boolean path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("volumeState", path).commit();
    }

    /**
     * 获取声音状态
     * @param context
     * @return
     */
    public static boolean getVolumeShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_PRIVATE);
        boolean volume = sharedPreferences.getBoolean("volumeState", false);
        return volume;
    }


    /**
     * 存储应用背景
     * @param context
     * @param path
     */
    public static void setBackgroundShared(Context context, String path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString("backgroundState", path).commit();
    }

    /**
     * 获取应用背景
     * @param context
     * @return
     */
    public static String getBackgroundShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        String volume = sharedPreferences.getString("backgroundState", "");
        return volume;
    }


    /**
     * 存储GL的壁纸
     * @param context
     * @param path
     */
    public static void setFileGLShared(Context context, String path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString("fileGlPath", path).commit();
    }

    /**
     * 获取GL的壁纸
     * @param context
     * @return
     */
    public static String getFileGLShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        String path = sharedPreferences.getString("fileGlPath", "");
        return path;
    }


    /**
     * 存储钟表的壁纸
     * @param context
     * @param path
     */
    public static void setFileClockShared(Context context, String path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString("fileClockPath", path).commit();
    }

    /**
     * 获取钟表的壁纸
     * @param context
     * @return
     */
    public static String getFileClockShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        String path = sharedPreferences.getString("fileClockPath", "");
        return path;
    }

    /**
     * 存储关于我页面的是否点击
     * @param context
     * @param path
     */
    public static void setAboutFirstShared(Context context, boolean path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean("AboutFirst", path).commit();
    }

    /**
     * 获取关于我页面的是否点击
     * @param context
     * @return
     */
    public static boolean getAboutFirstShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        boolean path = sharedPreferences.getBoolean("AboutFirst", false);
        return path;
    }

    /**
     * 存储使用说明的是否点击
     * @param context
     * @param path
     */
    public static void setInstFirstShared(Context context, boolean path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean("instFirst", path).commit();
    }

    /**
     * 获取使用说明的是否点击
     * @param context
     * @return
     */
    public static boolean getInstFirstShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        boolean path = sharedPreferences.getBoolean("instFirst", false);
        return path;
    }


    /**
     * 存储应用进入后台的方式（home-false和设置动态壁纸-true）
     * @param context
     * @param path
     */
    public static void setStartStateShared(Context context, boolean path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean("startState", path).commit();
    }

    /**
     * 获取应用进入后台的方式（按home键-false 和 设置动态壁纸-true）
     * @param context
     * @return
     */
    public static boolean getStartStateShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        boolean path = sharedPreferences.getBoolean("startState", false);
        return path;
    }

    /**
     * 存储压缩质量
     * @param context
     * @param path
     */
    public static void setBgQuality(Context context, int path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putInt("quality", path).commit();
    }

    /**
     * 获取压缩质量
     * @param context
     * @return
     */
    public static int getBgQuality(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        int path = sharedPreferences.getInt("quality", 60);
        return path;
    }

    /**
     * 存储字体颜色
     * @param context
     * @param path
     */
    public static void setFontColor(Context context, String path){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString("fontColor", path).commit();
    }

    /**
     * 获取字体颜色
     * @param context
     * @return
     */
    public static String getFontColor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareFile",MODE_MULTI_PROCESS);
        String path = sharedPreferences.getString("fontColor", "#ffffff");
        return path;
    }

}
