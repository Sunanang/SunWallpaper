package com.sunanang.sunwallpaper.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.aiyaapp.camera.sdk.base.FrameCallback;
import com.aiyaapp.camera.sdk.widget.CameraView;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.browse.BrowseVideoActivity;
import com.sunanang.sunwallpaper.coder.CameraRecorder;
import com.sunanang.sunwallpaper.ui.EffectSelectActivity;
import com.sunanang.sunwallpaper.util.PermissionUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Description:
 */
public class CameraActivity extends EffectSelectActivity implements FrameCallback{

    private CameraView mCameraView;
    private int bmpWidth=720,bmpHeight=1280;

    private CircularProgressView mCapture;
    private long time;
    private ExecutorService mExecutor;
    private long maxTime=20000;
    private long timeStep=50;
    private boolean recordFlag=false;
    private int type;       //1为拍照，0为录像
    private CameraRecorder mp4Recorder;
    private static boolean firstShared;
    private ImageView mLift;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},10,mRunnable);
    }

    private CameraView.CameraController mController=new CameraView.CameraController(){
        @Override
        protected void otherSetting(Camera.Parameters param) {
            super.otherSetting(param);

            bmpHeight=param.getPreviewSize().width;
            bmpWidth=param.getPreviewSize().height;
            mCameraView.setFrameCallback(bmpWidth,bmpHeight,CameraActivity.this);
        }
    };

    private Runnable mRunnable=new Runnable() {
        @Override
        public void run() {

            mExecutor= Executors.newSingleThreadExecutor();
            setContentView(R.layout.activity_camera);
            mCapture= findViewById(R.id.mCapture);
            mCameraView = findViewById(R.id.mCameraView);
            mLift = findViewById(R.id.mLift);

            mCameraView.setCameraController(mController);
            mCapture.setTotal((int)maxTime);

            firstShared = SharedUtils.getFirstShared(CameraActivity.this);

            initData();
            mCameraView.setEffect(null);

            mEffectPopup.attachTo(mCameraView);


            mCapture.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            recordFlag=false;
                            time=System.currentTimeMillis();
                            type=0;
                            mCapture.postDelayed(captureTouchRunnable,500);
                            break;
                        case MotionEvent.ACTION_UP:
                            recordFlag=false;
                            if(System.currentTimeMillis()-time<500){
                                type=1;
                                mCapture.removeCallbacks(captureTouchRunnable);
                                mCameraView.takePhoto();
                            }
                            break;
                    }
                    return false;
                }
            });

            mLift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCameraView.switchCamera();
                }
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,  int[] grantResults) {

        PermissionUtils.requestPermissionsResult(requestCode == 10, grantResults, mRunnable, new Runnable() {
            @Override
            public void run() {
                SharedUtils.setFirstShared(CameraActivity.this,true);
                finish();
                Toast.makeText(CameraActivity.this,"必要的权限未被允许",Toast.LENGTH_SHORT).show();

            }
        });
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(mCameraView!=null){
            mCameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mCameraView!=null){
            mCameraView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraView!=null){
            mCameraView.onDestroy();
        }
    }

    //拍照或者录制的回调
    @Override
    public void onFrame(final byte[] bytes,long time) {
        if(type == 1){
            saveBitmapAsync(bytes,bmpWidth,bmpHeight);
        }else{
            mp4Recorder.feedData(bytes,time);
        }
    }


    //录像的Runnable
    private Runnable captureTouchRunnable=new Runnable() {
        @Override
        public void run() {
            recordFlag=true;
            mExecutor.execute(recordRunnable);
        }
    };


    private Runnable recordRunnable=new Runnable() {
        @Override
        public void run() {
            type=0;
            long timeCount=0;
            if(mp4Recorder==null){
                mp4Recorder=new CameraRecorder();
            }
            long time=System.currentTimeMillis();
            String savePath=getPath("video/",time+".mp4");

            mp4Recorder.setSavePath(getPath("video/",time+""),"mp4");
            try {
                mp4Recorder.prepare(bmpWidth,bmpHeight);
                mp4Recorder.start();

                mCameraView.setFrameCallback(bmpWidth,bmpHeight, CameraActivity.this);
                mCameraView.startRecord();
                while (timeCount<=maxTime&&recordFlag){
                    long start=System.currentTimeMillis();
                    mCapture.setProcess((int)timeCount);
                    long end=System.currentTimeMillis();
                    try {
                        Thread.sleep(timeStep-(end-start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeCount+=timeStep;
                }
                mCameraView.stopRecord();

//                if(timeCount<1000){
//                    mp4Recorder.cancel();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mCapture.setProcess(0);
//                            Toast.makeText(CameraActivity.this,"录像时间太短了",Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                }else{
                mp4Recorder.stop(CameraActivity.this,firstShared);
                recordComplete(type,savePath);
//                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    private String getBaseFolder(){
        String baseFolder= getSD()+"/sunWallpaper/";
        File f=new File(baseFolder);
        if(!f.exists()){
            boolean b=f.mkdirs();
            if(!b){
                baseFolder=getExternalFilesDir(null).getAbsolutePath()+"/";
            }
        }
        return baseFolder;
    }

    //获取VideoPath
    private String getPath(String path,String fileName){
        String p= getBaseFolder()+path;
        File f=new File(p);
        if(!f.exists()&&!f.mkdirs()){
            return getBaseFolder()+fileName;
        }
        return p+fileName;
    }


    private void recordComplete(int type,final String path){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
//                Toast.makeText(CameraActivity.this,"文件保存路径："+path,Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(CameraActivity.this, BrowseVideoActivity.class);
                intent.putExtra("videoPath",path);
                intent.putExtra("typeCode",0);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
