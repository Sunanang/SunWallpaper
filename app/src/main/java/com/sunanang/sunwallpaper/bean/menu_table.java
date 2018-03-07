package com.sunanang.sunwallpaper.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ${Sunanang} on 17/12/8.
 */

public class menu_table extends BmobObject {
    private String menu;
    private String picMenu;

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getPicMenu() {
        return picMenu;
    }

    public void setPicMenu(String picMenu) {
        this.picMenu = picMenu;
    }
}
