package com.utravel.app.entity;

public class IntegerIconBean {
    public int iconImage;
    public int id;

    public IntegerIconBean(int iconImage, int id) {
        this.iconImage = iconImage;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIconImage() {
        return iconImage;
    }

    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }
}
