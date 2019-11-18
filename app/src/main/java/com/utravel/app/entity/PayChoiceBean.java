package com.utravel.app.entity;

import java.util.List;

public class PayChoiceBean {

    /**
     * data : {"amount":"100.0","balance":[{"balance":"800.0","code":"merchant"},{"balance":"1000.0","code":"realized"},{"balance":"1000.0","code":"recharged"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String amount;
        private String account_balance;
        private List<BalanceBean> balance;
        
        public String getAccount_balance() {
			return account_balance;
		}

		public void setAccount_balance(String account_balance) {
			this.account_balance = account_balance;
		}


        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public List<BalanceBean> getBalance() {
            return balance;
        }

        public void setBalance(List<BalanceBean> balance) {
            this.balance = balance;
        }

        public static class BalanceBean {
            /**
             * balance : 800.0
             * code : merchant
             */

            private String balance;
            private String code;

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }
        }
    }
}
