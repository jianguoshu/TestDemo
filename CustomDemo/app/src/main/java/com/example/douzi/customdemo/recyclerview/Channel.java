package com.example.douzi.customdemo.recyclerview;

/**
 * Created by douzi on 2017/7/14.
 */

public class Channel {

    int type;
    String name;
    boolean isSelected;
    int sortId;

    public Channel(String name, boolean isSelected, int sortId, int type) {
        this.name = name;
        this.isSelected = isSelected;
        this.sortId = sortId;
        this.type = type;
    }
}
