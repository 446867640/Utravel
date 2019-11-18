package com.utravel.app.entity;

import java.util.List;

public class PinduoduoOrderBean {

    /**
     * current_page : 1
     * data : [{"id":1,"items":[{"id":1,"image_url":"http://img14.360buyimg.com/n1/jfs/t1/5519/17/9009/504769/5bab4e86E7b094be7/a13f1268f159ddcf.jpg","points":10,"product_name":"迪茵（DIYIN）自粘防水厨房防油贴纸瓷砖灶台用铝箔锡纸加厚橱柜油烟防潮墙贴纸 银灰橘纹10米","quantity":1,"rewarded_balance":9.8,"unit_price":1.2}],"number":"81519966666","ordered_at":"2018-11-11T00:01:31.000+08:00","rewarded_balance":"9.8","state":"finished","state_zh_cn":"已完成","total_paid":12.34},{"id":2,"items":[{"id":3,"image_url":"http://img14.360buyimg.com/n1/jfs/t7438/94/1298110837/423771/ded4a5c7/599bd971N2dc3dfc8.jpg","points":11,"product_name":"炊大皇 菜板 三件套装竹砧板切菜板案板面板宝宝辅食板水果板三板组合CB3C","quantity":1,"rewarded_balance":42.6,"unit_price":13.2}],"number":"81519966367","ordered_at":"2018-11-11T03:01:38.000+08:00","rewarded_balance":"42.6","state":"settled","state_zh_cn":"已结算","total_paid":123.4}]
     * total_pages : 10
     * total_rewarded_balance : 22.56
     */

    private int current_page;
    private int total_pages;
    private double total_rewarded_balance;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public double getTotal_rewarded_balance() {
        return total_rewarded_balance;
    }

    public void setTotal_rewarded_balance(double total_rewarded_balance) {
        this.total_rewarded_balance = total_rewarded_balance;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * items : [{"id":1,"image_url":"http://img14.360buyimg.com/n1/jfs/t1/5519/17/9009/504769/5bab4e86E7b094be7/a13f1268f159ddcf.jpg","points":10,"product_name":"迪茵（DIYIN）自粘防水厨房防油贴纸瓷砖灶台用铝箔锡纸加厚橱柜油烟防潮墙贴纸 银灰橘纹10米","quantity":1,"rewarded_balance":9.8,"unit_price":1.2}]
         * number : 81519966666
         * ordered_at : 2018-11-11T00:01:31.000+08:00
         * rewarded_balance : 9.8
         * state : finished
         * state_zh_cn : 已完成
         * total_paid : 12.34
         */

        private int id;
        private String number;
        private String ordered_at;
        private String state;
        private String state_zh_cn;
        private double total_paid;
        private List<ItemsBean> items;
        private String rewarded_balance;
        private String estimated_commission;

        public String getEstimated_commission() {
            return estimated_commission;
        }

        public void setEstimated_commission(String estimated_commission) {
            this.estimated_commission = estimated_commission;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getOrdered_at() {
            return ordered_at;
        }

        public void setOrdered_at(String ordered_at) {
            this.ordered_at = ordered_at;
        }

        public String getRewarded_balance() {
            return rewarded_balance;
        }

        public void setRewarded_balance(String rewarded_balance) {
            this.rewarded_balance = rewarded_balance;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState_zh_cn() {
            return state_zh_cn;
        }

        public void setState_zh_cn(String state_zh_cn) {
            this.state_zh_cn = state_zh_cn;
        }

        public double getTotal_paid() {
            return total_paid;
        }

        public void setTotal_paid(double total_paid) {
            this.total_paid = total_paid;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {
            /**
             * id : 1
             * image_url : http://img14.360buyimg.com/n1/jfs/t1/5519/17/9009/504769/5bab4e86E7b094be7/a13f1268f159ddcf.jpg
             * points : 10
             * product_name : 迪茵（DIYIN）自粘防水厨房防油贴纸瓷砖灶台用铝箔锡纸加厚橱柜油烟防潮墙贴纸 银灰橘纹10米
             * quantity : 1
             * rewarded_balance : 9.8
             * unit_price : 1.2
             */

            private int id;
            private String image_url;
            private int points;
            private String product_name;
            private int quantity;
            private String rewarded_balance;
            private String estimated_commission;
            private String unit_price;

            public String getEstimated_commission() {
                return estimated_commission;
            }

            public void setEstimated_commission(String estimated_commission) {
                this.estimated_commission = estimated_commission;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public int getPoints() {
                return points;
            }

            public void setPoints(int points) {
                this.points = points;
            }

            public String getProduct_name() {
                return product_name;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public String getRewarded_balance() {
                return rewarded_balance;
            }

            public void setRewarded_balance(String rewarded_balance) {
                this.rewarded_balance = rewarded_balance;
            }

            public String getUnit_price() {
                return unit_price;
            }

            public void setUnit_price(String unit_price) {
                this.unit_price = unit_price;
            }
        }
    }
}
