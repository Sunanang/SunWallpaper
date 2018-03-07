package com.sunanang.sunwallpaper.util;

import android.text.TextUtils;

import com.sunanang.sunwallpaper.bean.ADBean;
import com.sunanang.sunwallpaper.bean.ras_Image;
import com.sunanang.sunwallpaper.bean.PicMenuBean;
import com.sunanang.sunwallpaper.bean.VideoBean;
import com.sunanang.sunwallpaper.bean.VideoMenuBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ${Sunanang} on 17/12/2.
 */

public class JsonUtils {

    /**
     * 视频menu菜单json数据
     * @param data
     * @return
     */
    public static ArrayList<VideoMenuBean> getVideoMenuList(String data){
        ArrayList<VideoMenuBean> arrayList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                VideoMenuBean bean = new VideoMenuBean();
                JSONObject object1 = array.optJSONObject(i);
                bean.setCode(object1.optString("Code"));
                bean.setName(object1.optString("Name"));
                arrayList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

//    /**
//     * 视频menu菜单json数据
//     * @param data
//     * @return
//     */
//    public static ArrayList<VideoMenuBean> getVideoMenuList(String data){
//        ArrayList<VideoMenuBean> arrayList = new ArrayList<>();
//        try {
//            JSONObject object = new JSONObject(data);
//            JSONArray array = object.optJSONArray("data");
//            for (int i = 0; i < array.length(); i++) {
//                VideoMenuBean bean = new VideoMenuBean();
//                JSONObject object1 = array.optJSONObject(i);
//                bean.setCode(object1.optString("Code"));
//                bean.setName(object1.optString("Name"));
//                arrayList.add(bean);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }

    /**
     * 解析视频video的数据
     * @param data
     * @return
     */
    public static ArrayList<VideoBean> getVideoData(String data){
        ArrayList<VideoBean> arrayList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                VideoBean bean = new VideoBean();
                JSONObject object1 = array.optJSONObject(i);
                bean.setName(object1.optString("Name"));
                bean.setVideoUrl(object1.optString("Video"));
                bean.setVideoPic(object1.optString("Pic"));
                bean.setSize(object1.optInt("Size"));
                if(!TextUtils.isEmpty(bean.getVideoUrl())){
                    arrayList.add(bean);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }



    /**
     * 图片menu菜单json数据
     * @param data
     * @return
     */
    public static ArrayList<PicMenuBean> getPicMenuList(String data){
        ArrayList<PicMenuBean> arrayList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.optJSONArray("category");
            for (int i = 0; i < array.length(); i++) {
                PicMenuBean bean = new PicMenuBean();
                JSONObject object1 = array.optJSONObject(i);
                bean.setNameEnglin(object1.optString("ename"));
                bean.setName(object1.optString("name"));
                bean.setId(object1.optString("id"));
                arrayList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

//    /**
//     * 解析图片data的数据
//     * @param data
//     * @return
//     */
//    public static ArrayList<ras_Image> getPicData(String data){
//        ArrayList<ras_Image> arrayList = new ArrayList<>();
//        try {
//            JSONObject object = new JSONObject(data);
//            JSONArray array = object.optJSONArray("data");
//            for (int i = 0; i < array.length(); i++) {
//                ras_Image bean = new ras_Image();
//                JSONObject object1 = array.optJSONObject(i);
//                bean.setId(object1.optString("id"));
//                bean.setItile(object1.optString("title"));
//                bean.setThumb(object1.optString("thumb"));
//                bean.setCount(object1.optInt("thumb"));
//                arrayList.add(bean);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }


    public static ADBean getAdData(String data){
        ADBean adBean = new ADBean();
        try {
            JSONObject object = new JSONObject(data);
            adBean.setImgUrl(object.optString("imgurl"));
            adBean.setGotourl(object.optString("gotourl"));
            adBean.setCountUrl(object.optString("count_url"));
            adBean.setSdownUrl(object.optString("sdown_url"));
            adBean.setEdownUrl(object.optString("edown_url"));
            adBean.setFinishUrl(object.optString("finish_url"));
            adBean.setIsLink(object.optInt("is_link"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  adBean;
    }
}
