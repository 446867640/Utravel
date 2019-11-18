package com.utravel.app.entity;

public class ShouCangEntity {
    int id;
    String imageUrl;
    String goodsName;
    String goodsPrice;
    String goodsOldPrice;
    String goodsBackPrice;

    public ShouCangEntity(
            String imageUrl,
            String goodsName,
            String goodsPrice,
            String goodsOldPrice,
            String goodsBackPrice){
        this.imageUrl = imageUrl;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsOldPrice = goodsOldPrice;
        this.goodsBackPrice = goodsBackPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsOldPrice() {
        return goodsOldPrice;
    }

    public void setGoodsOldPrice(String goodsOldPrice) {
        this.goodsOldPrice = goodsOldPrice;
    }

    public String getGoodsBackPrice() {
        return goodsBackPrice;
    }

    public void setGoodsBackPrice(String goodsBackPrice) {
        this.goodsBackPrice = goodsBackPrice;
    }
}
