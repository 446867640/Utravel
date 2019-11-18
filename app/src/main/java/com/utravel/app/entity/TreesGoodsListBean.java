package com.utravel.app.entity;

import java.util.List;
public class TreesGoodsListBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : 红酒
         * price : 1000
         * image_url : http://hx-staging-images.igolife.net/development/images/product_image/content/1/11%E5%8F%B70%E7%82%B9__%E6%A0%BC%E7%81%B5%E9%9B%85_%E7%A7%8B%E5%86%AC%E7%AB%96%E6%9D%A1%E7%BA%B9%E6%89%93%E5%BA%95%E8%A3%A4%E5%A5%B3%E5%8A%A0%E7%BB%92%E5%8A%A0%E5%8E%9A%E8%B8%A9%E8%84%9A%E8%BF%9E%E8%A2%9C%E8%A3%A4_%E9%BB%91%E8%89%B2_.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim
         */

        private int id;
        private String name;
        private String price;
        private String image_url;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}
