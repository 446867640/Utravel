package com.utravel.app.entity;

public class MemeberEntity {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int id;
        private String mobile;
        private int gender;
        private String name;
        private String gender_zh_cn;
        private AvatarBean avatar;
        private boolean has_payment_password;

        public boolean isHas_payment_password() {
            return has_payment_password;
        }

        public void setHas_payment_password(boolean has_payment_password) {
            this.has_payment_password = has_payment_password;
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

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender_zh_cn() {
            return gender_zh_cn;
        }

        public void setGender_zh_cn(String gender_zh_cn) {
            this.gender_zh_cn = gender_zh_cn;
        }

        public AvatarBean getAvatar() {
            return avatar;
        }

        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }

        public static class AvatarBean {

            private int id;
            private String url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
