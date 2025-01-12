package com.chocolate.nigerialoanapp.bean.response;

public class CheckPhoneNumResponse {
    private String mobile;
    private Long server_time;
    private int is_registered;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getServer_time() {
        return server_time;
    }

    public void setServer_time(Long server_time) {
        this.server_time = server_time;
    }

    public int getIs_registered() {
        return is_registered;
    }

    public void setIs_registered(int is_registered) {
        this.is_registered = is_registered;
    }
}
