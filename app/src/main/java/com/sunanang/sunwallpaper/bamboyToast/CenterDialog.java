package com.sunanang.sunwallpaper.bamboyToast;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sunanang.sunwallpaper.R;


/**
 * Created by ${Sunanang} on 17/11/30.
 */

public class CenterDialog extends Dialog implements View.OnClickListener{

    private Context context;      // 上下文
    private int layoutResID;      // 布局文件id
    private int[] listenedItems;  // 要监听的控件id
    private int contextId;        //显示内容id
    private int content;          //显示的内容
    private int[] bt_content;        //所有文字

    public CenterDialog(Context context, int layoutResID, int[] listenedItems,int[] bt_content,int contextId,int content) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
        this.bt_content = bt_content;
        this.contextId = contextId;
        this.content = content;
    }

    public CenterDialog( Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(layoutResID);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失

        TextView tv_show_text = findViewById(contextId);
        tv_show_text.setText(content);
        //遍历控件id,添加点击事件
        for (int i = 0; i < listenedItems.length; i++) {
            TextView bt = findViewById(listenedItems[i]);
            bt.setText(bt_content[i]);
            bt.setOnClickListener(this);
        }

    }

    private OnCenterItemClickListener listener;
    public interface OnCenterItemClickListener {
        void OnCenterItemClick(CenterDialog dialog, View view);
    }
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();//注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        listener.OnCenterItemClick(this, view);
    }

}
