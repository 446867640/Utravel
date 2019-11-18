package com.utravel.app.entity;

import java.util.List;

public class WoYaoQingDeRenBean {
    private DataBean data;
    public DataBean getData() {
        return data;
    }
    public void setData(DataBean data) {
        this.data = data;
    }
    public static class DataBean {
        private AvatarBean avatar;
        private int id;
        private int member_id;
        private String member_mobile;
        private List<ChildrenBeanX> children;
        public AvatarBean getAvatar() {
            return avatar;
        }
        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getMember_id() {
            return member_id;
        }
        public void setMember_id(int member_id) {
            this.member_id = member_id;
        }
        public String getMember_mobile() {
            return member_mobile;
        }
        public void setMember_mobile(String member_mobile) {
            this.member_mobile = member_mobile;
        }
        public List<ChildrenBeanX> getChildren() {
            return children;
        }
        public void setChildren(List<ChildrenBeanX> children) {
            this.children = children;
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
        public static class ChildrenBeanX {
            private AvatarBeanX avatar;
            private int id;
            private int member_id;
            private String member_mobile;
            private List<ChildrenBean> children;
            public AvatarBeanX getAvatar() {
                return avatar;
            }
            public void setAvatar(AvatarBeanX avatar) {
                this.avatar = avatar;
            }
            public int getId() {
                return id;
            }
            public void setId(int id) {
                this.id = id;
            }
            public int getMember_id() {
                return member_id;
            }
            public void setMember_id(int member_id) {
                this.member_id = member_id;
            }
            public String getMember_mobile() {
                return member_mobile;
            }
            public void setMember_mobile(String member_mobile) {
                this.member_mobile = member_mobile;
            }
            public List<ChildrenBean> getChildren() {
                return children;
            }
            public void setChildren(List<ChildrenBean> children) {
                this.children = children;
            }
            public static class AvatarBeanX {
            }
            public static class ChildrenBean {
            }
        }
    }
}
