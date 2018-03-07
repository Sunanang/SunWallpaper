package com.sunanang.sunwallpaper.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ${Sunanang} on 17/12/11.
 */

public class feedback extends BmobObject {
    private String contest; //反馈内容
    private String contact;

    public String getContest() {
        return contest;
    }

    public void setContest(String contest) {
        this.contest = contest;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
