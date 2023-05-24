package com.youkeda.application.art.exam.model;

import com.youkeda.application.art.member.model.Account;
import com.youkeda.model.Base;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 报名点
 *
 * @DATE 2021/4/13.
 */
@Document(collection = "art_registration_point", collation = "{locale:'zh'}")
public class RegistrationPoint extends Base<RegistrationPoint> {

    /**
     * 报名点名称
     */
    private String name;

    /**
     * 报名点别称
     */
    private String aliaName;

    /**
     * 报名点地址
     */
    private String address;

    /**
     * 报名点负责人id
     */
    private String chargeId;

    /**
     * 报名点负责人
     */
    @Transient
    private Account chargeAccount;

    /**
     * 考级点id
     */
    private String examOrgId;

    /**
     * 考级点
     */
    @Transient
    private ExamOrg examOrg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliaName() {
        return aliaName;
    }

    public void setAliaName(String aliaName) {
        this.aliaName = aliaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Account getChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(Account chargeAccount) {
        this.chargeAccount = chargeAccount;
    }

    public String getExamOrgId() {
        return examOrgId;
    }

    public void setExamOrgId(String examOrgId) {
        this.examOrgId = examOrgId;
    }

    public ExamOrg getExamOrg() {
        return examOrg;
    }

    public void setExamOrg(ExamOrg examOrg) {
        this.examOrg = examOrg;
    }
}
