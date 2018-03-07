package com.sunanang.sunwallpaper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.igexin.sdk.PushManager;
import com.sunanang.sunwallpaper.activity.SplashActivity;
import com.sunanang.sunwallpaper.service.DemoIntentService;
import com.sunanang.sunwallpaper.service.DemoPushService;
import com.sunanang.sunwallpaper.util.SharedUtils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Sunanang on 19/1/18.
 */
public class MyApplication extends Application{
    public int count = 0;
    private Intent intent;
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/DINCondensedBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //第一：默认初始化
        Bmob.initialize(this, "d917b88c833ccaef595e75f60bc42238");
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        Bmob.initialize(this, "d917b88c833ccaef595e75f60bc42238","bmob");

        if(this.getApplicationContext() != null){
            BmobUpdateAgent.update(this);
            BmobUpdateAgent.setUpdateOnlyWifi(false);
        }

        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this, DemoPushService.class);

        // DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this, DemoIntentService.class);


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
//                    Log.v("Sunanang", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");

                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
//                    Log.v("Sunanang", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    boolean isTask = isActivityTop(SplashActivity.class, getApplicationContext());
                    boolean startState = SharedUtils.getStartStateShared(getApplicationContext());
                    if(startState){
                        SharedUtils.setStartStateShared(getApplicationContext(),false);
                    }else {
                        if (!isTask){
                            intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                    }

                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });
    }

    /**
    *
    * 判断某activity是否处于栈顶
    * @return  true在栈顶 false不在栈顶
    */
    private boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }
}
