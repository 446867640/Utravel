package com.utravel.app.entity;

import java.util.List;

public class TreeNodesBean {
    /**
     * data : {"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"children":[{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"children":[{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":4,"is_invited":false,"member_id":5,"member_mobile":"150****7873"},{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":5,"is_invited":false,"member_id":6,"member_mobile":"178****0087"}],"id":2,"is_invited":true,"member_id":3,"member_mobile":"189****9518"},{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"children":[],"id":3,"is_invited":false,"member_id":4,"member_mobile":"182****8485"}],"id":1,"member_id":1,"member_mobile":"888****8888"}
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
         * avatar : {"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"}
         * children : [{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"children":[{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":4,"is_invited":false,"member_id":5,"member_mobile":"150****7873"},{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":5,"is_invited":false,"member_id":6,"member_mobile":"178****0087"}],"id":2,"is_invited":true,"member_id":3,"member_mobile":"189****9518"},{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"children":[],"id":3,"is_invited":false,"member_id":4,"member_mobile":"182****8485"}]
         * id : 1
         * member_id : 1
         * member_mobile : 888****8888
         */

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

        public static class ChildrenBeanX {
            /**
             * avatar : {"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"}
             * children : [{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":4,"is_invited":false,"member_id":5,"member_mobile":"150****7873"},{"avatar":{"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"},"id":5,"is_invited":false,"member_id":6,"member_mobile":"178****0087"}]
             * id : 2
             * is_invited : true
             * member_id : 3
             * member_mobile : 189****9518
             */

            private AvatarBeanX avatar;
            private int id;
            private boolean is_invited;
            private int member_id;
            private String member_mobile;
            private List<ChildrenBean> children;
            private String inviter;

            public String getInviter() {
                return inviter;
            }

            public void setInviter(String inviter) {
                this.inviter = inviter;
            }

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

            public boolean isIs_invited() {
                return is_invited;
            }

            public void setIs_invited(boolean is_invited) {
                this.is_invited = is_invited;
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

            public static class ChildrenBean {
                /**
                 * avatar : {"url":"http://hx.igolife.net/assets/default-avatar-19cf8cebb96b4d8beff4ef9cad0e5903d288c778c503777332a57085a65371be.png"}
                 * id : 4
                 * is_invited : false
                 * member_id : 5
                 * member_mobile : 150****7873
                 */

                private AvatarBeanXX avatar;
                private int id;
                private boolean is_invited;
                private int member_id;
                private String member_mobile;
                private String inviter;

                public String getInviter() {
                    return inviter;
                }

                public void setInviter(String inviter) {
                    this.inviter = inviter;
                }

                public AvatarBeanXX getAvatar() {
                    return avatar;
                }

                public void setAvatar(AvatarBeanXX avatar) {
                    this.avatar = avatar;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public boolean isIs_invited() {
                    return is_invited;
                }

                public void setIs_invited(boolean is_invited) {
                    this.is_invited = is_invited;
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

                public static class AvatarBeanXX {
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
    }
}
