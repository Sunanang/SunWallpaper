package com.sunanang.sunwallpaper.downLoad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bamboyToast.BToast;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DownLoadPicActivity extends AppCompatActivity {

    private LinearLayout down,background,wallpaper;
    private String picUrl,picName,downUrl;
    private ImageView back;
    private downSuccess downSuccess;
    private String picSavePath;
    private String backgroundShared;

    private void setDwonSuccess(downSuccess downSuccess){
        this.downSuccess = downSuccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_pic);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        backgroundShared = SharedUtils.getBackgroundShared(DownLoadPicActivity.this);
        if (!TextUtils.isEmpty(backgroundShared)) {
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        init();
    }

    private void init() {
        Intent intent = getIntent();
        picUrl = intent.getStringExtra("picUrl");
        picName = intent.getStringExtra("picName");
        downUrl = intent.getStringExtra("picDownUrl");


        TextView title = findViewById(R.id.tv_title);
        ImageView img = findViewById(R.id.pic_img);
        back = findViewById(R.id.img_back);
        findViewById(R.id.view_view).setVisibility(View.GONE);
        down = findViewById(R.id.lin_pic_down);
        background = findViewById(R.id.lin_pic_background);
        wallpaper = findViewById(R.id.lin_pic_setting);
        findViewById(R.id.relative).setBackgroundColor(getResources().getColor(R.color.c50000));
        title.setText(picName);
        back.setVisibility(View.VISIBLE);
        back.setClickable(true);
        back.setOnClickListener(listener);
        background.setOnClickListener(listener);
        wallpaper.setOnClickListener(listener);
        down.setOnClickListener(listener);

        Glide.with(DownLoadPicActivity.this).load(picUrl).into(img);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.lin_pic_down:
                    String[] split = downUrl.split("/");
                    String fileName = split[split.length - 1];
                    picSavePath = FileUtils2.getPicSavePath(fileName);
                    File dir1 = new File(picSavePath);
                    if(!dir1.exists()){
                        Toast.makeText(DownLoadPicActivity.this,"壁纸下载中，请稍后。。。",Toast.LENGTH_SHORT).show();
                        savePicture(downUrl,dir1);
                        setDwonSuccess(new downSuccess() {
                            @Override
                            public void downSuccessBitmap() {
                                showToast("下载成功");
                            }

                            @Override
                            public void downError() {
                                showToastErr("下载失败，请重新下载");
                            }
                        });
                    }else
                        Toast.makeText(DownLoadPicActivity.this,"壁纸已存在",Toast.LENGTH_SHORT).show();

                    break;
                case R.id.lin_pic_background:
                    split = downUrl.split("/");
                    fileName = split[split.length - 1];
                    picSavePath = FileUtils2.getPicSavePath(fileName);
                    dir1 = new File(picSavePath);
                    if(!dir1.exists()){
                        Toast.makeText(DownLoadPicActivity.this,"壁纸下载中，请稍后。。。",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DownLoadPicActivity.this,"正在设置，请稍等。。。",Toast.LENGTH_SHORT).show();
                    }
                    savePicture(downUrl,dir1);
                    setDwonSuccess(new downSuccess() {
                        @Override
                        public void downSuccessBitmap() {
                            SharedUtils.setBackgroundShared(DownLoadPicActivity.this,picSavePath);
                            String background = SharedUtils.getBackgroundShared(DownLoadPicActivity.this);
                            if (!backgroundShared.equals(background)) {
                                findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(DownLoadPicActivity.this,backgroundShared));
                            }
                            showToast("设置成功");
                        }

                        @Override
                        public void downError() {
                            showToastErr("设置失败，请重新设置");
                        }
                    });
                    break;
                case R.id.lin_pic_setting:
                    split = downUrl.split("/");
                    fileName = split[split.length - 1];
                    picSavePath = FileUtils2.getPicSavePath(fileName);
                    dir1 = new File(picSavePath);
                    if(!dir1.exists()){
                        Toast.makeText(DownLoadPicActivity.this,"壁纸下载中，请稍后。。。",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DownLoadPicActivity.this,"正在设置，请稍等。。。",Toast.LENGTH_SHORT).show();
                    }
                    savePicture(downUrl,dir1);
                    setDwonSuccess(new downSuccess() {
                        @SuppressLint("NewApi")
                        @Override
                        public void downSuccessBitmap() {
                            int[] screenParams = WallpaperUtils.getScreenParams(DownLoadPicActivity.this);
                            WallpaperUtils.setWallPaper(DownLoadPicActivity.this,
                                    screenParams[0],screenParams[1],picSavePath);
                            showToast("设置成功");
                        }

                        @Override
                        public void downError() {
                            showToastErr("设置失败，请重新设置");
                        }
                    });
                    break;
            }
        }
    };


    //Glide保存图片
    public void savePicture(String url, final File file){
        Glide.with(DownLoadPicActivity.this).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(final byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        domwBitmap(bytes,file);
                    }
                }.start();

            }
        });
    }

    private void domwBitmap(byte[] bytes,File file){
        try {
//            String[] split = downUrl.split("/");
//            String fileName = split[split.length - 1];
//            picSavePath = FileUtils2.getPicSavePath(fileName);
//            File dir1 = new File(picSavePath);
            if(!file.exists()){
                FileOutputStream output = new FileOutputStream(file);
                output.write(bytes);
                //将bytes写入到输出流中
                output.close();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downSuccess.downSuccessBitmap();
                }
            });

        } catch (final Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("Sunanang",e.getMessage());
                    downSuccess.downError();
                }
            });


        }
    }


    private void showToast(String data){
        BToast.showText(DownLoadPicActivity.this, data, true);
    }

    private void showToastErr(String data){
        BToast.showText(DownLoadPicActivity.this, data, false);
    }



    interface downSuccess{
        void downSuccessBitmap();

        void downError();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
