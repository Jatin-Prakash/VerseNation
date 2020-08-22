package com.marketplace.versenation.payload;

public class UserInfo {
    private String userName;
    private String AccountType;
    private String email;
    private String accountTier;
    private Boolean isBeingFollowed;

    public UserInfo(String userName, String accountType, String email,String accountTier) {
        this.userName = userName;
        this.AccountType = accountType;
        this.email = email;
        this.accountTier=accountTier;
    }

    public String getAccountTier() {
        return accountTier;
    }

    public void setAccountTier(String accountTier) {
        this.accountTier = accountTier;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBeingFollowed() {
        return isBeingFollowed;
    }

    public void setBeingFollowed(Boolean beingFollowed) {
        isBeingFollowed = beingFollowed;
    }
}
