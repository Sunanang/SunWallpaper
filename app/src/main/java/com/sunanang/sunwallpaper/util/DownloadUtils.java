package com.sunanang.sunwallpaper.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.sunanang.sunwallpaper.bean.ADBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ${Sunanang} on 17/12/6.
 * 下载工具类
 */

public class DownloadUtils {
    public static void download(String url, String localPath, OnDownloadListener listener) {
        DownloadAsyncTask task = new DownloadAsyncTask(url, localPath, listener);
        task.execute();
    }

    public static class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {

        private String mFailInfo;

        private String mUrl;
        private String mFilePath;
        private OnDownloadListener mListener;

        DownloadAsyncTask(String mUrl, String mFilePath, OnDownloadListener mListener) {
            this.mUrl = mUrl;
            this.mFilePath = mFilePath;
            this.mListener = mListener;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String pdfUrl = mUrl;

            try {
                URL url = new URL(pdfUrl);
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                int contentLength = urlConnection.getContentLength();


                File pdfFile = new File(mFilePath);
                if (pdfFile.exists()) {
                    boolean result = pdfFile.delete();
                    if (!result) {
                        mFailInfo = "存储路径下的同名文件删除失败！";
                        return false;
                    }
                }

                int downloadSize = 0;
                byte[] bytes = new byte[1024];
                int length;
                OutputStream out = new FileOutputStream(mFilePath);
                while ((length = in.read(bytes)) != -1) {
                    out.write(bytes, 0, length);
                    downloadSize += length;
                    publishProgress( downloadSize * 100 / contentLength);
                }

                in.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
                mFailInfo = e.getMessage();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mListener != null) {
                mListener.onStart();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mListener != null) {
                if (aBoolean) {
                    mListener.onSuccess(new File(mFilePath));
                } else {
                    mListener.onFail(new File(mFilePath), mFailInfo);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values != null && values.length > 0) {
                if (mListener != null && values[0] > 0) {
                    mListener.onProgress(values[0]);
                }
            }
        }
    }

    public interface OnDownloadListener{
        void onStart();
        void onSuccess(File file);
        void onFail(File file, String failInfo);
        void onProgress(int progress);
    }


    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //======================================
    private static long mTaskId;
    private static DownloadManager downloadManager;
    private static Context mContext;
    private static String mVersionName;
    private static ADBean mBean;

    //使用系统下载器下载
    public static void downloadAPK(String versionUrl, String versionName, Context context, ADBean bean) {
        mContext = context;
        mVersionName = versionName;
        mBean = bean;
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/download/", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId  = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    //广播接受者，接收下载状态
    private static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };
    //检查下载状态
    private static void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i("Sunanang",">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i("Sunanang",">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i("Sunanang",">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i("Sunanang",">>>下载完成");
                    HttpUtils.setADHttp(mBean.getEdownUrl());
                    //下载完成安装APK
                    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + mVersionName;
                    installAPK(new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.i("Sunanang",">>>下载失败");
                    break;
            }
        }
    }

    //下载到本地后执行安装
    protected static void installAPK(File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        HttpUtils.setADHttp(mBean.getFinishUrl());
    }



}
