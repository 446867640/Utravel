package com.utravel.app.entity;

public class ZhuanShuEntity {
    public String imageUrl;
    public String name;
    public String phone;
    public String time;

    public ZhuanShuEntity(String imageUrl, String name, String phone, String time){
        this.imageUrl = imageUrl;
        this.name = name;
        this.phone = phone;
        this.time = time;
    }

    public String getImageUrl() { return imageUrl; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getTime() { return time; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setTime(String time) { this.time = time; }
}
