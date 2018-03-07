package com.sunanang.sunwallpaper.unity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bean.ras_Image;
import com.sunanang.sunwallpaper.bean.ras_video;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ${Sunanang} on 17/12/5.
 */

public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ras_video> datas;//数据
    private ArrayList<ras_Image> picdatas;//数据
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private HashMap<Integer,Integer> imageHeightMap = new HashMap<>();
    private int width,height,type;

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化   type视频1，图片2
    public GridAdapter(Context context, ArrayList<ras_video> datas, ArrayList<ras_Image> picdatas,
                       int width, int height, int type) {
        mContext=context;
        this.datas=datas;
        this.picdatas=picdatas;
        this.width = width;
        this.height = height;
        this.type = type;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_adapter, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (!imageHeightMap.containsKey(position)) {
            View view = ((MyViewHolder) holder).iv;

            ViewGroup.LayoutParams params = view.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = width / 3;
            params.height =(int)(height / 3.5) ;
            view.setLayoutParams(params);
            imageHeightMap.put(position, params.height);
            view.setId(position);

        } else {
            Integer integer = imageHeightMap.get(position);
            View view = ((MyViewHolder) holder).iv;
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (width / 3);
            params.height = integer;
            view.setLayoutParams(params);
        }
        if(type == 1 && datas != null){
            Glide.with(mContext)
                    .load(datas.get(position).getVideoPic())
                    .placeholder(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(((MyViewHolder) holder).iv);
        }else {
            Glide.with(mContext)
                    .load(picdatas.get(position).getThumb())
                    .placeholder(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(((MyViewHolder) holder).iv);
        }

        if (mOnItemClickListener != null && mOnItemClickListener!= null) {
            ((MyViewHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
            ((MyViewHolder) holder).iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onItemLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(type == 1 && datas != null){
            return datas.size();//获取数据的个数
        }else
            return picdatas.size();//获取数据的个数

    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public MyViewHolder(View view) {
            super(view);
            iv =  view.findViewById(R.id.iv);
        }
    }


}


