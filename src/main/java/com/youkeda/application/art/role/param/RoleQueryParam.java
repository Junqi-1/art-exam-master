package com.youkeda.application.art.role.param;

import com.youkeda.model.BasePagingParam;

import java.util.List;

/**
 * TODO
 *
 * @date 2020/3/6, 周五
 */
public class RoleQueryParam extends BasePagingParam {

    /**
     * 角色主键
     */
    private List<String> roleIds;

    /**
     * 用户主键
     */
    private String accountId;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 根据role 查询用户
     */
    private String roleId;

    /**
     * 根据权限id查询角色
     */
    private String permissionId;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
