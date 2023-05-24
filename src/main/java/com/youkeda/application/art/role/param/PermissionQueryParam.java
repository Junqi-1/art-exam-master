package com.youkeda.application.art.role.param;

import java.util.List;

/**
 * TODO
 *
 * @date 2020/6/22, 周一
 */
public class PermissionQueryParam {

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 关联id
     */
    private String refId;

    /**
     * 权限主键
     */
    private List<String> permissionIds;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
