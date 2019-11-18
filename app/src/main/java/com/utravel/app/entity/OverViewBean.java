package com.utravel.app.entity;

public class OverViewBean {

    /**
     * data : {"balance":"string","uncollected_amount":"string","withdrawn_amount":"string"}
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
         * balance : string
         * uncollected_amount : string
         * withdrawn_amount : string
         */

        private String balance;
        private String uncollected_amount;
        private String withdrawn_amount;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getUncollected_amount() {
            return uncollected_amount;
        }

        public void setUncollected_amount(String uncollected_amount) {
            this.uncollected_amount = uncollected_amount;
        }

        public String getWithdrawn_amount() {
            return withdrawn_amount;
        }

        public void setWithdrawn_amount(String withdrawn_amount) {
            this.withdrawn_amount = withdrawn_amount;
        }
    }
}
