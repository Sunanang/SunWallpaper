package com.sunanang.sunwallpaper.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.activity.SettingsActivity;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.SystemInfoUtils;
import com.sunanang.sunwallpaper.view.AnalogClock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Clock wallpaper service
 *
 * @author sylsau - sylvain.saurel@gmail.com - http://www.ssaurel.com
 *
 */
public class ClockWallpaperService extends WallpaperService {

    private static Context mContext;
    private static int[] wh;

    //设置壁纸
    public static void setToClockWallPaper(Activity context) {
        SharedUtils.setStartStateShared(context,true);
        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, ClockWallpaperService.class));
        context.startActivity(intent);
        mContext = context;
        wh = SystemInfoUtils.getWH(context);

    }


	@Override
	public Engine onCreateEngine() {
		return new ClockWallpaperEngine(mContext,wh);
	}

	private class ClockWallpaperEngine extends Engine implements
            OnSharedPreferenceChangeListener {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}

		};

		private Paint paint;
		/** hands colors for hour, min, sec */
		private int[] colors = { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF };
		private int bgColor;
		private int width;
		private int height;
		private boolean visible = true;
		private boolean displayHandSec;
		private AnalogClock clock;
		private SharedPreferences prefs;
		private Context mContext;
		private int[] screenWH;

		public ClockWallpaperEngine(Context context,int[] screen) {
			prefs = PreferenceManager
					.getDefaultSharedPreferences(ClockWallpaperService.this);
			prefs.registerOnSharedPreferenceChangeListener(this);
			displayHandSec = prefs.getBoolean(
					SettingsActivity.DISPLAY_HAND_SEC_KEY, true);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5);
			bgColor = Color.parseColor("#C0C0C0");
			clock = new AnalogClock(getApplicationContext());
			handler.post(drawRunner);
            mContext = context;
            screenWH = screen;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
			prefs.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					draw(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}

			handler.removeCallbacks(drawRunner);

			if (visible) {
				handler.postDelayed(drawRunner, 200);
			}
		}


        @SuppressLint("ResourceType")
        private void draw(Canvas canvas) {
//
            Bitmap bitmap = null;
            String paths = SharedUtils.getFileClockShared(mContext);
            if (!TextUtils.isEmpty(paths)){
                bitmap = BitmapFactory.decodeFile(paths);
                bitmap = FileUtils2.setImgSize(bitmap, screenWH[0], screenWH[1]);
            }else {
                Toast.makeText(mContext,R.string.bitmapError,Toast.LENGTH_SHORT).show();
                InputStream is = mContext.getResources().openRawResource(R.drawable.timg);
                try {
                    //BitmapFactory is an Android graphics utility for images
                    bitmap = BitmapFactory.decodeStream(is);
                } finally {
                    //Always clear and close
                    try {
                        is.close();
                        is = null;
                    } catch (IOException e) {
                    }
                }
            }
//			canvas.drawColor(bgColor);
            canvas.drawBitmap(bitmap,0,0,paint);
			clock.config(width / 2, height / 2, (int) (width * 0.6f),
					new Date(), paint, colors, displayHandSec);
			clock.draw(canvas);
		}

		@Override
		public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
			if (SettingsActivity.DISPLAY_HAND_SEC_KEY.equals(key)) {
				displayHandSec = sharedPreferences.getBoolean(
						SettingsActivity.DISPLAY_HAND_SEC_KEY, true);
			}
		}

	}

}
