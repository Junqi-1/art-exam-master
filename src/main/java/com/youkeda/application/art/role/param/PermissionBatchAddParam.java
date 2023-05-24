package com.youkeda.application.art.role.param;

import java.util.List;

/**
 * TODO
 *
 * @date 2020/6/27, 周六
 */
public class PermissionBatchAddParam {

    /**
     * 权限
     */
    private String permissionId;

    /**
     * 权限
     */
    private List<String> permissionIds;

    /**
     * 用户
     */
    private List<String> accountIds;

    /**
     * 用户
     */
    private String accountId;

    /**
     * 公司
     */
    private String companyId;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
