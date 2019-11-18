package com.utravel.app.entity;

public class CalculateDetailBean {

    /**
     * data : {"downlines_jingdong_amounts":"0.0","downlines_pdd_amounts":"0.0","downlines_tbk_amounts":"0.0","jingdong_amounts":"0.0","pdd_amounts":"0.0","tbk_amounts":"0.0","total_amounts":"0.0"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * downlines_jingdong_amounts : 0.0
         * downlines_pdd_amounts : 0.0
         * downlines_tbk_amounts : 0.0
         * jingdong_amounts : 0.0
         * pdd_amounts : 0.0
         * tbk_amounts : 0.0
         * total_amounts : 0.0
         */

        private String downlines_jingdong_amounts;
        private String downlines_pdd_amounts;
        private String downlines_tbk_amounts;
        private String jingdong_amounts;
        private String pdd_amounts;
        private String tbk_amounts;
        private String total_amounts;

        public String getDownlines_jingdong_amounts() {
            return downlines_jingdong_amounts;
        }

        public void setDownlines_jingdong_amounts(String downlines_jingdong_amounts) {
            this.downlines_jingdong_amounts = downlines_jingdong_amounts;
        }

        public String getDownlines_pdd_amounts() {
            return downlines_pdd_amounts;
        }

        public void setDownlines_pdd_amounts(String downlines_pdd_amounts) {
            this.downlines_pdd_amounts = downlines_pdd_amounts;
        }

        public String getDownlines_tbk_amounts() {
            return downlines_tbk_amounts;
        }

        public void setDownlines_tbk_amounts(String downlines_tbk_amounts) {
            this.downlines_tbk_amounts = downlines_tbk_amounts;
        }

        public String getJingdong_amounts() {
            return jingdong_amounts;
        }

        public void setJingdong_amounts(String jingdong_amounts) {
            this.jingdong_amounts = jingdong_amounts;
        }

        public String getPdd_amounts() {
            return pdd_amounts;
        }

        public void setPdd_amounts(String pdd_amounts) {
            this.pdd_amounts = pdd_amounts;
        }

        public String getTbk_amounts() {
            return tbk_amounts;
        }

        public void setTbk_amounts(String tbk_amounts) {
            this.tbk_amounts = tbk_amounts;
        }

        public String getTotal_amounts() {
            return total_amounts;
        }

        public void setTotal_amounts(String total_amounts) {
            this.total_amounts = total_amounts;
        }
    }
}
