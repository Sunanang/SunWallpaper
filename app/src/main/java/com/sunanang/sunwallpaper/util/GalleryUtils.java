package com.sunanang.sunwallpaper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.browse.BrowsePhoneActivity;
import com.sunanang.sunwallpaper.browse.BrowseVideoActivity;
import com.sunanang.sunwallpaper.service.ClockWallpaperService;
import com.sunanang.sunwallpaper.specialActivity.TransparentActivity;
import com.sunanang.sunwallpaper.trendsGL.gl.AdvanceGLWallpaperService;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.mediascanner.MediaScanner;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by 孙连强 on 2017/11/19.
 */

public class GalleryUtils {
    private static GalleryUtils galleryUtils;

    private GalleryUtils(){}

    public static GalleryUtils getInstance(){
        if(galleryUtils == null){
            galleryUtils = new GalleryUtils();
        }
        return galleryUtils;
    }


    @SuppressLint("ResourceAsColor")
    public void setVideoActivity(final Context context){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sunWallpaper/video/";

        ArrayList<String> strings = GetVideoFileName(filePath);
        ArrayList<String> filepath = new ArrayList<>();
        for(String lib:strings){
            filepath.add(filePath + "/" + lib);
        }
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(filepath);

        Album.video(context) // 选择视频。
                .singleChoice()
                .requestCode(99)
                .camera(false)
                .widget(Widget.newDarkBuilder(context)
                        .statusBarColor(R.color.colorToolbar) // 状态栏颜色。
                        .toolBarColor(R.color.colorToolbar) // Toolbar颜色。
                        .build())
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode,  ArrayList<AlbumFile> result) {
                        // TODO 接受结果。
                        Intent intent =  new Intent(context, BrowseVideoActivity.class);
                        intent.putExtra("videoPath",result.get(0).getPath());
                        intent.putExtra("typeCode",1);
                        context.startActivity(intent);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, String result) {
                        // 用户取消了操作。
                    }
                })
                .start();
    }

    @SuppressLint("ResourceAsColor")
    public void setGalleryActivity(final Context context){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sunWallpaper/photo/";

        ArrayList<String> strings = GetImageFileName(filePath);
        ArrayList<String> filepath = new ArrayList<>();
        for(String lib:strings){
            filepath.add(filePath + "/" + lib);
        }
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(filepath);

        Album.image(context) // 选择图片。
                .singleChoice()
                .requestCode(99)
                .camera(false)
                .widget(Widget.newDarkBuilder(context)
                        .statusBarColor(R.color.colorToolbar) // 状态栏颜色。
                        .toolBarColor(R.color.colorToolbar) // Toolbar颜色。
                        .build())
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, ArrayList<AlbumFile> result) {
                        //返回数据
                        Intent intent=new Intent(context,BrowsePhoneActivity.class);
                        intent.putExtra("ImagePath",result.get(0).getPath());
                        intent.putExtra("typeCode",1);
                        context.startActivity(intent);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, String result) {

                    }
                })
                .start();
    }



    @SuppressLint("ResourceAsColor")
    public void setGalleryPicWallActivity(final Activity context, final int type){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sunWallpaper/photo/";

        ArrayList<String> strings = GetImageFileName(filePath);
        ArrayList<String> filepath = new ArrayList<>();
        for(String lib:strings){
            filepath.add(filePath + "/" + lib);
        }
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(filepath);

        Album.image(context) // 选择图片。
                .singleChoice()
                .requestCode(99)
                .camera(false)
                .widget(Widget.newDarkBuilder(context)
                        .statusBarColor(R.color.colorToolbar) // 状态栏颜色。
                        .toolBarColor(R.color.colorToolbar) // Toolbar颜色。
                        .build())
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, ArrayList<AlbumFile> result) {
                        String path = result.get(0).getPath();
                        if (!TextUtils.isEmpty(path)){
                            if(type == 1){
                                SharedUtils.setFileGLShared(context,path);
                                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                        new ComponentName(context, AdvanceGLWallpaperService.class));
                                SharedUtils.setStartStateShared(context,true);
                                context.startActivity(intent);
                            }else if(type == 2){
                                SharedUtils.setFileClockShared(context,path);
                                ClockWallpaperService.setToClockWallPaper(context);
                            }

                        }else
                            Toast.makeText(context,"您必须选择一张照片",Toast.LENGTH_SHORT).show();

                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, String result) {

                    }
                }).start();
    }


    // 获取当前目录下所有的jpg文件
    public ArrayList<String> GetImageFileName(String fileAbsolutePath) {
        ArrayList<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if(subFile != null){
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    // 判断是否为jpg结尾
                    if (filename.trim().toLowerCase().endsWith(".jpg")) {
                        vecFile.add(filename);
                    }
                }
            }
        }
        return vecFile;
    }


    // 获取当前目录下所有的jpg文件
    public ArrayList<String> GetVideoFileName(String fileAbsolutePath) {
        ArrayList<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        if(subFile != null){
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    // 判断是否为MP4结尾
                    if (filename.trim().toLowerCase().endsWith(".mp4")) {
                        vecFile.add(filename);
                    }
                }
            }
        }
        return vecFile;
    }


}
