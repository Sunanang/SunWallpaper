package com.sunanang.sunwallpaper.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.aiyaapp.camera.sdk.AiyaEffects;
import com.aiyaapp.camera.sdk.base.ActionObserver;
import com.aiyaapp.camera.sdk.base.Event;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.aboutUs.AboutUsActivity;
import com.sunanang.sunwallpaper.appSettings.AppSettingsActivity;
import com.sunanang.sunwallpaper.bamboyToast.CenterDialog;
import com.sunanang.sunwallpaper.camera.CameraActivity;
import com.sunanang.sunwallpaper.clear.DataCleanManager;
import com.sunanang.sunwallpaper.instructions.InstructionsActivity;
import com.sunanang.sunwallpaper.opinion.OpinionActivity;
import com.sunanang.sunwallpaper.service.VideoLiveWallpaper;
import com.sunanang.sunwallpaper.specialActivity.TransparentActivity;
import com.sunanang.sunwallpaper.util.GalleryUtils;
import com.sunanang.sunwallpaper.unity.UnityPicActivity;
import com.sunanang.sunwallpaper.unity.UnityVideoActivity;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.ShareToTencentUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.SystemInfoUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import cn.bmob.v3.update.BmobUpdateAgent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CenterDialog.OnCenterItemClickListener {


    private LinearLayout baseLayout;
    private ImageView volume;  //设置声音
    private ImageView sign,signInst;//标记，点击后不显示
    private static boolean volumeShared;
    private CenterDialog centerDialog;
    private String backgroundShared;
    private TextView clear;
    //记录用户首次点击返回键的时间
    private long firstTime=0;
    private PopupWindow popWindow;
    private View gray;
    private Tencent mTencent;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //权限申请
        requestPermission();
        baseLayout = findViewById(R.id.lin_base);
        backgroundShared = SharedUtils.getBackgroundShared(MainActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            baseLayout.setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        final ActionObserver observer=new ActionObserver() {
            @Override
            public void onAction(Event event) {
                AiyaEffects.getInstance().unRegisterObserver(this);
//                if(event.eventType== Event.RESOURCE_FAILED){
//
//                }else if(event.eventType== Event.RESOURCE_READY){
//                }else if(event.eventType== Event.INIT_FAILED){
//                    Toast.makeText(MainActivity.this, "注册失败，请检查网络", Toast.LENGTH_SHORT).show();
//                    AiyaEffects.getInstance().unRegisterObserver(this);
//                }else if(event.eventType== Event.INIT_SUCCESS){
//                    AiyaEffects.getInstance().unRegisterObserver(this);
//                }
            }
        };
        mTencent = Tencent.createInstance("1106548587",MainActivity.this);
        api = WXAPIFactory.createWXAPI(this,ShareToTencentUtils.app_id,true);
        api.registerApp(ShareToTencentUtils.app_id);
        AiyaEffects.getInstance().registerObserver(observer);
        AiyaEffects.getInstance().init(MainActivity.this,"a817bf2c10e40dc83a9e8757d33fb021");
        init();

    }


    /**
     * 布局创建
     */
    private void init(){
        volume = findViewById(R.id.img_check_volume);
        TextView version = findViewById(R.id.tv_version);
        clear = findViewById(R.id.tv_clear);
        gray = findViewById(R.id.gray_layout);

        String name = SystemInfoUtils.getLocalVersionName(MainActivity.this);
        version.setText("v" + name);
        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(MainActivity.this);
            clear.setText(cacheSize);

        } catch (Exception e) {
            e.printStackTrace();
        }

        volumeShared = SharedUtils.getVolumeShared(MainActivity.this);
        if(volumeShared){
            VideoLiveWallpaper.voiceNormal(MainActivity.this); //有声
//            volume.setImageDrawable();
            volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_on));
        }else {
            VideoLiveWallpaper.voiceSilence(MainActivity.this); //无声
            volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
        }
        centerDialog = new CenterDialog(this, R.layout.dialog_layout,
                new int[]{R.id.dialog_cancel, R.id.dialog_sure},
                new int[]{R.string.voice_dialog_cancel,R.string.voice_dialog_sure},
                R.id.dialog_text,R.string.voice_dialog);
        centerDialog.setOnCenterItemClickListener(MainActivity.this);

        sign = findViewById(R.id.img_sign);
        signInst = findViewById(R.id.img_sign_inst);
        //判断
        boolean isFirst = SharedUtils.getAboutFirstShared(this);
        if (isFirst){
            sign.setVisibility(View.GONE);
        }
        boolean isInst = SharedUtils.getInstFirstShared(this);
        if (isInst){
            signInst.setVisibility(View.GONE);
        }
        initListener();
    }

    private void initListener() {
        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeShared = !volumeShared;
                if(volumeShared){
                    centerDialog.show();
                }else {
                    VideoLiveWallpaper.voiceSilence(MainActivity.this); //无声
                    volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
                    SharedUtils.setVolumeShared(MainActivity.this,volumeShared);
                }
            }
        });

    }



    /**
     * 动态申请权限
     */
    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=
                        PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断申请码
        int code = 0;
        for(int results:grantResults){
            code += results;
        }
        if(code == 0){
            Toast.makeText(this,"申请权限成功", Toast.LENGTH_SHORT).show();
        }else{
            //申请的第一个权限失败后
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.latout_camera:
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_gallery:
                GalleryUtils.getInstance().setGalleryActivity(MainActivity.this);
                break;
            case R.id.tv_videoLib:
                GalleryUtils.getInstance().setVideoActivity(MainActivity.this);
                break;
            case R.id.layout_especially:
                Intent intent1 = new Intent(MainActivity.this, TransparentActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_shop:
                intent1 = new Intent(MainActivity.this, UnityPicActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_community:
                intent1 = new Intent(MainActivity.this, UnityVideoActivity.class);
                startActivity(intent1);
                break;
            case R.id.main_clear_garbage:
                DataCleanManager.clearAllCache(MainActivity.this);
                Toast.makeText(MainActivity.this,"缓存数据清理完成",Toast.LENGTH_SHORT).show();
                try {
                    clear.setText(DataCleanManager.getTotalCacheSize(MainActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.main_version: //版本更新
                BmobUpdateAgent.silentUpdate(this);   //静默下载
                Toast.makeText(MainActivity.this,"正在检测版本更新，请稍后。。。",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_inst: //使用说明
                intent1 = new Intent(MainActivity.this, InstructionsActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_opinion: //意见反馈
                intent1 = new Intent(MainActivity.this, OpinionActivity.class);
                startActivity(intent1);
                break;
            case R.id.main_about: //关于我们
                intent1 = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_share:  //分享
                showPopwindow();
                break;
            case R.id.re_evaluate: //评分
                setScore();
                break;
            case R.id.tv_setting: //应用设置
                intent1 = new Intent(MainActivity.this, AppSettingsActivity.class);
                startActivity(intent1);
                break;
        }

    }

    @Override
    public void OnCenterItemClick(CenterDialog dialog, View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                volumeShared = false;
                SharedUtils.setVolumeShared(MainActivity.this,volumeShared);
                VideoLiveWallpaper.voiceSilence(MainActivity.this); //无声
                volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
                break;
            case R.id.dialog_cancel:
                volumeShared = true;
                SharedUtils.setVolumeShared(MainActivity.this,volumeShared);
                VideoLiveWallpaper.voiceNormal(MainActivity.this); //有声
                volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_on));
                break;
            default:

                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(centerDialog.isShowing()){
            volumeShared = false;
            SharedUtils.setVolumeShared(MainActivity.this,volumeShared);
            VideoLiveWallpaper.voiceSilence(MainActivity.this); //无声
            volume.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_switch_off));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String background = SharedUtils.getBackgroundShared(MainActivity.this);
        if(!TextUtils.isEmpty(background)){
            if(!backgroundShared.equals(background)){
                baseLayout.setBackground(FileUtils2.compressBmp(this,background));
                backgroundShared = background;
            }
            try {
                clear.setText(DataCleanManager.getTotalCacheSize(MainActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            baseLayout.setBackgroundResource(R.drawable.timg);
            backgroundShared = null;
        }

        boolean isFirst = SharedUtils.getAboutFirstShared(MainActivity.this);

        if (isFirst){
            sign.setVisibility(View.GONE);
        }

        boolean isInst = SharedUtils.getInstFirstShared(this);
        if (isInst){
            signInst.setVisibility(View.GONE);
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                Toast.makeText(MainActivity.this,"再按一次退出『拍拍壁纸』",Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showPopwindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.camera_pop_menu, null);

        LinearLayout wechatFirs = popView.findViewById(R.id.lin_wechat_firs);
        LinearLayout wechat = popView.findViewById(R.id.lin_wechat);
        LinearLayout space = popView.findViewById(R.id.lin_space);
        LinearLayout qq = popView.findViewById(R.id.lin_qq);
        TextView btnCancel = popView.findViewById(R.id.btn_camera_pop_cancel);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popWindow = new PopupWindow(popView,width,height);
        popWindow.setAnimationStyle(R.style.bottom_menu_animation);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lin_wechat_firs: //朋友圈
                        ShareToTencentUtils.setWechatShare(2,MainActivity.this,api);
                        break;
                    case R.id.lin_wechat://微信
                        ShareToTencentUtils.setWechatShare(1,MainActivity.this,api);
                        break;
                    case R.id.lin_space: //空间
                        ShareToTencentUtils.shareToQzone(MainActivity.this,mTencent);
                        break;
                    case R.id.lin_qq: //qq
                        ShareToTencentUtils.onClickStory(MainActivity.this,mTencent);
                        break;
                    case R.id.btn_camera_pop_cancel:
                        //取消
                        break;
                }
                popWindow.dismiss();
                gray.setVisibility(View.GONE);
            }
        };
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                gray.setVisibility(View.GONE);
            }
        });

        wechatFirs.setOnClickListener(listener);
        wechat.setOnClickListener(listener);
        space.setOnClickListener(listener);
        qq.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);

        gray.setVisibility(View.VISIBLE);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    /**
     * 应用评分
     */
    private void setScore(){
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://search?q="+this.getPackageName()));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "您的手机没有安装Android应用市场,请您前往网站评分", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
