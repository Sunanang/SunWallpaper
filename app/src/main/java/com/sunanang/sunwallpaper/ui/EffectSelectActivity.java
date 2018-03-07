package com.sunanang.sunwallpaper.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.aiyaapp.camera.sdk.AiyaEffects;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.browse.BrowsePhoneActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Description:
 */
public class EffectSelectActivity extends AppCompatActivity implements EffectAdapter.OnEffectCheckListener {

    private ImageView mBtnRight;
    private int mBeautyFlag=0;

    protected EffectController mEffectPopup;
    private View mBtnContainer;
    private View mEffectContainer;
    public static Bitmap bitmap;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void initData(){
        mBtnRight= findViewById(R.id.mRight);
        mBtnContainer=findViewById(R.id.mOtherMenu);
        mEffectContainer=findViewById(R.id.mEffectContainer);
        mEffectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEffectContainer.setVisibility(View.GONE);
                mBtnContainer.setVisibility(View.VISIBLE);
            }
        });

        mEffectPopup=new EffectController(this,mEffectContainer,this);

    }

    public View getContentView(){
        return findViewById(android.R.id.content);
    }

    //View的点击事件处理
    public void onClick(View view){
        switch (view.getId()){
            case R.id.mRight:
                mEffectContainer.setVisibility(View.VISIBLE);
                mBtnContainer.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mEffectContainer.getVisibility()==View.VISIBLE){
            mEffectContainer.setVisibility(View.GONE);
            mBtnContainer.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEffectPopup!=null){
            mEffectPopup.release();
        }
    }

    protected String getSD(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public void saveBitmapAsync(final byte[] bytes,final int width,final int height){
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                ByteBuffer b=ByteBuffer.wrap(bytes);
                bitmap.copyPixelsFromBuffer(b);
                saveBitmap(bitmap);

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                String dataPath=getRealFilePath(data.getData());
                if(dataPath!=null&&dataPath.endsWith(".json")){
                    AiyaEffects.getInstance().setEffect(dataPath);
                }
            }
        }
    }

    public String getRealFilePath(final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //图片保存
    public void saveBitmap(final Bitmap b){
        String path =  getSD()+ "/sunWallpaper/photo/";
        File folder=new File(path);
        if(!folder.exists()&&!folder.mkdirs()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EffectSelectActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName=path+ dataTake +".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(EffectSelectActivity.this, "保存成功->"+jpegName, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(EffectSelectActivity.this,BrowsePhoneActivity.class);
                intent.putExtra("ImagePath",jpegName);
                intent.putExtra("typeCode",0);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onEffectChecked(int pos, String path) {
        if(pos==-1){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "请选择一个json文件"),101);
            return true;
        }
        return false;
    }
}
