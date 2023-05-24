package com.youkeda.application.art.role.param;

/**
 * PermissionAccountsParam 查询模型
 *
 * @date 2020/6/22, 周一
 */
public class PermissionAccountsParam {

    /**
     * 权限id
     */
    private String permissionId;

    /**
     * 用户主键
     */
    private String accountId;

    /**
     * 关联id
     */
    private String refId;


    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
