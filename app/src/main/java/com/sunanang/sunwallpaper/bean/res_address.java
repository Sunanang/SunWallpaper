package com.sunanang.sunwallpaper.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ${Sunanang} on 17/12/2.
 */

public class res_address extends BmobObject {
    private String video_address;
    private String pic_address;

    public String getVideo_address() {
        return video_address;
    }

    public void setVideo_address(String video_address) {
        this.video_address = video_address;
    }

    public String getPic_address() {
        return pic_address;
    }

    public void setPic_address(String pic_address) {
        this.pic_address = pic_address;
    }
}
