package com.youkeda.application.art.role.param;

import com.youkeda.model.BasePagingParam;

/**
 * TODO
 *
 * @date 2020/6/24, 周三
 */
public class PagePermissionAccountParam extends BasePagingParam {

    /**
     * 权限主键
     */
    private String permissionId;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
