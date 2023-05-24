package com.youkeda.application.art.member.param;

/**
 * 公司查询模型
 */
public class CompanyQueryParam {

    private String code;

    private String domain;

    public static CompanyQueryParam create() {
        return new CompanyQueryParam();
    }

    public String getCode() {
        return code;
    }

    public CompanyQueryParam setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public CompanyQueryParam setDomain(String domain) {
        this.domain = domain;
        return this;
    }

}
