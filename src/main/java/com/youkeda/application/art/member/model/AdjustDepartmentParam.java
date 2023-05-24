package com.youkeda.application.art.member.model;

import java.util.List;

/**
 * @Author
 * @DATE 2019/11/13.
 */
public class AdjustDepartmentParam {
    /**
     * 源部门id
     */
    private String originDepartmentId;
    /**
     * 目的部门Id
     */
    private String destDepartmentId;

    /**
     * 员工的账户Ids
     */
    private List<String> accountIds;

    public String getDestDepartmentId() {
        return destDepartmentId;
    }

    public void setDestDepartmentId(String destDepartmentId) {
        this.destDepartmentId = destDepartmentId;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public String getOriginDepartmentId() {
        return originDepartmentId;
    }

    public void setOriginDepartmentId(String originDepartmentId) {
        this.originDepartmentId = originDepartmentId;
    }
}
