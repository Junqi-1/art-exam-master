package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * 账户服务
 */
public interface AccountService {

    /**
     * 保存账户，创建或者修改
     *
     * @param account
     * @return
     */
    Account save(Account account);

    /**
     * 删除账户
     *
     * @param accountId
     */
    void delete(String accountId);

    /**
     * 获取用户
     *
     * @param param
     * @return
     */
    Paging<Account> query(AccountQueryParam param);

    /**
     * 查询企业下的用户
     *
     * @param companyId
     * @param param
     * @return
     */
    List<Account> query(String companyId, AccountQueryParam param);

}
