package com.sunanang.sunwallpaper.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ${Sunanang} on 17/12/8.
 */

public class ras_video extends BmobObject {
    private String videoUrl;
    private String videoPic;
    private int size;
    private String name;
    private String category;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
