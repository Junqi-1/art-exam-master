package com.youkeda.application.art.exam.param;

import com.youkeda.model.BasePagingParam;

/**
 * @DATE 2021/4/14.
 */
public class RegistrationPointPagingParam extends BasePagingParam<RegistrationPointPagingParam> {

    /**
     * 考级点id
     */
    private String orgId;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 账户id
     */
    private String accountId;

    /**
     * 是否需要考级点信息
     */
    private boolean needOrg = false;

    /**
     * 审核状态
     */
    private String status;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isNeedOrg() {
        return needOrg;
    }

    public void setNeedOrg(boolean needOrg) {
        this.needOrg = needOrg;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
