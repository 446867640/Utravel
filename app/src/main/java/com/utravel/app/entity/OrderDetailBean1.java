package com.utravel.app.entity;

import java.io.Serializable;
import java.util.List;

public class OrderDetailBean1 implements Serializable{

	private String address;
	private String created_at;
	private int id;
	private String number;
	private String paid_amount;
	private String state;
	private String state_zh_cn;
	private String total_amount;
	private String subtotal;
	private String total_cash_coupons;
	private String total_payment_amount;
	private String total_points;
	private String deduction_points;
	private String total_points_coupons;
	private String total_reward_points;
	private String shipping_fees;
	private String updated_at;
	
    private String paid_at;
    private String remark;
    private String payment_at;
    private int refund_id;
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

	public String getPaid_amount() {
		return paid_amount;
	}

	public void setPaid_amount(String paid_amount) {
		this.paid_amount = paid_amount;
	}

	public String getTotal_reward_points() {
		return total_reward_points;
	}

	public void setTotal_reward_points(String total_reward_points) {
		this.total_reward_points = total_reward_points;
	}

	public String getShipping_fees() {
		return shipping_fees;
	}

	public void setShipping_fees(String shipping_fees) {
		this.shipping_fees = shipping_fees;
	}

	public String getTotal_cash_coupons() {
		return total_cash_coupons;
	}

	public void setTotal_cash_coupons(String total_cash_coupons) {
		this.total_cash_coupons = total_cash_coupons;
	}

	public String getTotal_points() {
		return total_points;
	}

	public void setTotal_points(String total_points) {
		this.total_points = total_points;
	}

	public String getTotal_points_coupons() {
		return total_points_coupons;
	}

	public void setTotal_points_coupons(String total_points_coupons) {
		this.total_points_coupons = total_points_coupons;
	}

	public String getPaid_at() {
		return paid_at;
	}

	public void setPaid_at(String paid_at) {
		this.paid_at = paid_at;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPayment_at() {
		return payment_at;
	}

	public void setPayment_at(String payment_at) {
		this.payment_at = payment_at;
	}

	public int getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(int refund_id) {
		this.refund_id = refund_id;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getTotal_payment_amount() {
        return total_payment_amount;
    }

    public void setTotal_payment_amount(String total_payment_amount) {
        this.total_payment_amount = total_payment_amount;
    }
    
    public String getState_zh_cn() {
        return state_zh_cn;
    }

    public void setState_zh_cn(String state_zh_cn) {
        this.state_zh_cn = state_zh_cn;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean implements Serializable{

        private String amount;
        private String product_name;
        private int stock_keeping_unit_id;
        private String price;
        private String reward_points;
        private int quantity;
        private String image_url;
        private List<SpecificationValuesBean> specification_values;
        private String points;

        public String getPoints() {
			return points;
		}

		public void setPoints(String points) {
			this.points = points;
		}
        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public int getStock_keeping_unit_id() {
            return stock_keeping_unit_id;
        }

        public void setStock_keeping_unit_id(int stock_keeping_unit_id) {
            this.stock_keeping_unit_id = stock_keeping_unit_id;
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

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public List<SpecificationValuesBean> getSpecification_values() {
            return specification_values;
        }

        public void setSpecification_values(List<SpecificationValuesBean> specification_values) {
            this.specification_values = specification_values;
        }

        public static class SpecificationValuesBean implements Serializable{
            /**
             * id : 4
             * content : ����
             * image_url : null
             */

            private int id;
            private String content;
            private String image_url;

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

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }
        }
    }
}
