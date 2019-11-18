package com.utravel.app.entity;

import java.util.List;

public class MyTeamBean {


    /**
     * current_page : 1
     * data : [{"avatar":{"id":1,"url":"http://p66xp2ism.bkt.clouddn.com/development/images/avatar/content/12/wheel.png"},"created_at":"2018-04-08T10:55:01.101Z","current_level_name":"���ƻ�Ա","id":1,"name":"Tom"},{"avatar":{"id":2,"url":"http://p66xp2ism.bkt.clouddn.com/development/images/avatar/content/13/default.png"},"created_at":"2018-04-08T10:55:01.101Z","current_level_name":"��ʯ��Ա","id":2,"name":"Jerry"}]
     * total_pages : 10
     */

    private int current_page;
    private int total_pages;
    private int counts;
    private List<DataBean> data;
    
    public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
 
        private AvatarBean avatar;
        private String created_at;
        private String current_level_name;
        private int id;
        private String name;

        public AvatarBean getAvatar() {
            return avatar;
        }

        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCurrent_level_name() {
            return current_level_name;
        }

        public void setCurrent_level_name(String current_level_name) {
            this.current_level_name = current_level_name;
        }

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

        public static class AvatarBean {
            /**
             * id : 1
             * url : http://p66xp2ism.bkt.clouddn.com/development/images/avatar/content/12/wheel.png
             */

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
