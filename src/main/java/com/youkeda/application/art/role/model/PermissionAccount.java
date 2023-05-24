package com.youkeda.application.art.role.model;

import com.youkeda.model.Base;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permission_accounts", collation = "{locale:'zh'}")
public class PermissionAccount extends Base<PermissionAccount> {

    /**
     * 权限主键
     */
    private String permissionId;

    /**
     * 用户主键
     */
    private String accountId;

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
}
