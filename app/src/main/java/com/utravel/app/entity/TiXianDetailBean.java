package com.utravel.app.entity;

import java.util.List;

public class TiXianDetailBean {

    private DataBean data;
    private int current_page;
    private int total_pages;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
        /**
         * total : 100.0
         * detail : [{"amount_of_arrival":"100.0","state":"successful","state_zh_cn":"已到账","channel":"alipay","channel_zh_cn":"支付宝","arrival_periods":7,"created_at":"2018-10-25T13:25:53.439+08:00"},{"amount_of_arrival":"200.0","state":"rejected","state_zh_cn":"已拒绝","channel":"wxpay","channel_zh_cn":"微信支付","arrival_periods":1,"created_at":"2018-10-24T13:21:53.430+08:00"}]
         */

        private String total;
        private List<DetailBean> detail;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<DetailBean> getDetail() {
            return detail;
        }

        public void setDetail(List<DetailBean> detail) {
            this.detail = detail;
        }

        public static class DetailBean {
            /**
             * amount_of_arrival : 100.0
             * state : successful
             * state_zh_cn : 已到账
             * channel : alipay
             * channel_zh_cn : 支付宝
             * arrival_periods : 7
             * created_at : 2018-10-25T13:25:53.439+08:00
             */

            private String amount_of_arrival;
            private String state;
            private String state_zh_cn;
            private String channel;
            private String channel_zh_cn;
            private int arrival_periods;
            private String created_at;

            public String getAmount_of_arrival() {
                return amount_of_arrival;
            }

            public void setAmount_of_arrival(String amount_of_arrival) {
                this.amount_of_arrival = amount_of_arrival;
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

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }

            public String getChannel_zh_cn() {
                return channel_zh_cn;
            }

            public void setChannel_zh_cn(String channel_zh_cn) {
                this.channel_zh_cn = channel_zh_cn;
            }

            public int getArrival_periods() {
                return arrival_periods;
            }

            public void setArrival_periods(int arrival_periods) {
                this.arrival_periods = arrival_periods;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }
        }
    }
}
