package com.utravel.app.entity;

import java.util.List;

public class DingDanDitailBean1 {

	private DataBean data;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {

		private String current_member_cash_coupons;
		private String current_member_points;
		private String total_amount;
		private String total_cash_coupons;
		private String total_points;
		private String subtotal;
		private String total_points_coupons;
		private String deduction_points;
		private List<ItemsBean> items;

		public String getDeduction_points() {
			return deduction_points;
		}

		public void setDeduction_points(String deduction_points) {
			this.deduction_points = deduction_points;
		}

		public String getSubtotal() {
			return subtotal;
		}

		public void setSubtotal(String subtotal) {
			this.subtotal = subtotal;
		}

		public String getCurrent_member_cash_coupons() {
			return current_member_cash_coupons;
		}

		public void setCurrent_member_cash_coupons(String current_member_cash_coupons) {
			this.current_member_cash_coupons = current_member_cash_coupons;
		}

		public String getCurrent_member_points() {
			return current_member_points;
		}

		public void setCurrent_member_points(String current_member_points) {
			this.current_member_points = current_member_points;
		}

		public String getTotal_cash_coupons() {
			return total_cash_coupons;
		}

		public void setTotal_cash_coupons(String total_cash_coupons) {
			this.total_cash_coupons = total_cash_coupons;
		}

		public String getTotal_points_coupons() {
			return total_points_coupons;
		}

		public void setTotal_points_coupons(String total_points_coupons) {
			this.total_points_coupons = total_points_coupons;
		}

		public String getTotal_amount() {
			return total_amount;
		}

		public void setTotal_amount(String total_amount) {
			this.total_amount = total_amount;
		}

		public String getTotal_points() {
			return total_points;
		}

		public void setTotal_points(String total_points) {
			this.total_points = total_points;
		}

		public List<ItemsBean> getItems() {
			return items;
		}

		public void setItems(List<ItemsBean> items) {
			this.items = items;
		}

		public static class ItemsBean {

			private String image_url;
			private String points;
			private String price;
			private String product_name;
			private int quantity;
			private int stock_keeping_unit_id;
			private List<SpecificationValuesBean> specification_values;

			public String getImage_url() {
				return image_url;
			}

			public void setImage_url(String image_url) {
				this.image_url = image_url;
			}

			public String getPoints() {
				return points;
			}

			public void setPoints(String points) {
				this.points = points;
			}

			public String getPrice() {
				return price;
			}

			public void setPrice(String price) {
				this.price = price;
			}

			public String getProduct_name() {
				return product_name;
			}

			public void setProduct_name(String product_name) {
				this.product_name = product_name;
			}

			public int getQuantity() {
				return quantity;
			}

			public void setQuantity(int quantity) {
				this.quantity = quantity;
			}

			public int getStock_keeping_unit_id() {
				return stock_keeping_unit_id;
			}

			public void setStock_keeping_unit_id(int stock_keeping_unit_id) {
				this.stock_keeping_unit_id = stock_keeping_unit_id;
			}

			public List<SpecificationValuesBean> getSpecification_values() {
				return specification_values;
			}

			public void setSpecification_values(
					List<SpecificationValuesBean> specification_values) {
				this.specification_values = specification_values;
			}

			public static class SpecificationValuesBean {

				private String content;
				private int id;

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
			}
		}
	}
}
