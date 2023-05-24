package com.youkeda.application.art.member.param;

import com.youkeda.model.BasePagingParam;

public class DepartmentAccountParam extends BasePagingParam<DepartmentAccountParam> {
    private static final long serialVersionUID = -4278721586246592139L;

    private String departmentId;

    public String getDepartmentId() {
        return departmentId;
    }

    public DepartmentAccountParam setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
        return this;
    }
}
