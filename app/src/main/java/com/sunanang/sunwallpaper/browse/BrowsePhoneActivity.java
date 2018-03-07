package com.sunanang.sunwallpaper.browse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bamboyToast.BToast;
import com.sunanang.sunwallpaper.util.GalleryUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BrowsePhoneActivity extends AppCompatActivity{

    private ImageView imageView;
    private LinearLayout linBack,linSetting,setBackground;
    private int typecode;
    private String imagePath;
    private int width,height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse);
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("ImagePath");

        typecode = intent.getIntExtra("typeCode",0);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView = findViewById(R.id.browse_image);

        linBack = findViewById(R.id.lin_pic_back);
        linSetting = findViewById(R.id.lin_pic_setting);
        setBackground = findViewById(R.id.lin_pic_background);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = width;
        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(lp);
        imageView.setMaxWidth(width);
        imageView.setMaxHeight(width * 10); //这里其实可以根据需求而定，我这里测试为最大宽度的5倍
        imageView.setImageBitmap(bitmap);

        viewListener();


    }

    private void viewListener() {
        linBack.setOnClickListener(onClickListener);
        linSetting.setOnClickListener(onClickListener);
        setBackground.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.lin_pic_back:
                    if(typecode == 1){
                        GalleryUtils.getInstance().setGalleryActivity(BrowsePhoneActivity.this);
                    }

                    finish();
                    break;
                case R.id.lin_pic_setting:
//                    VideoLiveWallpaper.setToWallPaper(BrowsePhoneActivity.this);
                    WallpaperUtils.setWallPaper(BrowsePhoneActivity.this,width,height,imagePath);
                    BToast.showText(BrowsePhoneActivity.this, "壁纸设置成功",
                            true);
                    finish();
                    break;
                case R.id.lin_pic_background:
                    SharedUtils.setBackgroundShared(BrowsePhoneActivity.this,imagePath);
                    BToast.showText(BrowsePhoneActivity.this, "应用背景设置成功",
                            true);
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(typecode == 1){
            GalleryUtils.getInstance().setGalleryActivity(BrowsePhoneActivity.this);
        }

        finish();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
