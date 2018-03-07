package com.sunanang.sunwallpaper.service;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sunanang.sunwallpaper.browse.BrowsePhoneActivity;
import com.sunanang.sunwallpaper.util.SharedUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2017/6/26.
 * 视频壁纸
 */
public class VideoLiveWallpaper extends WallpaperService {




    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.zhy.livewallpaper";
    public static final String KEY_ACTION = "action";
    public static final int ACTION_VOICE_SILENCE = 110;
    public static final int ACTION_VOICE_NORMAL = 111;

    //静音
    public static void voiceSilence(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    //正常（关于声音）
    public static void voiceNormal(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    //设置壁纸
    public static void setToWallPaper(Activity context) {
        SharedUtils.setStartStateShared(context,true);
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, VideoLiveWallpaper.class));
//        context.startActivity(intent);
        context.startActivityForResult(intent,10);
    }


    class VideoEngine extends Engine {

        private MediaPlayer mMediaPlayer;

        private BroadcastReceiver mVideoParamsControlReceiver;

        private String fileShared;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            fileShared = SharedUtils.getFileShared(getApplicationContext());
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(mVideoParamsControlReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra(KEY_ACTION, -1);
                    switch (action) {
                        case ACTION_VOICE_NORMAL:
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                        case ACTION_VOICE_SILENCE:
                            mMediaPlayer.setVolume(0, 0);
                            break;

                    }
                }
            }, intentFilter);

            // 设置处理触摸事件
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            unregisterReceiver(mVideoParamsControlReceiver);
            super.onDestroy();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(holder.getSurface());
            try {

                if(fileShared.indexOf(".mp4") != -1){
                    mMediaPlayer.setDataSource(fileShared);  //设置本地路径视频
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setVolume(0, 0);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
//                    BToast.showText(getApplication(), "视频壁纸设置成功", true);
                }else {
                    Intent intent = new Intent(VideoLiveWallpaper.this, BrowsePhoneActivity.class);
                    intent.putExtra("ImagePath",fileShared);
                    intent.putExtra("typeCode",0);
                    startActivity(intent);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mMediaPlayer.release();
            mMediaPlayer = null;

        }

    }


}
