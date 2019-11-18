package com.utravel.app.entity;

import java.io.Serializable;

public class JDBankuanBean implements Serializable {

    private String brand_id;
    private String commisionRatioWl;
    private String estimated_commission;
    private String imageUrl;
    private String op_time;
    private String price;
    private String qtty_30;
    private String skuId;
    private String wareName;

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCommisionRatioWl() {
        return commisionRatioWl;
    }

    public void setCommisionRatioWl(String commisionRatioWl) {
        this.commisionRatioWl = commisionRatioWl;
    }

    public String getEstimated_commission() {
        return estimated_commission;
    }

    public void setEstimated_commission(String estimated_commission) {
        this.estimated_commission = estimated_commission;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOp_time() {
        return op_time;
    }

    public void setOp_time(String op_time) {
        this.op_time = op_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQtty_30() {
        return qtty_30;
    }

    public void setQtty_30(String qtty_30) {
        this.qtty_30 = qtty_30;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }
}
