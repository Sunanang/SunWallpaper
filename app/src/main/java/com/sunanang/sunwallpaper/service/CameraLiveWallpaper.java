package com.sunanang.sunwallpaper.service;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Camera;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.sunanang.sunwallpaper.util.SharedUtils;

import java.io.IOException;

/**
 * 透明壁纸
 */
public class CameraLiveWallpaper extends WallpaperService {

    //设置壁纸
    public static void setToWallPaper(Activity context) {
        SharedUtils.setStartStateShared(context,true);
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, CameraLiveWallpaper.class));
        context.startActivityForResult(intent,10);
    }



    // 实现WallpaperService必须实现的抽象方法  
    public Engine onCreateEngine() {
        // 返回自定义的CameraEngine
        return new CameraEngine();
    }


    class CameraEngine extends Engine implements Camera.PreviewCallback {
        private Camera camera;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            startPreview();
            // 设置处理触摸事件  
            setTouchEventsEnabled(false);

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopPreview();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                startPreview();
            } else {
                stopPreview();
            }
        }

        /**
         * 开始预览
         */
        public void startPreview() {
            try {
                camera = Camera.open();
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(getSurfaceHolder());
                camera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * 停止预览
         */
        public void stopPreview() {
            if (camera != null) {
                try {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    // camera.lock();
                    camera.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera = null;
            }
        }

        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            camera.addCallbackBuffer(bytes);
        }
    }
}  