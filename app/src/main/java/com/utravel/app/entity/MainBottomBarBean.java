package com.utravel.app.entity;

public class MainBottomBarBean {
    public int id;
    public int image_non;
    public int image_on;
    public String name;

    public MainBottomBarBean(int image_non, int image_on, String name, int id) {
        this.image_non = image_non;
        this.image_on = image_on;
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage_non() {
        return image_non;
    }

    public void setImage_non(int image_non) {
        this.image_non = image_non;
    }

    public int getImage_on() {
        return image_on;
    }

    public void setImage_on(int image_on) {
        this.image_on = image_on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
