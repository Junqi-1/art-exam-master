package com.youkeda.application.art.member.param;

import com.youkeda.application.art.member.model.AccountStatus;
import com.youkeda.model.BasePagingParam;

import java.util.List;

/**
 * 账户查询服务
 *
 * @author yingjunjiao
 */
public class AccountQueryParam extends BasePagingParam<AccountQueryParam> {

    private static final long serialVersionUID = 4679489114099008390L;

    private String name;

    /**
     * 用户名称
     */
    private String userName;

    private String mobile;

    private List<String> mobiles;

    private String accountId;
    /**
     * 账户集合
     */
    private List<String> accountIds;

    /**
     * 用户id
     */
    private String userId;

    private String companyId;

    private AccountStatus status;

    public AccountQueryParam() {

    }

    public static AccountQueryParam create() {
        return new AccountQueryParam();
    }

    public String getName() {
        return name;
    }

    public AccountQueryParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public AccountQueryParam setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getAccountId() {
        return accountId;
    }

    public AccountQueryParam setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public AccountQueryParam setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public AccountQueryParam setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getCompanyId() {
        return companyId;
    }

    public AccountQueryParam setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    public List<String> getMobiles() {
        return mobiles;
    }

    public AccountQueryParam setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
        return this;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public AccountQueryParam setStatus(AccountStatus status) {
        this.status = status;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AccountQueryParam setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
