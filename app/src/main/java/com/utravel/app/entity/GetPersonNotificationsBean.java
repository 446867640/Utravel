package com.utravel.app.entity;

import java.util.List;

public class GetPersonNotificationsBean {

    /**
     * current_page : 1
     * data : [{"content":"收到转账 1000 元","created_at":"2018-08-20T14:04:51.936+08:00","id":1,"payload":{"id":3,"type":"balance"},"read":false,"tag":"account_change"}]
     * total_pages : 10
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
         * content : 收到转账 1000 元
         * created_at : 2018-08-20T14:04:51.936+08:00
         * id : 1
         * payload : {"id":3,"type":"balance"}
         * read : false
         * tag : account_change
         */

        private String content;
        private String created_at;
        private int id;
        private PayloadBean payload;
        private boolean read;
        private String tag;

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

        public PayloadBean getPayload() {
            return payload;
        }

        public void setPayload(PayloadBean payload) {
            this.payload = payload;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public static class PayloadBean {
            /**
             * id : 3
             * type : balance
             */

            private int id;
            private String type;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
