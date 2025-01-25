package com.chocolate.nigerialoanapp.bean.response;

public class ProductTrialResponse {

    private int stage_no;   //当前期数
    private int stage_total;   //总期数
    private String name;   //产品名字
    private int repay_date;   //还款日期
    private int total;   //应还款总金额
    private int amount;   //本金
    private int disburse_amount;   //放款金额
    private int service_fee;   //服务费
    private int service_fee_prepaid;   //砍头服务费，非砍头产品为0
    private int interest;   //利息
    private int interest_prepaid;   //砍头利息，非砍头产品为0

    public int getStage_no() {
        return stage_no;
    }

    public void setStage_no(int stage_no) {
        this.stage_no = stage_no;
    }

    public int getStage_total() {
        return stage_total;
    }

    public void setStage_total(int stage_total) {
        this.stage_total = stage_total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRepay_date() {
        return repay_date;
    }

    public void setRepay_date(int repay_date) {
        this.repay_date = repay_date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDisburse_amount() {
        return disburse_amount;
    }

    public void setDisburse_amount(int disburse_amount) {
        this.disburse_amount = disburse_amount;
    }

    public int getService_fee() {
        return service_fee;
    }

    public void setService_fee(int service_fee) {
        this.service_fee = service_fee;
    }

    public int getService_fee_prepaid() {
        return service_fee_prepaid;
    }

    public void setService_fee_prepaid(int service_fee_prepaid) {
        this.service_fee_prepaid = service_fee_prepaid;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getInterest_prepaid() {
        return interest_prepaid;
    }

    public void setInterest_prepaid(int interest_prepaid) {
        this.interest_prepaid = interest_prepaid;
    }
}
