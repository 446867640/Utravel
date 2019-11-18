package com.utravel.app.entity;

import java.util.List;

public class WoYaoQingDeRenBean1 {
    /**
     * current_page : 1
     * data : [{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"created_at":"2019-09-10T12:04:45.323+08:00","id":4,"mobile":"18278058485","name":"ztluo"}]
     * total_count : 1
     * total_pages : 1
     */

    private int current_page;
    private int total_count;
    private int total_pages;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * avatar : {"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"}
         * created_at : 2019-09-10T12:04:45.323+08:00
         * id : 4
         * mobile : 18278058485
         * name : ztluo
         */

        private AvatarBean avatar;
        private String created_at;
        private int id;
        private String mobile;
        private String name;

        public AvatarBean getAvatar() {
            return avatar;
        }

        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class AvatarBean {
            /**
             * url : http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png
             */

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
