package com.utravel.app.entity;

import java.util.List;

public class NewProductSearchBean {
    /**
     * current_page : 1
     * total_pages : 3
     * data : [{"id":23,"name":"iphone X","category_id":22,"price":"8800.0","reward_points":"8800.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/74/ios.png?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":22,"name":"森诗测试专用","category_id":1,"price":"200.0","reward_points":"1000.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/72/Simulator_Screen_Shot_-_iPhone_XS_Max_-_2018-11-28_at_10.04.42.png?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":21,"name":"Subaru BRZ","category_id":19,"price":"0.01","reward_points":"9.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/71/boxer.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":20,"name":"细跟短靴女秋冬新款欧美时尚兔毛毛尖头细高跟靴子短筒冬靴","category_id":2,"price":"799.0","reward_points":"6392.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/68/1.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":19,"name":"迪奥希高跟鞋女细跟尖头浅口五金装饰米白色胎牛皮欧美风秋季新款","category_id":2,"price":"439.0","reward_points":"3512.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/65/1.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":18,"name":"风衣女中长款韩版秋季2018新款棉立方春秋装端庄大气宽松chic外套","category_id":2,"price":"299.0","reward_points":"2392.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/62/1.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":17,"name":"毛呢大衣女反季双面羊绒韩版中长款2018新款呢子外套","category_id":2,"price":"799.0","reward_points":"6392.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/59/1.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":16,"name":"100%纯羊毛衫女半高领中长款套头毛衣裙秋冬拼色打底衫针织连衣裙","category_id":2,"price":"299.0","reward_points":"2392.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/56/1.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":15,"name":"开衫针织女中长款2018冬季新款韩版春秋披风外套毛衣慵懒风针织衫","category_id":2,"price":"199.0","reward_points":"1592.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/53/151.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"},{"id":14,"name":"女童风衣2018新款春秋韩版洋气秋装童装秋儿童大童中长款秋冬外套","category_id":2,"price":"159.0","reward_points":"1272.0","image_url":"http://images.staging.wanteyun.com/staging/images/product_image/content/50/141.jpg?imageView2/1/w/420/h/420/q/75%7Cimageslim"}]
     */

    private int current_page;
    private int total_pages;
    private List<DataBean> data;

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
        /**
         * id : 23
         * name : iphone X
         * category_id : 22
         * price : 8800.0
         * reward_points : 8800.0
         * image_url : http://images.staging.wanteyun.com/staging/images/product_image/content/74/ios.png?imageView2/1/w/420/h/420/q/75%7Cimageslim
         */

        private int id;
        private String name;
        private int category_id;
        private String price;
        private String points;
        private String reward_points;
        private String image_url;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
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

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getReward_points() {
            return reward_points;
        }

        public void setReward_points(String reward_points) {
            this.reward_points = reward_points;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}

