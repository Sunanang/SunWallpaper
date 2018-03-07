package com.sunanang.sunwallpaper.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ${Sunanang} on 17/12/7.
 */

public class ras_Image extends BmobObject {
    private String thumb;
    private String imgUrl;
    private String category;
    private String basePicUrl;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBasePicUrl() {
        return basePicUrl;
    }

    public void setBasePicUrl(String basePicUrl) {
        this.basePicUrl = basePicUrl;
    }
}