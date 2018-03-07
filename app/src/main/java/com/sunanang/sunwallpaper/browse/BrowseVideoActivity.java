package com.sunanang.sunwallpaper.browse;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bamboyToast.BToast;
import com.sunanang.sunwallpaper.service.VideoLiveWallpaper;
import com.sunanang.sunwallpaper.util.GalleryUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BrowseVideoActivity extends AppCompatActivity{

    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private int postion = 0;
    private int screenWidth;
    private int screenHeight;
    private LinearLayout linBack,linSetting;
    private int typecode;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse_video);

        int[] screenParams = WallpaperUtils.getScreenParams(this);
        screenWidth = screenParams[0];
        screenHeight = screenParams[1];
        findViewByIds();
        initView();
        viewListener();

    }



    protected void findViewByIds() {
        surfaceview =  findViewById(R.id.surfaceView);

        linBack = findViewById(R.id.lin_video_back);
        linSetting = findViewById(R.id.lin_video_setting);

    }

    protected void initView() {
        Intent intent = getIntent();
        filePath = intent.getStringExtra("videoPath");
        typecode = intent.getIntExtra("typeCode",0);
        mediaPlayer = new MediaPlayer();
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceViewLis(filePath));
        // 调整surfaceView的大小

        ViewGroup.LayoutParams params = surfaceview.getLayoutParams();
        params.width = screenWidth;
        params.height = screenHeight;
        surfaceview.setLayoutParams(params);

    }

    private void viewListener() {
        linBack.setOnClickListener(onClickListener);
        linSetting.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.lin_video_back:
                    if(typecode == 1){
                        GalleryUtils.getInstance().setVideoActivity(BrowseVideoActivity.this);
                    }
                    finish();
                    break;
                case R.id.lin_video_setting:
                    WallpaperUtils.clearWall(BrowseVideoActivity.this);
                    SharedUtils.setFileShared(BrowseVideoActivity.this,filePath);
                    VideoLiveWallpaper.setToWallPaper(BrowseVideoActivity.this);
//                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (WallpaperUtils.isLiveWallpaperServiceRunning(BrowseVideoActivity.this,
                    "com.sunanang.sunwallpaper.service.VideoLiveWallpaper")) {
                //do something
                BToast.showText(BrowseVideoActivity.this,
                        "视频壁纸设置成功", true);
            }
        }
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {
        private String filePath;

        public SurfaceViewLis(String pathFile){
            filePath = pathFile;

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {}

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (postion == 0) {
                try {
                    play(filePath);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    public void play(String pathFile) throws IllegalArgumentException, SecurityException,
            IllegalStateException, IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(pathFile);
        mediaPlayer.setLooping(true);
        mediaPlayer.setDisplay(surfaceHolder);
        // 设置边播放变缓冲
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 通过异步的方式装载媒体资源
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 装载完毕回调
                mediaPlayer.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(typecode == 1){
            GalleryUtils.getInstance().setVideoActivity(BrowseVideoActivity.this);
        }

        finish();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
