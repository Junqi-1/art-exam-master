package com.youkeda.application.art.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youkeda.model.Base;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 考试
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
@Document(collection = "art_exam", collation = "{locale:'zh'}")
public class Exam extends Base<Exam> {

    /**
     * 考试名称
     */
    private String name;

    /**
     * 报名开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpStart;

    /**
     * 报名结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpEnd;

    /**
     * 考试开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examStart;

    /**
     * 考试结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examEnd;

    /**
     * 考级点id
     */
    private String examOrgId;

    @Transient
    private ExamOrg examOrg;

    /**
     * 报名点id
     */
    private String registrationId;

    /**
     * 报名点
     */
    @Transient
    private RegistrationPoint registrationPoint;

    /**
     * 考试进度
     */
    private ExamStage examStage;

    /**
     * 结算单
     */
    private String paymentUrl;

    /**
     * 发放单
     */
    private String statementsUrl;

    /**
     * 上传的文件名
     */
    private String fileName;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 考试报名人数
     */
    private long signUpNum = 0;

    public LocalDateTime getSignUpStart() {
        return signUpStart;
    }

    public void setSignUpStart(LocalDateTime signUpStart) {
        this.signUpStart = signUpStart;
    }

    public LocalDateTime getSignUpEnd() {
        return signUpEnd;
    }

    public void setSignUpEnd(LocalDateTime signUpEnd) {
        this.signUpEnd = signUpEnd;
    }

    public LocalDateTime getExamStart() {
        return examStart;
    }

    public void setExamStart(LocalDateTime examStart) {
        this.examStart = examStart;
    }

    public LocalDateTime getExamEnd() {
        return examEnd;
    }

    public void setExamEnd(LocalDateTime examEnd) {
        this.examEnd = examEnd;
    }

    public String getExamOrgId() {
        return examOrgId;
    }

    public void setExamOrgId(String examOrgId) {
        this.examOrgId = examOrgId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getStatementsUrl() {
        return statementsUrl;
    }

    public void setStatementsUrl(String statementsUrl) {
        this.statementsUrl = statementsUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExamOrg getExamOrg() {
        return examOrg;
    }

    public void setExamOrg(ExamOrg examOrg) {
        this.examOrg = examOrg;
    }

    public long getSignUpNum() {
        return signUpNum;
    }

    public void setSignUpNum(long signUpNum) {
        this.signUpNum = signUpNum;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public RegistrationPoint getRegistrationPoint() {
        return registrationPoint;
    }

    public void setRegistrationPoint(RegistrationPoint registrationPoint) {
        this.registrationPoint = registrationPoint;
    }

    public ExamStage getExamStage() {
        return examStage;
    }

    public void setExamStage(ExamStage examStage) {
        this.examStage = examStage;
    }
}
