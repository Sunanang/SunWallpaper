package com.sunanang.sunwallpaper.appSettings;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bamboyToast.CenterDialog;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.warkiz.widget.IndicatorSeekBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AppSettingsActivity extends AppCompatActivity implements CenterDialog.OnCenterItemClickListener {

    private TextView title,quality;
    private ImageView back;
    private RelativeLayout recovery;
    private CenterDialog centerDialog;
    private IndicatorSeekBar continuous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String backgroundShared = SharedUtils.getBackgroundShared(AppSettingsActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(AppSettingsActivity.this,backgroundShared));
        }
        init();
    }

    private void init() {
        back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        title = findViewById(R.id.tv_title);
        title.setText(R.string.settings);
        recovery = findViewById(R.id.latout_recovery);
        continuous = findViewById(R.id.continuous);
        quality = findViewById(R.id.quality);


        centerDialog = new CenterDialog(AppSettingsActivity.this, R.layout.dialog_layout,
                new int[]{R.id.dialog_cancel, R.id.dialog_sure},
                new int[]{R.string.dialog_cancel,R.string.dialog_sure},
                R.id.dialog_text,R.string.dialog_content);
        centerDialog.setOnCenterItemClickListener(AppSettingsActivity.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        int bgQuality = SharedUtils.getBgQuality(AppSettingsActivity.this);
        quality.setText("当前质量：" + bgQuality + "%");
        continuous.setProgress(bgQuality);
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerDialog.show();
            }
        });

        continuous.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                quality.setText("当前质量：" + progress + "%");
                SharedUtils.setBgQuality(AppSettingsActivity.this,progress);
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String tickBelowText, boolean fromUserTouch) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }


    @Override
    public void OnCenterItemClick(CenterDialog dialog, View view) {
        switch (view.getId()) {
            case R.id.dialog_sure:
                SharedUtils.setBackgroundShared(AppSettingsActivity.this, null);
                findViewById(R.id.lin_base).setBackgroundResource(R.drawable.timg);
                SharedUtils.setBgQuality(AppSettingsActivity.this,60);
                quality.setText("当前质量：" + 60 + "%");
                continuous.setProgress(60);
                Toast.makeText(getApplicationContext(),R.string.defaults,Toast.LENGTH_SHORT).show();
                break;
            case R.id.dialog_cancel:
                break;
            default:
                break;
        }
        centerDialog.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
