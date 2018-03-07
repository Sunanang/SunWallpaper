package com.sunanang.sunwallpaper.instructions;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InstructionsActivity extends AppCompatActivity {

    private String backgroundShared;
    private TextView describe;
    private LinearLayout layout,hLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        backgroundShared = SharedUtils.getBackgroundShared(this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        SharedUtils.setInstFirstShared(this,true);
        init();
    }

    private void init() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.pic_Instructions);
        ImageView back = findViewById(R.id.img_back);
        TextView personal = findViewById(R.id.inst_personal);
        describe = findViewById(R.id.inst_cate);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(listener);
        describe.setText(R.string.instruction);
        personal.setText(R.string.declare);
        layout = findViewById(R.id.ad_full);


        hLayout = findViewById(R.id.ll_ad_fullscreen);

    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}

