package com.sunanang.sunwallpaper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ${Sunanang} on 17/12/2.
 */

public class HttpUtils {

    public static final String PIC_URL="http://s.3987.com/api-2.0/Public/demo/index.php";

    /**
     * 检测网络是否连接
     * @return
     */
    public static boolean checkNetworkState(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }


    public static String setHttp(String url){
        StringBuilder resultData = new StringBuilder();
        try {
            URL urls = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            //inputStreamReader一个个字节读取转为字符,可以一个个字符读也可以读到一个buffer
            //getInputStream是真正去连接网络获取数据
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());

            //使用缓冲一行行的读入，加速InputStreamReader的速度
            BufferedReader buffer = new BufferedReader(isr);
            String inputLine = null;

            while((inputLine = buffer.readLine()) != null){
                resultData.append(inputLine);
                resultData.append("\n");
            }
            buffer.close();
            isr.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultData.toString();
    }


    public static void setADHttp(final String url){
        new Thread(){
            @Override
            public void run() {
                super.run();
                URL urls = null;
                try {
                    urls = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
                    //inputStreamReader一个个字节读取转为字符,可以一个个字符读也可以读到一个buffer
                    //getInputStream是真正去连接网络获取数据
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
