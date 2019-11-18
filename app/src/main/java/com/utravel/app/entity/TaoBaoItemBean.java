package com.utravel.app.entity;

import java.util.List;

public class TaoBaoItemBean {

    /**
     * request_id : v1ww2ltnfklv
     * results : [{"cat_leaf_name":"帽子","cat_name":"服饰配件/皮带/帽子/围巾","item_url":"https://detail.m.tmall.com/item.htm?id=578071112189","material_lib_type":"1,2","nick":"橙影旗舰店","num_iid":578071112189,"pict_url":"https://img.alicdn.com/bao/uploaded/i1/3339200673/O1CN011rj2ga1GqH6mlQoLY_!!0-item_pic.jpg","provcity":"浙江 金华","reserve_price":"128","seller_id":3339200673,"small_images":["https://img.alicdn.com/i2/3339200673/O1CN01LHat3e1GqH38Sd03f_!!0-item_pic.jpg","https://img.alicdn.com/i2/3339200673/O1CN01aL9vsu1GqH22EvxcM_!!3339200673.jpg","https://img.alicdn.com/i3/3339200673/TB2Jx0FdHvpK1RjSZPiXXbmwXXa_!!3339200673.jpg","https://img.alicdn.com/i1/3339200673/TB2stVZdNnaK1RjSZFtXXbC2VXa_!!3339200673.jpg"],"title":"雷锋帽男士韩版保暖棉帽户外防寒东北加厚加绒帽子女冬季骑车防风","user_type":1,"volume":2488,"zk_final_price":"13.8"}]
     */

    private String request_id;
    private List<ResultsBean> results;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * cat_leaf_name : 帽子
         * cat_name : 服饰配件/皮带/帽子/围巾
         * item_url : https://detail.m.tmall.com/item.htm?id=578071112189
         * material_lib_type : 1,2
         * nick : 橙影旗舰店
         * num_iid : 578071112189
         * pict_url : https://img.alicdn.com/bao/uploaded/i1/3339200673/O1CN011rj2ga1GqH6mlQoLY_!!0-item_pic.jpg
         * provcity : 浙江 金华
         * reserve_price : 128
         * seller_id : 3339200673
         * small_images : ["https://img.alicdn.com/i2/3339200673/O1CN01LHat3e1GqH38Sd03f_!!0-item_pic.jpg","https://img.alicdn.com/i2/3339200673/O1CN01aL9vsu1GqH22EvxcM_!!3339200673.jpg","https://img.alicdn.com/i3/3339200673/TB2Jx0FdHvpK1RjSZPiXXbmwXXa_!!3339200673.jpg","https://img.alicdn.com/i1/3339200673/TB2stVZdNnaK1RjSZFtXXbC2VXa_!!3339200673.jpg"]
         * title : 雷锋帽男士韩版保暖棉帽户外防寒东北加厚加绒帽子女冬季骑车防风
         * user_type : 1
         * volume : 2488
         * zk_final_price : 13.8
         */

        private String cat_leaf_name;
        private String cat_name;
        private String item_url;
        private String material_lib_type;
        private String nick;
        private long num_iid;
        private String pict_url;
        private String provcity;
        private String reserve_price;
        private long seller_id;
        private String title;
        private int user_type;
        private int volume;
        private String zk_final_price;
        private List<String> small_images;

        public String getCat_leaf_name() {
            return cat_leaf_name;
        }

        public void setCat_leaf_name(String cat_leaf_name) {
            this.cat_leaf_name = cat_leaf_name;
        }

        public String getCat_name() {
            return cat_name;
        }

        public void setCat_name(String cat_name) {
            this.cat_name = cat_name;
        }

        public String getItem_url() {
            return item_url;
        }

        public void setItem_url(String item_url) {
            this.item_url = item_url;
        }

        public String getMaterial_lib_type() {
            return material_lib_type;
        }

        public void setMaterial_lib_type(String material_lib_type) {
            this.material_lib_type = material_lib_type;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public long getNum_iid() {
            return num_iid;
        }

        public void setNum_iid(long num_iid) {
            this.num_iid = num_iid;
        }

        public String getPict_url() {
            return pict_url;
        }

        public void setPict_url(String pict_url) {
            this.pict_url = pict_url;
        }

        public String getProvcity() {
            return provcity;
        }

        public void setProvcity(String provcity) {
            this.provcity = provcity;
        }

        public String getReserve_price() {
            return reserve_price;
        }

        public void setReserve_price(String reserve_price) {
            this.reserve_price = reserve_price;
        }

        public long getSeller_id() {
            return seller_id;
        }

        public void setSeller_id(long seller_id) {
            this.seller_id = seller_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public String getZk_final_price() {
            return zk_final_price;
        }

        public void setZk_final_price(String zk_final_price) {
            this.zk_final_price = zk_final_price;
        }

        public List<String> getSmall_images() {
            return small_images;
        }

        public void setSmall_images(List<String> small_images) {
            this.small_images = small_images;
        }
    }
}
