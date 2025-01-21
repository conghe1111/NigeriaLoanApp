package com.chocolate.nigerialoanapp.bean.response;

public class EditProfileBean {
    //	首借KYC流程 当前步骤，当前流程=111的时候，流程结束，可以借款。
    private int current_phase;
    //下一步骤，如果当前流程=111，下一步骤为0.
    private int next_phase;
    private long server_time;


    public int getCurrent_phase() {
        return current_phase;
    }

    public void setCurrent_phase(int current_phase) {
        this.current_phase = current_phase;
    }

    public int getNext_phase() {
        return next_phase;
    }

    public void setNext_phase(int next_phase) {
        this.next_phase = next_phase;
    }

    public long getServer_time() {
        return server_time;
    }

    public void setServer_time(long server_time) {
        this.server_time = server_time;
    }
}
