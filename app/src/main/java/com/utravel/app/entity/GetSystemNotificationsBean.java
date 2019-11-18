package com.utravel.app.entity;

import java.util.List;

public class GetSystemNotificationsBean {

    /**
     * current_page : 1
     * data : [{"content":"呵呵呵呵呵呵中","created_at":"2018-08-22T14:51:58.341+08:00","id":3,"signature":"广西万特云商科技上限公司","title":"大家不太好"},{"content":"这是一条母告","created_at":"2018-08-22T14:51:29.552+08:00","id":2,"signature":"广东百特云商科技没限公司","title":"大家好"},{"content":"这是一条公告","created_at":"2018-08-22T14:50:10.725+08:00","id":1,"signature":"广东万特云商科技有限公司","title":"大家好"}]
     * total_pages : 1
     */

    private int current_page;
    private int total_pages;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * content : 呵呵呵呵呵呵中
         * created_at : 2018-08-22T14:51:58.341+08:00
         * id : 3
         * signature : 广西万特云商科技上限公司
         * title : 大家不太好
         */

        private String content;
        private String created_at;
        private int id;
        private String signature;
        private String title;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
