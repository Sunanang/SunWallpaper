package com.sunanang.sunwallpaper.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.luomi.lm.ad.ADType;
import com.luomi.lm.ad.DRAgent;
import com.luomi.lm.ad.IAdSuccessBack;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bean.ADBean;
import com.sunanang.sunwallpaper.util.DownloadUtils;
import com.sunanang.sunwallpaper.util.HttpUtils;
import com.sunanang.sunwallpaper.util.JsonUtils;
import com.sunanang.sunwallpaper.util.PermissionUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.SystemInfoUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;



public class SplashActivity extends AppCompatActivity{
    private LinearLayout layout;
    private int type;
    private String adUrl;
    private ImageView adImg;
    private ADBean adData = new ADBean();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                10,mRunnable);
    }


    private Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            setContentView(R.layout.activity_splash);
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
            layout = findViewById(R.id.splash);
            adImg = findViewById(R.id.img_ad);

            Intent intent = getIntent();
            type = intent.getIntExtra("type", 0);




            setSdkAd();
//            setApiAd();

            //检查网络状态
            boolean state = HttpUtils.checkNetworkState(SplashActivity.this);
            if(!state ){
            new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    //实现页面跳转
                    if(type != 1)
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                    return false;
                }
            }).sendMessageDelayed(new Message(),3000);
            }
        }};


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,  int[] grantResults) {

        PermissionUtils.requestPermissionsResult(requestCode == 10, grantResults, mRunnable, new Runnable() {
            @Override
            public void run() {
                SharedUtils.setFirstShared(SplashActivity.this,true);
                finish();
                Toast.makeText(SplashActivity.this,"必要的权限未被允许",Toast.LENGTH_SHORT).show();
            }
        });
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /**
     * sdk请求广告
     */
    private void setSdkAd(){
        DRAgent.getInstance().init(SplashActivity.this, "844b3e127a0655cc3e1a6dbaa41fd8fd", true);
        DRAgent.getInstance().getOpenView(SplashActivity.this, ADType.FULL_SCREEN , true, new IAdSuccessBack() {
            @Override
            public void onError(final String result) {
                // TODO Auto-generated method stub
                if(type != 1)
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
            @Override
            public void onClick(String result) {
                // TODO Auto-generated method stub
            }
            @Override
            public void OnSuccess(String result) {
                // TODO Auto-generated method stub
                if(type != 1)
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
            @Override
            public void OnLoadAd(View view) {
                // TODO Auto-generated method stub
                layout.addView(view);
            }
        });
    }


    /**
     * api请求广告
     */
    private void setApiAd(){
                    String imei = SystemInfoUtils.getIMEI(getApplication());
            int[] wh = SystemInfoUtils.getWH(SplashActivity.this);

            adUrl = "http://sdk.cferw.com/api.php?z=6255&appkey=e9a3b26f6e5193e536ed8659347d7c4c&deviceId="
                    + imei+ "&sw=" + wh[0]+ "&sh=" + wh[1] + "&osver=6.5";
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String data = HttpUtils.setHttp(adUrl);
                    adData = JsonUtils.getAdData(data);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(TextUtils.isEmpty(adData.getCountUrl())){
                                new Handler(new Handler.Callback() {
                                    @Override
                                    public boolean handleMessage(Message msg) {
                                        //实现页面跳转
                                        if(type != 1)
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                        return false;
                                    }
                                }).sendMessageDelayed(new Message(),3000);
                            }else {
                                HttpUtils.setADHttp(adData.getCountUrl());
                                Glide.with(SplashActivity.this).load(adData.getImgUrl()).into(adImg);
                                layout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getADClick(adData);
                                    }
                                });
                            }
                        }
                    });
                }
            }.start();
    }

    /**
     *  api广告点击事件
     * @param bean
     */
    private void getADClick(ADBean bean){
        int isLink = bean.getIsLink();

        HttpUtils.setHttp(bean.getClickUrl());
        if(isLink == 1){
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(bean.getGotourl());
            intent.setData(content_url);
            startActivity(intent);
        }else {
            HttpUtils.setHttp(bean.getSdownUrl());
            String[] split = bean.getSdownUrl().split("/");
            String s = split[split.length - 1];
            if ( s.indexOf(".apk") == -1){
                s += ".apk";
            }
            DownloadUtils.downloadAPK(bean.getGotourl(),s,SplashActivity.this,bean);
        }
    }

}
