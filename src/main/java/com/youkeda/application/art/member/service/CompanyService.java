package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.param.CompanyQueryParam;
import com.youkeda.model.Company;

import java.util.List;

/**
 * 企业服务
 */
public interface CompanyService {
    /**
     * 创建企业，生成一个code
     *
     * @param company
     * @return
     */
    Company save(Company company);

    /**
     * 获取企业
     *
     * @return
     */
    Company find(CompanyQueryParam param);

    /**
     * 根据accountId查询公司信息
     *
     * @param accountId 用户主键
     * @return List<Company>
     */
    List<Company> queryByAccountId(String accountId);

    /**
     * 根据域名查询公司信息
     *
     * @param domainUrl 域名url
     * @return Company
     */
    Company getByDomain(String domainUrl);

    /**
     * 根据主键id删除公司
     */
    void delete(String companyId);

    /**
     * 根据主键id查询企业信息
     */
    Company get(String companyId);

}
