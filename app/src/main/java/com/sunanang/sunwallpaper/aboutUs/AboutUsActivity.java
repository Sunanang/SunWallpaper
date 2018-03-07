package com.sunanang.sunwallpaper.aboutUs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.util.DownloadUtils;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.ShareToTencentUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.SystemInfoUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutUsActivity extends AppCompatActivity {
    private String backgroundShared;
    private ImageView wechat,zf;
    private TextView  website,shareToQz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        backgroundShared = SharedUtils.getBackgroundShared(AboutUsActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(AboutUsActivity.this,backgroundShared));
        }
        init();
    }

    private void init() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.main_aboutUs);
        ImageView back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(listener);
        TextView version = findViewById(R.id.tv_version);
        String name = SystemInfoUtils.getLocalVersionName(AboutUsActivity.this);
        version.setText("v" + name);
        wechat = findViewById(R.id.img_wechat);
        zf = findViewById(R.id.img_zf);
        wechat.setOnLongClickListener(longClickListener);
        zf.setOnLongClickListener(longClickListener);
        shareToQz = findViewById(R.id.shareToQz);
        shareToQz.setOnClickListener(listener);
        website = findViewById(R.id.website);
        String s1 = (String) website.getText();
        SpannableString text = new SpannableString(s1);
        NoUnderLineSpan noUnderLineSpan = new NoUnderLineSpan("http://paipaiwall.bmob.site");
        text.setSpan(noUnderLineSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        website.setText(text);
        website.setMovementMethod(LinkMovementMethod.getInstance());



        SharedUtils.setAboutFirstShared(AboutUsActivity.this,true);


    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.shareToQz:
                    ShareToTencentUtils.joinQQGroup(AboutUsActivity.this);
                    break;
            }
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.img_wechat:
                    Bitmap imageWechat = ((BitmapDrawable)wechat.getDrawable()).getBitmap();
                    boolean res = DownloadUtils.saveImageToGallery(AboutUsActivity.this,imageWechat);
                    getToast(res);
                    break;
                case R.id.img_zf:
                    Bitmap imageZf = ((BitmapDrawable)zf.getDrawable()).getBitmap();
                    boolean resZf = DownloadUtils.saveImageToGallery(AboutUsActivity.this,imageZf);
                    getToast(resZf);
                    break;
            }
            return false;
        }
    };


    private void getToast(boolean isSuccess){
        if (isSuccess){
            Toast.makeText(AboutUsActivity.this,"图片下载成功",Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(AboutUsActivity.this,"图片下载失败",Toast.LENGTH_SHORT).show();

    }


    @SuppressLint("ParcelCreator")
    public static class NoUnderLineSpan extends URLSpan {
        public NoUnderLineSpan(String src) {
            super(src);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(Color.parseColor("#00B2EE"));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
