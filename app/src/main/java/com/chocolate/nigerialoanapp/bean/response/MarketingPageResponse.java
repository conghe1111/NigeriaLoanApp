package com.chocolate.nigerialoanapp.bean.response;

public class MarketingPageResponse {

    private int min_amount; //最小金额
    private int max_amount; //最大金额
    private int max_period; //最长期限

    public int getMin_amount() {
        return min_amount;
    }

    public void setMin_amount(int min_amount) {
        this.min_amount = min_amount;
    }

    public int getMax_amount() {
        return max_amount;
    }

    public void setMax_amount(int max_amount) {
        this.max_amount = max_amount;
    }

    public int getMax_period() {
        return max_period;
    }

    public void setMax_period(int max_period) {
        this.max_period = max_period;
    }
}
