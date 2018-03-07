package com.sunanang.sunwallpaper.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by ${Sunanang} on 17/12/7.
 */

public class FileUtils2 {
    public static String getVideoSavePath(String filePargName){

        String s = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/sunWallpaper/video/";
        File dir1 = new File(s);
        if (!dir1.exists()){
            dir1.mkdirs();
        }
        return s + filePargName;
    }
    public static String getPicSavePath(String filePargName){
        String s = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/sunWallpaper/photo/" ;
        File dir1 = new File(s);
        if (!dir1.exists()){
            dir1.mkdirs();
        }
        return s + filePargName + ".jpeg";
    }

    /**
     * 背景图片压缩
     * @param FileName  图片路径
     * @return
     */
    public static Drawable compressBmp(Context context, String FileName){
        Bitmap bitmap = BitmapFactory.decodeFile(FileName);
        int[] screenWH = SystemInfoUtils.getWH((Activity) context);
        bitmap = setImgSize(bitmap, screenWH[0], screenWH[1]);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = SharedUtils.getBgQuality(context);//个人喜欢从80开始
        BitmapFactory.Options options2 = new BitmapFactory.Options();

        options2.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);

        byte[] bytes = baos.toByteArray();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options2);
        Drawable drawable = new BitmapDrawable(bm);
        baos.reset();
        return drawable;
    }


    /**
     * bitmap设置宽高
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap setImgSize(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
