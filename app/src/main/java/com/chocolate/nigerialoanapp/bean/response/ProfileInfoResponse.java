package com.chocolate.nigerialoanapp.bean.response;

public class ProfileInfoResponse {

    private AccountProfile account_profile;
    private AccountReceive account_receive;

    public AccountProfile getAccount_profile() {
        return account_profile;
    }

    public void setAccount_profile(AccountProfile account_profile) {
        this.account_profile = account_profile;
    }

    public AccountReceive getAccount_receive() {
        return account_receive;
    }

    public void setAccount_receive(AccountReceive account_receive) {
        this.account_receive = account_receive;
    }

    public static class AccountProfile {
        private String account_id;
        private String email;
        private String mobile;
        private String middle_name;
        private String first_name;
        private String last_name;
        private String full_name;
        private int gender;
        private String birthday;
        private int marital_status;
        private int education;
        private String bvn;
        private int bvn_verify;
        private int live_verify;
        private String photo_self;
        private int employment;
        private int position;
        private int monthly_income;
        private int salary_day;
        private int other_loan;
        private String contact1;
        private String contact1_name;
        private int contact1_relationship;
        private String contact2;
        private String contact2_name;
        private int contact2_relationship;
        private String home_address;
        private String home_street;
        private String company_name;
        private String company_mobile;
        private String company_address;
        private String app_name;

        public String getAccount_id() {
            return account_id;
        }

        public void setAccount_id(String account_id) {
            this.account_id = account_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMiddle_name() {
            return middle_name;
        }

        public void setMiddle_name(String middle_name) {
            this.middle_name = middle_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getMarital_status() {
            return marital_status;
        }

        public void setMarital_status(int marital_status) {
            this.marital_status = marital_status;
        }

        public int getEducation() {
            return education;
        }

        public void setEducation(int education) {
            this.education = education;
        }

        public String getBvn() {
            return bvn;
        }

        public void setBvn(String bvn) {
            this.bvn = bvn;
        }

        public int getBvn_verify() {
            return bvn_verify;
        }

        public void setBvn_verify(int bvn_verify) {
            this.bvn_verify = bvn_verify;
        }

        public int getLive_verify() {
            return live_verify;
        }

        public void setLive_verify(int live_verify) {
            this.live_verify = live_verify;
        }

        public String getPhoto_self() {
            return photo_self;
        }

        public void setPhoto_self(String photo_self) {
            this.photo_self = photo_self;
        }

        public int getEmployment() {
            return employment;
        }

        public void setEmployment(int employment) {
            this.employment = employment;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getMonthly_income() {
            return monthly_income;
        }

        public void setMonthly_income(int monthly_income) {
            this.monthly_income = monthly_income;
        }

        public int getSalary_day() {
            return salary_day;
        }

        public void setSalary_day(int salary_day) {
            this.salary_day = salary_day;
        }

        public int getOther_loan() {
            return other_loan;
        }

        public void setOther_loan(int other_loan) {
            this.other_loan = other_loan;
        }

        public String getContact1() {
            return contact1;
        }

        public void setContact1(String contact1) {
            this.contact1 = contact1;
        }

        public String getContact1_name() {
            return contact1_name;
        }

        public void setContact1_name(String contact1_name) {
            this.contact1_name = contact1_name;
        }

        public int getContact1_relationship() {
            return contact1_relationship;
        }

        public void setContact1_relationship(int contact1_relationship) {
            this.contact1_relationship = contact1_relationship;
        }

        public String getContact2() {
            return contact2;
        }

        public void setContact2(String contact2) {
            this.contact2 = contact2;
        }

        public String getContact2_name() {
            return contact2_name;
        }

        public void setContact2_name(String contact2_name) {
            this.contact2_name = contact2_name;
        }

        public int getContact2_relationship() {
            return contact2_relationship;
        }

        public void setContact2_relationship(int contact2_relationship) {
            this.contact2_relationship = contact2_relationship;
        }

        public String getHome_address() {
            return home_address;
        }

        public void setHome_address(String home_address) {
            this.home_address = home_address;
        }

        public String getHome_street() {
            return home_street;
        }

        public void setHome_street(String home_street) {
            this.home_street = home_street;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getCompany_mobile() {
            return company_mobile;
        }

        public void setCompany_mobile(String company_mobile) {
            this.company_mobile = company_mobile;
        }

        public String getCompany_address() {
            return company_address;
        }

        public void setCompany_address(String company_address) {
            this.company_address = company_address;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }
    }

    public static class AccountReceive {
        private String account_id;
        private String bank_code;
        private String bank_name;
        private String bank_account_num;
        private String status;

        public String getAccount_id() {
            return account_id;
        }

        public void setAccount_id(String account_id) {
            this.account_id = account_id;
        }

        public String getBank_code() {
            return bank_code;
        }

        public void setBank_code(String bank_code) {
            this.bank_code = bank_code;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getStatus() {
            return status;
        }

        public String getBank_account_num() {
            return bank_account_num;
        }

        public void setBank_account_num(String bank_account_num) {
            this.bank_account_num = bank_account_num;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
