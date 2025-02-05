package com.chocolate.nigerialoanapp.bean.response;

public class OrderCheekBean extends EditProfileBean{

    private int order_id;
    private int has_upload;

    public int getOrder_id() {
        return order_id;
    }

    public int getHas_upload() {
        return has_upload;
    }

    public void setHas_upload(int has_upload) {
        this.has_upload = has_upload;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
