package com.youkeda.application.art.exam.model;

import com.youkeda.application.art.member.model.Account;
import com.youkeda.model.Base;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 考级点
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
@Document(collection = "art_exam_org", collation = "{locale:'zh'}")
public class ExamOrg extends Base<ExamOrg> {
    /**
     * 考级点编号
     */
    private String num;

    /**
     * 协会
     */
    private Association association;

    /**
     * 考级点名称
     */
    private String name;

    /**
     * 考级点别称
     */
    private String alias;

    /**
     * 考级点区号
     */
    private String areaCode;

    /**
     * 考级点地址
     */
    private String address;

    /**
     * 领队id
     */
    private String leaderId;

    @Transient
    private Account leaderAccount;

    /**
     * 考级点负责人id
     */
    private String chargeId;

    private List<String> chargeIds;

    /**
     * 考生报名次数
     */
    private Integer signUpIndex = 1;

    @Transient
    private Account chargeAccount;

    @Transient
    private List<Account> chargeAccounts;

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Account getLeaderAccount() {
        return leaderAccount;
    }

    public void setLeaderAccount(Account leaderAccount) {
        this.leaderAccount = leaderAccount;
    }

    public Account getChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(Account chargeAccount) {
        this.chargeAccount = chargeAccount;
    }

    public Integer getSignUpIndex() {
        return signUpIndex;
    }

    public void setSignUpIndex(Integer signUpIndex) {
        this.signUpIndex = signUpIndex;
    }

    public List<String> getChargeIds() {
        return chargeIds;
    }

    public void setChargeIds(List<String> chargeIds) {
        this.chargeIds = chargeIds;
    }

    public List<Account> getChargeAccounts() {
        return chargeAccounts;
    }

    public void setChargeAccounts(List<Account> chargeAccounts) {
        this.chargeAccounts = chargeAccounts;
    }
}
