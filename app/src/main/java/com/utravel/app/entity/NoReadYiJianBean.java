package com.utravel.app.entity;

import java.util.List;

public class NoReadYiJianBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private int id;
        private String title;
        private String content;
        
        public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

   

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
