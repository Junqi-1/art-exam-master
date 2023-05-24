package com.youkeda.application.art.member.model;

import java.util.List;

/**
 * @Author
 * @DATE 2019-11-16
 */
public class DeleteAccountParam {
    private String departmentId;
    private List<String> accountIds;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }
}
