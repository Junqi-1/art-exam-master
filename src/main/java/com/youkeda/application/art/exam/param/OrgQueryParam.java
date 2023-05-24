package com.youkeda.application.art.exam.param;

import com.youkeda.application.art.exam.model.Association;
import com.youkeda.model.BasePagingParam;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
public class OrgQueryParam extends BasePagingParam<OrgQueryParam> {
    /**
     * 协会类型
     */
    private Association association;
    /**
     * 领队accountId
     */
    private String leadId;
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 领队或者考级点负责人
     */
    private String adminId;

    private List<String> orgIds;
    /**
     * 考级点负责人accountId
     */
    private String chargeId;

    /**
     * 多个考级点负责人accountId
     */
    private List<String> chargeIds;

    /**
     * 考级点编号
     */
    private String num;

    /**
     * 需要查询领队和负责人
     */
    private boolean wantAccount = false;

    /**
     * 根据考级点创建 gmtCreated 年份查询
     */
    private int year;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean wantAccount() {
        return wantAccount;
    }

    public void setWantAccount(boolean wantAccount) {
        this.wantAccount = wantAccount;
    }

    public List<String> getChargeIds() {
        return chargeIds;
    }

    public void setChargeIds(List<String> chargeIds) {
        this.chargeIds = chargeIds;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }
}
