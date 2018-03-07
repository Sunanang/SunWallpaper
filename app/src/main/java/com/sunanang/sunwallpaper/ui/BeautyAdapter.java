package com.sunanang.sunwallpaper.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aiyaapp.camera.sdk.util.ClickUtils;
import com.aiyaapp.camera.sdk.AiyaEffects;
import com.aiyaapp.camera.sdk.base.ISdkManager;
import com.sunanang.sunwallpaper.R;

/**
 * Description:
 */
public class BeautyAdapter extends RecyclerView.Adapter<BeautyAdapter.MenuHolder> implements View.OnClickListener {

    private Context mContext;
    public String[] beautys=new String[]{
            "无美颜","美颜一","美颜二","美颜三","美颜四","美颜五","美颜六"
    };
    public int checkPos=0;

    public BeautyAdapter(Context context){
        this.mContext=context;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuHolder(LayoutInflater.from(mContext).inflate(R.layout.item_menu,parent,false));
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        holder.setData(beautys[position],position);
    }

    @Override
    public int getItemCount() {
        return beautys.length;
    }

    @Override
    public void onClick(View v) {
        checkPos=ClickUtils.getPos(v);
        notifyDataSetChanged();
        AiyaEffects.getInstance().set(ISdkManager.SET_BEAUTY_TYPE,checkPos);
    }

    public class MenuHolder extends RecyclerView.ViewHolder{

        private TextView tv;

        public MenuHolder(View itemView) {
            super(itemView);
            tv= (TextView)itemView.findViewById(R.id.mMenu);
            ClickUtils.addClickTo(tv, BeautyAdapter.this,R.id.mMenu);
        }

        public void setData(String name,int pos){
            tv.setText(name);
            tv.setSelected(pos==checkPos);
            ClickUtils.setPos(tv,pos);
        }

        public void select(boolean isSelect){
            tv.setSelected(isSelect);
        }
    }

}
