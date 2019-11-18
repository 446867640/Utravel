package com.utravel.app.entity;

import java.util.List;

public class BankCardsBean {

    /**
     * current_page : 1
     * total_pages : 3
     * data : [{"id":1,"bank_name":"中国农业银行","account_number":"**** **** **** 7562","is_default":true}]
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
         * id : 1
         * bank_name : 中国农业银行
         * account_number : **** **** **** 7562
         * is_default : true
         */

        private int id;
        private String name;
        private String account_number;
        private boolean is_default;

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

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public boolean isIs_default() {
            return is_default;
        }

        public void setIs_default(boolean is_default) {
            this.is_default = is_default;
        }
    }
}
