package com.sunanang.sunwallpaper.downLoad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.browse.BrowseVideoActivity;
import com.sunanang.sunwallpaper.service.VideoLiveWallpaper;
import com.sunanang.sunwallpaper.util.DownloadUtils;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;
import com.sunanang.sunwallpaper.view.FlikerProgressBar;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DownLoadVideoActivity extends AppCompatActivity  {
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private int postion = 0;
    private String filePath,fileName;
    private TextView title;
    private ImageView back;
    private ProgressBar loadPro;
    private FlikerProgressBar roundProgressbar;
    private int fileSize;
    private String videoSavePath;
    private TextView size;
    private LinearLayout error;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_video);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String backgroundShared = SharedUtils.getBackgroundShared(DownLoadVideoActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }

        findViewByIds();
        initView();
    }
    protected void findViewByIds() {
        surfaceview =  findViewById(R.id.surfaceView);
        title = findViewById(R.id.tv_title);
        back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(listener);
        loadPro = findViewById(R.id.load_progress);
        loadPro.setVisibility(View.VISIBLE);
        size = findViewById(R.id.tv_video_size);

        roundProgressbar = findViewById(R.id.round_flikerbar);
        roundProgressbar.setFisrt(true);

        error = findViewById(R.id.errors);

    }

    protected void initView() {
        Intent intent = getIntent();
        filePath = intent.getStringExtra("videoUrl");
        if(!TextUtils.isEmpty(filePath)){

            fileName = intent.getStringExtra("videoName");
            fileSize = intent.getIntExtra("videoSize",0);
            title.setText(fileName);
            size.setText("壁纸大小：" + fileSize + "MB");
            mediaPlayer = new MediaPlayer();
            surfaceHolder = surfaceview.getHolder();
            surfaceHolder.setKeepScreenOn(true);
            surfaceHolder.addCallback(new SurfaceViewLis(filePath,postion));

            roundProgressbar.setOnClickListener(listener);
        }else {
            //文件异常，
            error.setVisibility(View.VISIBLE);
            loadPro.setVisibility(View.GONE);
            return;
        }



    }




    private void viewListener() {
        String[] split = filePath.split("/");
        String name = split[split.length - 1];
        videoSavePath = FileUtils2.getVideoSavePath(name);
        DownloadUtils.download(filePath,videoSavePath,new DownloadUtils.OnDownloadListener(){
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(File file) {
                roundProgressbar.setProgress(100);
                roundProgressbar.finishLoad();
            }

            @Override
            public void onFail(File file, String failInfo) {
            }

            @Override
            public void onProgress(int progress) {
                roundProgressbar.setProgress(progress);

            }
        });
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.round_flikerbar:

                    if(!roundProgressbar.isFinish()){
                        if (roundProgressbar.isFirst()){
                            roundProgressbar.setFisrt(false);
                            roundProgressbar.toggle();

                            if(roundProgressbar.isStop()){
                            } else {
                                viewListener();
                            }
                        }
                        roundProgressbar.toggle();

                        if(roundProgressbar.isStop()){
                        } else {
                            viewListener();
                        }

                    }else {
//                        Intent intent = new Intent(DownLoadVideoActivity.this,)
//                        GalleryUtils.getInstance().GetVideoFileName();
                        WallpaperUtils.clearWall(DownLoadVideoActivity.this);
                        SharedUtils.setFileShared(DownLoadVideoActivity.this,videoSavePath);
                        VideoLiveWallpaper.setToWallPaper(DownLoadVideoActivity.this);
                    }
                    break;
            }
        }
    };




    @Override
    protected void onPause() {
        super.onPause();
        if(!TextUtils.isEmpty(filePath))
            mediaPlayer.pause();
//        roundProgressbar.isStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!TextUtils.isEmpty(filePath)){
            mediaPlayer.stop();
//        pregress_down.stopDownloading();
            roundProgressbar.destroyDrawingCache();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    private class SurfaceViewLis implements SurfaceHolder.Callback {
        private String filePath;
        private int position;

        public SurfaceViewLis(String pathFile,int position){
            filePath = pathFile;
            this.position = position;

        }


        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {}

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (position == 0) {
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
                loadPro.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
