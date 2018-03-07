package com.sunanang.sunwallpaper.opinion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bean.feedback;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.ShareToTencentUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpinionActivity extends AppCompatActivity {
    private String backgroundShared;
    private TextView submit;
    private RelativeLayout join_submit;
    private ClipboardManager myClipboard;  //复制数据
    private ClipData myClip;
    private EditText tickling,contact;//意见,联系
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_opinion);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        backgroundShared = SharedUtils.getBackgroundShared(OpinionActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        init();
    }

    private void init() {
        ImageView back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(listener);
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.pic_opinion);
        submit = findViewById(R.id.tickling_submit);
        submit.setOnClickListener(listener);
        join_submit = findViewById(R.id.join_submit);
        join_submit.setOnClickListener(listener);
        tickling = findViewById(R.id.tickling_information);  //意见
        contact = findViewById(R.id.contact_information);    //联系方式
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        layout = findViewById(R.id.ad_full);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.tickling_submit:
                    ReportInfo();
                    break;
                case R.id.join_submit:
                    boolean b = ShareToTencentUtils.joinQQGroup(OpinionActivity.this);
                    if(!b){
                        myClip = ClipData.newPlainText("text", "欢迎加入拍拍壁纸吐槽群，群号码：244587320");//text是内容
                        myClipboard.setPrimaryClip(myClip);
                        Toast.makeText(OpinionActivity.this,"qq群跳转失败，已为您复制到粘贴板",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void ReportInfo(){
        String tick = tickling.getText().toString();
        String contacts = contact.getText().toString();
        if(!TextUtils.isEmpty(tick)){
            if(tick.length() > 5){
                bmobData(tick,contacts);
            }else
                Toast.makeText(OpinionActivity.this,"反馈内容必须多于5个字",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(OpinionActivity.this,"反馈内容不可为空",Toast.LENGTH_SHORT).show();
        }
    }

    private void bmobData(String tick,String contacts){
        feedback back = new feedback();
        back.setContest(tick);
        back.setContact(contacts);
        back.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Toast.makeText(OpinionActivity.this,"您的反馈已提交，我们会尽快处理并联系您，请注意查收。",Toast.LENGTH_SHORT).show();
                    tickling.setText("");
                    contact.setText("");
                }else{
                    Toast.makeText(OpinionActivity.this,"对不起，服务错误，请重新提交",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
