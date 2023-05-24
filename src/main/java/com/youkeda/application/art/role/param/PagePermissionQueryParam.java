package com.youkeda.application.art.role.param;

import com.youkeda.model.BasePagingParam;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/30, 周二
 */
public class PagePermissionQueryParam extends BasePagingParam {

    private String companyId;

    private String keyword;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
