package com.youkeda.application.art.member.model;

import java.io.Serializable;

/**
 * 登录用户对象
 */
public class LoginUser implements Serializable {
    private String accountId;
    private String userId;
    private String companyId;

    public LoginUser() {
    }

    public static LoginUser create() {
        return new LoginUser();
    }

    public String getAccountId() {
        return accountId;
    }

    public LoginUser setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public LoginUser setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getCompanyId() {
        return companyId;
    }

    public LoginUser setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }
}
