package com.youkeda.application.art.exam.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youkeda.application.art.exam.model.ExamStage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
public class ExamQueryParam {
    /**
     * 考级点id
     */
    private String orgId;

    // 多考级点id
    private List<String> orgIds;

    // 多考试 id
    private List<String> examIds;

    /**
     * 除当前考试进度外
     */
    private ExamStage notExamStage;

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

    private int limit;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public ExamStage getNotExamStage() {
        return notExamStage;
    }

    public void setNotExamStage(ExamStage notExamStage) {
        this.notExamStage = notExamStage;
    }

    public List<String> getExamIds() {
        return examIds;
    }

    public void setExamIds(List<String> examIds) {
        this.examIds = examIds;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
