package com.chocolate.nigerialoanapp.bean.response;

import java.util.List;

public class OrderDetailResponse {

    private OrderDetail order_detail;

    public OrderDetail getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(OrderDetail order_detail) {
        this.order_detail = order_detail;
    }

    public static class OrderDetail {
        private long order_id;   //订单ID
        private int check_status;   //订单状态， 状态内容请查看
        private String reject_message;  //拒绝消息
        private int limit_day;  //拒绝之后，剩余多少天可以申请
        private boolean can_apply;  //是否可以申请
        private int info_completed; //个人信息完成 0 未完成 1 完成
        private int auth_upload;    //授信是否上传（过期） 0 未上传 1上传
        private int is_reloan;      //首借，复借， 0 首借 1复借
        private String checkout_url;      //首借，复借， 0 首借 1复借
        private List<Stage> stages; //分期信息

        public long getOrder_id() {
            return order_id;
        }

        public void setOrder_id(long order_id) {
            this.order_id = order_id;
        }

        public int getCheck_status() {
            return check_status;
        }

        public String getCheckout_url() {
            return checkout_url;
        }

        public void setCheckout_url(String checkout_url) {
            this.checkout_url = checkout_url;
        }

        public void setCheck_status(int check_status) {
            this.check_status = check_status;
        }

        public String getReject_message() {
            return reject_message;
        }

        public void setReject_message(String reject_message) {
            this.reject_message = reject_message;
        }

        public int getLimit_day() {
            return limit_day;
        }

        public void setLimit_day(int limit_day) {
            this.limit_day = limit_day;
        }

        public boolean isCan_apply() {
            return can_apply;
        }

        public void setCan_apply(boolean can_apply) {
            this.can_apply = can_apply;
        }

        public int getInfo_completed() {
            return info_completed;
        }

        public void setInfo_completed(int info_completed) {
            this.info_completed = info_completed;
        }

        public int getAuth_upload() {
            return auth_upload;
        }

        public void setAuth_upload(int auth_upload) {
            this.auth_upload = auth_upload;
        }

        public int getIs_reloan() {
            return is_reloan;
        }

        public void setIs_reloan(int is_reloan) {
            this.is_reloan = is_reloan;
        }

        public List<Stage> getStages() {
            return stages;
        }

        public void setStages(List<Stage> stages) {
            this.stages = stages;
        }

    }

    //分期信息
    public static class Stage {

        private int stage_no;   //当前分期
        private int disburse_amount;    //放款金额
        private int repay_total;    //应还总额
        private String repay_date; //还款日期
        private int amount; //应还本金
        private int interest;   //利息
        private int interest_pre_paid;  //砍头利息
        private int service_fee;    //服务费
        private int service_fee_pre_paid;    //砍头服务费
        private int penalty;    //罚息

        public int getStage_no() {
            return stage_no;
        }

        public void setStage_no(int stage_no) {
            this.stage_no = stage_no;
        }

        public int getDisburse_amount() {
            return disburse_amount;
        }

        public void setDisburse_amount(int disburse_amount) {
            this.disburse_amount = disburse_amount;
        }

        public int getRepay_total() {
            return repay_total;
        }

        public void setRepay_total(int repay_total) {
            this.repay_total = repay_total;
        }

        public String getRepay_date() {
            return repay_date;
        }

        public void setRepay_date(String repay_date) {
            this.repay_date = repay_date;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getInterest() {
            return interest;
        }

        public void setInterest(int interest) {
            this.interest = interest;
        }

        public int getInterest_pre_paid() {
            return interest_pre_paid;
        }

        public void setInterest_pre_paid(int interest_pre_paid) {
            this.interest_pre_paid = interest_pre_paid;
        }

        public int getService_fee() {
            return service_fee;
        }

        public void setService_fee(int service_fee) {
            this.service_fee = service_fee;
        }

        public int getService_fee_pre_paid() {
            return service_fee_pre_paid;
        }

        public void setService_fee_pre_paid(int service_fee_pre_paid) {
            this.service_fee_pre_paid = service_fee_pre_paid;
        }

        public int getPenalty() {
            return penalty;
        }

        public void setPenalty(int penalty) {
            this.penalty = penalty;
        }
    }
}
