package com.utravel.app.entity;

import java.util.List;

public class CategoryBean  {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * main_image_url : http://images.igolife.net/images/product_category/image/1/%E5%A5%B3%E8%A3%85.jpg?imageView2/1/w/750/h/328/q/75%7Cimageslim
         * name : 潮流女装
         * secondary_categories : [{"id":17,"image_url":"http://images.igolife.net/images/product_category/image/17/%E8%BF%90%E5%8A%A8.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"运动系列"},{"id":16,"image_url":"http://images.igolife.net/images/product_category/image/16/%E5%8D%8A%E8%BA%AB%E8%A3%99.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"半身裙"},{"id":15,"image_url":"http://images.igolife.net/images/product_category/image/15/%E8%A3%A4%E8%A3%85.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"裤装"},{"id":13,"image_url":"http://images.igolife.net/images/product_category/image/13/%E5%A4%96%E5%A5%97.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"精品外套"},{"id":11,"image_url":"http://images.igolife.net/images/product_category/image/11/%E8%BF%9E%E8%A1%A3%E8%A3%99.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"连衣裙"},{"id":12,"image_url":"http://images.igolife.net/images/product_category/image/12/%E5%A5%97%E8%A3%85.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"时尚套装"},{"id":18,"image_url":"http://images.igolife.net/images/product_category/image/18/%E5%A9%9A%E7%BA%B1%E7%A4%BC%E6%9C%8D2.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"婚纱礼服"},{"id":14,"image_url":"http://images.igolife.net/images/product_category/image/14/%E7%BE%BD%E7%BB%92%E6%9C%8D.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"大衣羽绒女"},{"id":2,"image_url":"http://images.igolife.net/images/product_category/image/2/%E4%B8%8A%E8%A1%A3.png?imageView2/1/w/420/h/420/q/75%7Cimageslim","name":"女士上衣"}]
         */

        private int id;
        private String main_image_url;
        private String name;
        private List<SecondaryCategoriesBean> secondary_categories;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain_image_url() {
            return main_image_url;
        }

        public void setMain_image_url(String main_image_url) {
            this.main_image_url = main_image_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SecondaryCategoriesBean> getSecondary_categories() {
            return secondary_categories;
        }

        public void setSecondary_categories(List<SecondaryCategoriesBean> secondary_categories) {
            this.secondary_categories = secondary_categories;
        }

        public static class SecondaryCategoriesBean {
            /**
             * id : 17
             * image_url : http://images.igolife.net/images/product_category/image/17/%E8%BF%90%E5%8A%A8.png?imageView2/1/w/420/h/420/q/75%7Cimageslim
             * name : 运动系列
             */

            private int id;
            private String image_url;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
