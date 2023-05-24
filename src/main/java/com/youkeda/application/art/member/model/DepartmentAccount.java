package com.youkeda.application.art.member.model;

import com.youkeda.model.Base;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 * 通讯录的部门人员表
 */
@Document(collection = DepartmentAccount.C_NAME, collation = "{locale: 'zh'}")
public class DepartmentAccount extends Base<DepartmentAccount> {
    private static final long serialVersionUID = -7242062088219364661L;

    public static final String C_NAME = "addressbook_department_accounts";

    @Field(targetType = FieldType.OBJECT_ID)
    private String departmentId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String accountId;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
