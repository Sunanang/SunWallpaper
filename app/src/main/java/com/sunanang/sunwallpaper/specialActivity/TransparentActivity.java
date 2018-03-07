package com.sunanang.sunwallpaper.specialActivity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.browse.BrowsePhoneActivity;
import com.sunanang.sunwallpaper.browse.BrowseVideoActivity;
import com.sunanang.sunwallpaper.service.CameraLiveWallpaper;
import com.sunanang.sunwallpaper.service.ClockWallpaperService;
import com.sunanang.sunwallpaper.util.GalleryUtils;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TransparentActivity extends AppCompatActivity {
    private ImageView back,transparent;
    private TextView title;
    private boolean transparentShared;
    private RelativeLayout trendsGL,click,sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String backgroundShared = SharedUtils.getBackgroundShared(TransparentActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        init();
    }

    private void init() {
        trendsGL = findViewById(R.id.latout_trendsGL);
        back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        title = findViewById(R.id.tv_title);
        title.setText(R.string.special);
        transparent = findViewById(R.id.img_check_transparent);
        click = findViewById(R.id.latout_click);
        sun = findViewById(R.id.latout_sun);
        transparentShared = WallpaperUtils.isLiveWallpaperServiceRunning(
                TransparentActivity.this,
                "com.sunanang.sunwallpaper.service.CameraLiveWallpaper");
        if (transparentShared) {
            transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_on));
        }else {
            transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
        }


        initListener();
    }

    private void initListener() {
        transparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transparentShared = !transparentShared;
                if(transparentShared){ //设置为透明
                    transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_on));
                    CameraLiveWallpaper.setToWallPaper(TransparentActivity.this);
                }else { //设置回来
                    transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
                    Intent intent = new Intent(TransparentActivity.this, CameraLiveWallpaper.class);
                    stopService(intent);
                    String fileShared = SharedUtils.getFileShared(getApplicationContext());
                    if(fileShared.contains(".mp4")){
                        Intent intent1 =  new Intent(TransparentActivity.this, BrowseVideoActivity.class);
                        intent1.putExtra("videoPath",fileShared);
                        intent1.putExtra("typeCode",1);
                        startActivity(intent1);
                    }else {
                        Intent intent1 = new Intent(TransparentActivity.this, BrowsePhoneActivity.class);
                        intent1.putExtra("ImagePath",fileShared);
                        intent1.putExtra("typeCode",1);
                        startActivity(intent1);
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        trendsGL.setOnClickListener(listener);
        click.setOnClickListener(listener);
        sun.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.latout_trendsGL:
                    GalleryUtils.getInstance().setGalleryPicWallActivity(TransparentActivity.this,1);
                    break;
                case R.id.latout_click:
                    GalleryUtils.getInstance().setGalleryPicWallActivity(TransparentActivity.this,2);
                    break;
                case R.id.latout_sun:
                    Intent intent = new Intent(TransparentActivity.this,ShadowActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            transparentShared = WallpaperUtils.isLiveWallpaperServiceRunning(
                    TransparentActivity.this,
                    "com.sunanang.sunwallpaper.service.CameraLiveWallpaper");
            if (transparentShared) {
                transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_on));
            }else {
                transparent.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
            }
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
