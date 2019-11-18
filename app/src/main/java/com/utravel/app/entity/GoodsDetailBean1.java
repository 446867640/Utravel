package com.utravel.app.entity;

import java.util.List;

public class GoodsDetailBean1 {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int category_id;
        private String description;
        private int id;
        private String name;
        private String price;
        private String range_price;
        private String highest_sharing_reward;
		private List<ImagesBean> images;
        private List<SpecificationsBean> specifications;
        private List<StockKeepingUnitsBean> stock_keeping_units;

		public String getHighest_sharing_reward() {
			return highest_sharing_reward;
		}

		public void setHighest_sharing_reward(String highest_sharing_reward) {
			this.highest_sharing_reward = highest_sharing_reward;
		}

		public String getRange_price() {
			return range_price;
		}

		public void setRange_price(String range_price) {
			this.range_price = range_price;
		}
		
        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public List<SpecificationsBean> getSpecifications() {
            return specifications;
        }

        public void setSpecifications(List<SpecificationsBean> specifications) {
            this.specifications = specifications;
        }

        public List<StockKeepingUnitsBean> getStock_keeping_units() {
            return stock_keeping_units;
        }

        public void setStock_keeping_units(List<StockKeepingUnitsBean> stock_keeping_units) {
            this.stock_keeping_units = stock_keeping_units;
        }

        public static class ImagesBean {

            private int id;
            private int position;
            private String url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class SpecificationsBean {

            private int id;
            private String name;
            private List<ValuesBean> values;

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

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            public static class ValuesBean {

                private String content;
                private int id;
                private String image_url;
                public int flag = 1;

                @Override
				public String toString() {
					return "ValuesBean [content=" + content + ", id=" + id
							+ ", image_url=" + image_url + ", flag=" + flag
							+ "]";
				}

				public int getFlag() {
					return flag;
				}

				public void setFlag(int flag) {
					this.flag = flag;
				}

				public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

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
            }
        }

        public static class StockKeepingUnitsBean {

            private int id;
            private String price;
            private String reward_points;
            private List<Integer> specification_value_ids;
            private String points;
            
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

            public List<Integer> getSpecification_value_ids() {
                return specification_value_ids;
            }

            public void setSpecification_value_ids(List<Integer> specification_value_ids) {
                this.specification_value_ids = specification_value_ids;
            }
        }
    }
}
