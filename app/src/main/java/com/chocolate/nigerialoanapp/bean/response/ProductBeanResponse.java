package com.chocolate.nigerialoanapp.bean.response;

public class ProductBeanResponse {

    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public static class Product {
        private int product_type;   //产品类型：4.营销
        private String product_desc;    //产品类型文本描述
        private int[] amount;   //可借金额 [10000,20000,30000,40000]
        private int[] period;   //可借期限 [7,14]

        public int getProduct_type() {
            return product_type;
        }

        public void setProduct_type(int product_type) {
            this.product_type = product_type;
        }

        public String getProduct_desc() {
            return product_desc;
        }

        public void setProduct_desc(String product_desc) {
            this.product_desc = product_desc;
        }

        public int[] getAmount() {
            return amount;
        }

        public void setAmount(int[] amount) {
            this.amount = amount;
        }

        public int[] getPeriod() {
            return period;
        }

        public void setPeriod(int[] period) {
            this.period = period;
        }
    }

}
