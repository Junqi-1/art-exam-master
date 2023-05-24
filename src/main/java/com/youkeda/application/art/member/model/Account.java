package com.youkeda.application.art.member.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youkeda.model.Base;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

/**
 * 账户模型,用于区分登录模型和账户，业务只关心 account 就可以了
 */
@Document(collection = "accounts", collation = "{locale: 'zh'}")
public class Account extends Base<Account> {

    @Transient
    public static final Account DEFAULT = new Account("5da98c79431bc630043b719f", "ykd", AccountStatus.enabled);

    private static final long serialVersionUID = 8885480567675268182L;
    /**
     * 账户名称，用于手工创建
     */
    private String accountName;

    /**
     * 帐户手机号码，用于企业创建帐户
     */
    private String accountMobile;

    @Transient
    private User user;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;

    private AccountProfile profile = new AccountProfile();

    private AccountStatus status = AccountStatus.enabled;

    private String oldId;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    /**
     * 用户工号
     */
    private String jobNumber;

    /**
     * 是否被封禁
     */
    private boolean ban;

    public String getEmail() {
        return email;
    }

    public LocalDateTime getJoinTime() {
        if (joinTime == null) {
            return this.getGmtCreated();
        }
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public Account() {

    }

    public Account(String id, String accountName, AccountStatus status) {
        setId(id);
        setAccountName(accountName);
        setStatus(status);
    }

    /**
     * 创建帐户模型
     */
    public static Account create() {
        return new Account();
    }

    public String getAccountName() {
        return accountName;
    }

    public Account setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public String getAccountMobile() {
        return accountMobile;
    }

    public Account setAccountMobile(String accountMobile) {
        this.accountMobile = accountMobile;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Account setUser(User user) {
        this.user = user;
        return this;
    }

    public AccountProfile getProfile() {
        return profile;
    }

    public void setProfile(AccountProfile profile) {
        this.profile = profile;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }
}
