package com.utravel.app.entity;

public class ImgTvBean {
    public int id;
    public int imageResource;
    public String name;
    public String imageUrl;

    public ImgTvBean(String imageUrl, String name, int id) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.id = id;
    }

    public ImgTvBean(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public ImgTvBean(int imageResource, String name, int id) {
        this.imageResource = imageResource;
        this.name = name;
        this.id = id;
    }

    public ImgTvBean(int imageResource, String name) {
        this.imageResource = imageResource;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
