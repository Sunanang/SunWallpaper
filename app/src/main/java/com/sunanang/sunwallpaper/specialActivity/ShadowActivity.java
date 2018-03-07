package com.sunanang.sunwallpaper.specialActivity;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.opengles.GLESPlaneAnimatedRenderer;
import com.sunanang.sunwallpaper.opengles.GLESPlaneAnimatedSurfaceView;
import com.sunanang.sunwallpaper.service.ColloredWallpaperService;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.view.CustomRadioGroup;
import com.warkiz.widget.IndicatorSeekBar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ShadowActivity extends AppCompatActivity {

    private ImageView back;
    private TextView title;
    private GLESPlaneAnimatedSurfaceView _mGLSurfaceView;
    private GLESPlaneAnimatedRenderer _mRenderer;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    private boolean changedColor;
    private boolean changedMotion;
    private boolean changedSpeed;
    private boolean changedParallax;
    private boolean wasParallax;
    private String movement;
    private String color;
    private String[] colors_intern;
    private String[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colors = getResources().getStringArray(R.array.colors);
        colors_intern = getResources().getStringArray(R.array.colors_intern);
        setContentView(R.layout.activity_shadow);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String backgroundShared = SharedUtils.getBackgroundShared(ShadowActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        init();
    }

    private void init() {
        back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        title = findViewById(R.id.tv_title);
        title.setText(R.string.magic);
        changedColor = false;
        changedMotion = false;
        changedSpeed = false;
        changedParallax = false;
        wasParallax = false;
        movement = "";
        color = "";

        prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor = prefs.edit();


        _mGLSurfaceView = findViewById(R.id.surfaceView);
        _mRenderer = new GLESPlaneAnimatedRenderer(this);

        if(isValidGLES()) {
            _mGLSurfaceView.setEGLContextClientVersion(2);
            _mGLSurfaceView.setRenderer(_mRenderer);
        }
        else
        {
            throw new RuntimeException("Error OpenGL ES 2.0 not found");
        }

        //SAVED STATE
        editor.putString("color", prefs.getString("color", "COLORFUL"));
        _mRenderer.switchColors(prefs.getString("color", "COLORFUL"));
        color =  prefs.getString("color", "");
        editor.putFloat("animSpeed", prefs.getFloat("animSpeed", 0.2f));
        _mRenderer.changeAnimationSpeed(prefs.getFloat("animSpeed", 0.2f));
        editor.putString("motion", prefs.getString("motion", "straight"));
        _mRenderer.changeMotion(prefs.getString("motion", "straight"));
        movement = prefs.getString("motion", "");
        editor.putBoolean("sensors", prefs.getBoolean("sensors", false));
        _mGLSurfaceView.activateSensors(prefs.getBoolean("sensors", false));
        wasParallax = prefs.getBoolean("sensors", false);


        //COLOR DROPDOWN
        Spinner colorDropDown = findViewById(R.id.colorDropdown);
        ArrayAdapter adapter= new ArrayAdapter<>(this, R.layout.spinner_item, colors);
        final ImageView dropDownImage = findViewById(R.id.dropdownimage);

        colorDropDown.setAdapter(adapter);
        String currentColor = prefs.getString("color", "COLORFUL");
        switch (currentColor) {
            case "RED":
                colorDropDown.setSelection(0);
                dropDownImage.setBackgroundColor(Color.rgb(255, 0, 0));
                break;
            case "BLUE":
                colorDropDown.setSelection(1);
                dropDownImage.setBackgroundColor(Color.rgb(0, 0, 255));
                break;
            case "GREEN":
                colorDropDown.setSelection(2);
                dropDownImage.setBackgroundColor(Color.rgb(0, 255, 0));
                break;
            case "COLORFUL":
                colorDropDown.setSelection(3);
                dropDownImage.setBackgroundColor(Color.rgb(255, 150, 0));
                break;
            case "PINK":
                colorDropDown.setSelection(4);
                dropDownImage.setBackgroundColor(Color.rgb(225, 0, 135));
                break;
            case "AUTUMN":
                colorDropDown.setSelection(5);
                dropDownImage.setBackgroundColor(Color.rgb(175, 125, 0));
                break;
            case "WINTER WONDERLAND":
                colorDropDown.setSelection(6);
                dropDownImage.setBackgroundColor(Color.rgb(200, 200, 255));
                break;
        }

        colorDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!color.equals(colors_intern[(int) id])) changedColor = true;
                else changedColor = false;
                switch ((int)id) {
                    case 0:
                        _mRenderer.switchColors("RED");
                        editor.putString("color", "RED");
                        dropDownImage.setBackgroundColor(Color.rgb(255, 0, 0));
                        break;
                    case 1:
                        _mRenderer.switchColors("BLUE");
                        editor.putString("color", "BLUE");
                        dropDownImage.setBackgroundColor(Color.rgb(0, 0, 255));
                        break;
                    case 2:
                        _mRenderer.switchColors("GREEN");
                        editor.putString("color", "GREEN");
                        dropDownImage.setBackgroundColor(Color.rgb(0, 255, 0));
                        break;
                    case 3:
                        _mRenderer.switchColors("COLORFUL");
                        editor.putString("color", "COLORFUL");
                        dropDownImage.setBackgroundColor(Color.rgb(255, 150, 0));
                        break;
                    case 4:
                        _mRenderer.switchColors("PINK");
                        editor.putString("color", "PINK");
                        dropDownImage.setBackgroundColor(Color.rgb(225, 0, 135));
                        break;
                    case 5:
                        _mRenderer.switchColors("AUTUMN");
                        editor.putString("color", "AUTUMN");
                        dropDownImage.setBackgroundColor(Color.rgb(175, 125, 0));
                        break;
                    case 6:
                        _mRenderer.switchColors("WINTER WONDERLAND");
                        editor.putString("color", "WINTER WONDERLAND");
                        dropDownImage.setBackgroundColor(Color.rgb(200, 200, 255));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //SET WALLPAPER
        TextView setButton = findViewById(R.id.buttonSetWallpaper);
        setButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
                changedColor = false;
                changedMotion = false;
                changedSpeed = false;
                changedParallax = false;
                SharedUtils.setStartStateShared(ShadowActivity.this,true);
                try {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new
                            ComponentName(ShadowActivity.this, ColloredWallpaperService.class));
                    startActivity(intent);

                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent);
                }
            }
        });

        //PARALLAX TOGGLE
        SwitchCompat parallaxToggle = findViewById(R.id.parallaxToggle) ;
        boolean pT = prefs.getBoolean("sensors", false);
        if(pT)
            parallaxToggle.toggle();
        parallaxToggle.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    if(!wasParallax) changedParallax = true;
                    else changedParallax = false;
                    _mGLSurfaceView.activateSensors(true);
                    editor.putBoolean("sensors", true);
                }
                else
                {
                    if(wasParallax) changedParallax = true;
                    else changedParallax = false;
                    _mGLSurfaceView.activateSensors(false);
                    editor.putBoolean("sensors", false);
                }
            }
        });



        RadioButton radioButton;

        // MOTION RADIO GROUP
        final CustomRadioGroup motion = findViewById(R.id.motionGroup);
        String currentMotion = prefs.getString("motion", "straight");
        switch (currentMotion) {
            case "straight":
                radioButton = findViewById(R.id.straight);
                radioButton.toggle();
                break;
            case "8":
                radioButton = findViewById(R.id.eight);
                radioButton.toggle();
                break;
            case "random":
                radioButton = findViewById(R.id.random);
                radioButton.toggle();
                break;
        }
        motion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        if(!movement.equals("straight")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("straight");
                        editor.putString("motion", "straight");
                        break;
                    case 1:
                        if(!movement.equals("8")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("8");
                        editor.putString("motion", "8");
                        break;
                    case 2:
                        if(!movement.equals("random")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("random");
                        editor.putString("motion", "random");
                        break;
                }
            }
        });

        //ANIMATION SPEED SLIDER
        IndicatorSeekBar animSpeed = findViewById(R.id.animationSpeedSlider);
        Float currentSpeed = prefs.getFloat("animSpeed", 0.5f);
        animSpeed.setProgress((int)(currentSpeed * 100));

        animSpeed.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                changedSpeed = true;
                float animSpeed = progress/100.0f;
                _mRenderer.changeAnimationSpeed(animSpeed);
                editor.putFloat("animSpeed", animSpeed);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    private boolean isValidGLES() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x2000;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
