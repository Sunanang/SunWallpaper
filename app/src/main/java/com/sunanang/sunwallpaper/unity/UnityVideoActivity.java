package com.sunanang.sunwallpaper.unity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sunanang.sunwallpaper.R;
import com.sunanang.sunwallpaper.bean.VideoMenuBean;
import com.sunanang.sunwallpaper.bean.menu_table;
import com.sunanang.sunwallpaper.bean.ras_video;
import com.sunanang.sunwallpaper.downLoad.DownLoadVideoActivity;
import com.sunanang.sunwallpaper.popup.TopMiddlePopup;
import com.sunanang.sunwallpaper.util.FileUtils2;
import com.sunanang.sunwallpaper.util.JsonUtils;
import com.sunanang.sunwallpaper.util.SharedUtils;
import com.sunanang.sunwallpaper.util.WallpaperUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UnityVideoActivity extends AppCompatActivity {

    private String menuData;
    private TopMiddlePopup middlePopup;
    public static int screenW, screenH;
    private ArrayList<VideoMenuBean> videoMenuList = new ArrayList<>();  //菜单
    private ArrayList<ras_video> videoBeans = new ArrayList<>();
    private int menuIndex,cateIndex;
    private TextView title;
    private ImageView back;
    private RecyclerView recyclerview;
    private GridAdapter adapter;
    private ArrayList<String> menu;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 21; // 每页的数据是21条
    private String lastTime = null;
    private boolean isRefresh = false,isLoadmore = false;
    private LinearLayout layout;



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String backgroundShared = SharedUtils.getBackgroundShared(UnityVideoActivity.this);
        if(!TextUtils.isEmpty(backgroundShared)){
            findViewById(R.id.lin_base).setBackground(FileUtils2.compressBmp(this,backgroundShared));
        }
        int[] screenParams = WallpaperUtils.getScreenParams(this);
        screenW = screenParams[0];
        screenH = screenParams[1];
        initView();
        init();
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableLoadmoreWhenContentNotFull(true);//内容不满屏幕的时候也开启加载更多

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                setRefresh(refreshlayout);
            }

        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                setLoadmore(refreshlayout);

            }
        });
    }

    private void init() {
        BmobQuery<menu_table> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject("LswY999C", new QueryListener<menu_table>() {
            @Override
            public void done(menu_table res_address, BmobException e) {
                if(e==null){
                    menuData = res_address.getMenu();
                    videoMenuList = JsonUtils.getVideoMenuList(menuData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menuIndex = 0;
                            title.setText(videoMenuList.get(menuIndex).getName());
                            title.setOnClickListener(listener);
                            queryData(0, STATE_REFRESH,videoMenuList.get(menuIndex).getCode(),0,null);
                        }
                    });

                }else{
                    Log.i("Sunanang","查询失败：" + e.getMessage());
                }
            }
        });
    }


    /**
     * 初始化控件
     */
    private void initView() {
        title = findViewById(R.id.tv_menu_title);
        TextView title = findViewById(R.id.tv_title);
        recyclerview = findViewById(R.id.recyclerview);
        back = findViewById(R.id.img_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(listener);
        title.setText(R.string.video_community);
        layout = findViewById(R.id.Video_ad_fullscreen);
    }

    /**
     * 初始化recyclerview
     */
    private void initRecyclerView(ArrayList<ras_video> beans){
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        adapter = new GridAdapter(UnityVideoActivity.this,beans,null,screenW, screenH,1);
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new GridSpacingItemDecoration(3,10,true));
        adapter.setOnItemClickListener(recyclerItemListener);
        recyclerview.setAdapter(adapter);
    }

    /**
     * 设置弹窗
     *
     * @param type
     */
    private void setPopup(int type) {
        if(menu == null){
            menu = new ArrayList<>();
            for (VideoMenuBean b : videoMenuList) {
                menu.add(b.getName());

            }
        }
        middlePopup = new TopMiddlePopup(UnityVideoActivity.this, screenW, screenH,
                onItemClickListener, menu, type);

    }


    /**
     * 弹窗点击事件
     */
    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    menuIndex = position;
                    title.setText(videoMenuList.get(menuIndex).getName());
                    cateIndex = 0;
                    findViewById(R.id.layout_load).setVisibility(View.VISIBLE);
                    findViewById(R.id.comm_content).setVisibility(View.GONE);
                    isLoadmore = false;
                    isRefresh = false;
                    setOtherMenuData();
                    middlePopup.dismiss();
                }
            };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.tv_menu_title:
                    setPopup(0);
                    middlePopup.show(title);
                    break;

            }
        }
    };

    /**
     * item点击事件
     */
    private GridAdapter.OnRecyclerViewItemClickListener recyclerItemListener = new
            GridAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(UnityVideoActivity.this, DownLoadVideoActivity.class);
                    intent.putExtra("videoUrl",videoBeans.get(position).getVideoUrl());
                    intent.putExtra("videoName",videoBeans.get(position).getName());
                    intent.putExtra("videoSize",videoBeans.get(position).getSize());
                    startActivity(intent);

                }

                @Override
                public void onItemLongClick(int position) {
//                    Toast.makeText(getApplicationContext(),"长按了  " + position,Toast.LENGTH_SHORT).show();
                }
            };

    /**
     * 刷新
     * @param refreshlayout
     */
    private void setRefresh(final RefreshLayout refreshlayout){
        cateIndex  = 0;
        queryData(cateIndex, STATE_REFRESH,videoMenuList.get(menuIndex).getCode(),2,refreshlayout);
    }

    /**
     * 加载
     */
    private void setLoadmore(final RefreshLayout refreshlayout){
        queryData(cateIndex, STATE_MORE,videoMenuList.get(menuIndex).getCode(),3,refreshlayout);
    }

    /**
     * 获取其他的菜单信息
     */
    private void setOtherMenuData(){
        queryData(cateIndex, STATE_REFRESH,videoMenuList.get(menuIndex).getCode(),1,null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 分页获取数据
     *
     * @param page
     *            页码
     * @param actionType
     *            ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData(int page, final int actionType, String category, final int type, final RefreshLayout refreshlayout) {

        if (actionType == STATE_MORE && type == 3 && isLoadmore) { //加载更多
            Message msg = new Message();
            msg.what =type;
            msg.obj = refreshlayout;
            mHandler.sendMessage(msg);
            return;

        } else if (actionType == STATE_REFRESH&& type == 2 && isRefresh) { // 刷新
            Message msg = new Message();
            msg.what =type;
            msg.obj = refreshlayout;
            mHandler.sendMessage(msg);
            return;
        }


        BmobQuery<ras_video> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        query.addWhereEqualTo("category", category);

        int count = 0;
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * count + 1);
        } else {
            // 下拉刷新
            page = 0;
            query.setSkip(page);
        }
        // 设置每页数据个数
        query.setLimit(limit);


        query.findObjects(new FindListener<ras_video>() {

            @Override
            public void done(List<ras_video> list, BmobException e) {
                if(e==null){
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            cateIndex = 0;
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }
                        if(type == 0 || type == 1){
                            videoBeans.clear();
                            // 将本次查询的数据添加到bankCards中
                            for (ras_video td : list) {
                                videoBeans.add(td);
                            }
                        }else if(type == 2){
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 0; j < videoBeans.size(); j++) {
                                    if(videoBeans.get(j).getVideoPic().equals(list.get(i).getVideoPic())){
                                        break;
                                    }else {
                                        if(!videoBeans.get(j).getVideoPic().equals(list.get(i).getVideoPic())){
                                            if(j == list.size()-1){
                                                videoBeans.add(0,list.get(i));
                                            }
                                        }
                                    }
                                }
                            }

                        }else if(type == 3){
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 0; j < videoBeans.size(); j++) {
                                    if(videoBeans.get(j).getVideoPic().equals(list.get(i).getVideoPic())){
                                        break;
                                    }else {
                                        if(!videoBeans.get(j).getVideoPic().equals(list.get(i).getVideoPic())){
                                            if(j == list.size()-1){
                                                videoBeans.add(list.get(i));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        cateIndex++;
                    } else if (actionType == STATE_MORE) {
                        isLoadmore = true;

                    } else if (actionType == STATE_REFRESH) {
                        isRefresh = true;
                    }

                    Message msg = new Message();
                    msg.what =type;
                    msg.obj = refreshlayout;
                    mHandler.sendMessage(msg);
                }else {
                    Log.i("Sunanang","查询失败：" + e.getMessage());
                    refreshlayout.finishLoadmore(200);
                }
            }
        });

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:  //第一次创建
                    findViewById(R.id.layout_load).setVisibility(View.GONE);
                    findViewById(R.id.comm_content).setVisibility(View.VISIBLE);
                    initRecyclerView(videoBeans);
                    break;
                case 1: //切换菜单
                    adapter.notifyDataSetChanged();
                    findViewById(R.id.layout_load).setVisibility(View.GONE);
                    findViewById(R.id.comm_content).setVisibility(View.VISIBLE);
                    break;
                case 2: //刷新
                    RefreshLayout layouthead = (RefreshLayout) msg.obj;
                    layouthead.finishRefresh(100);
                    adapter.notifyDataSetChanged();
                    break;
                case 3:  //加载
                    RefreshLayout layout = (RefreshLayout) msg.obj;
                    layout.finishLoadmore(200);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
