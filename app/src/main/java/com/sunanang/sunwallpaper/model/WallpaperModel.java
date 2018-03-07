package com.sunanang.sunwallpaper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: Coolspan
 * time: 2017/3/13 16:42
 * describe:
 */
public class WallpaperModel implements Parcelable {

    public String wallpaperKey;//壁纸key，唯一值，用于缓存使用
    public int wallpaperRid;//壁纸资源id
    public String path;

    public WallpaperModel(String wallpaperKey, int wallpaperRid, String path) {
        this.wallpaperKey = wallpaperKey;
        this.wallpaperRid = wallpaperRid;
        this.path = path;
    }

    protected WallpaperModel(Parcel in) {
        wallpaperKey = in.readString();
        wallpaperRid = in.readInt();
    }

    public static final Creator<WallpaperModel> CREATOR = new Creator<WallpaperModel>() {
        @Override
        public WallpaperModel createFromParcel(Parcel in) {
            return new WallpaperModel(in);
        }

        @Override
        public WallpaperModel[] newArray(int size) {
            return new WallpaperModel[size];
        }
    };

    public String getWallpaperKey() {
        return wallpaperKey;
    }

    public void setWallpaperKey(String wallpaperKey) {
        this.wallpaperKey = wallpaperKey;
    }

    public int getWallpaperRid() {
        return wallpaperRid;
    }

    public void setWallpaperRid(int wallpaperRid) {
        this.wallpaperRid = wallpaperRid;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wallpaperKey);
        dest.writeInt(wallpaperRid);
        dest.writeString(path);
    }
}
