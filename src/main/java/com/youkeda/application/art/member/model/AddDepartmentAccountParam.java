package com.youkeda.application.art.member.model;

/**
 * @Author
 * @DATE 2019-11-16
 */
public class AddDepartmentAccountParam {
    private String departmentId;
    private Account account;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
