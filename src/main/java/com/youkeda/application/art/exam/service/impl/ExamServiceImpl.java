package com.youkeda.application.art.exam.service.impl;

import com.youkeda.application.art.exam.model.Exam;
import com.youkeda.application.art.exam.model.ExamOrg;
import com.youkeda.application.art.exam.model.ExamStage;
import com.youkeda.application.art.exam.param.ExamQueryParam;
import com.youkeda.application.art.exam.service.ExamOrgService;
import com.youkeda.application.art.exam.service.ExamService;
import com.youkeda.application.art.exam.util.LWConstant;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.DepartmentAccountService;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
@Component
public class ExamServiceImpl implements ExamService {
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExamOrgService examOrgService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentAccountService departmentAccountService;

    @Autowired
    private RoleService roleService;

    @PostConstruct
    private void initData() {
        logger.info("ExamServiceImpl init successful.");
    }

    @Override
    public Exam save(Exam exam) {

        if (exam == null) {
            return new Exam();
        }
        Update update = assembleExamDate(exam);
        if (StringUtils.isBlank(exam.getId())) {
            exam.setId(IDUtils.getId());
        }

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(new ObjectId(exam.getId()))), update, exam.getClass());

        return exam;
    }

    @Override
    public Result<Exam> delete(String examId) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isBlank(examId)) {
            return result;
        }

        result.setData(mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(examId)), Exam.class));
        result.setSuccess(true);
        result.setCode("200");
        return result;

    }

    @Override
    public List<Exam> query(ExamQueryParam param) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(param.getOrgId())) {
            criteria.and("examOrgId").is(param.getOrgId());
        }

        if (!CollectionUtils.isEmpty(param.getOrgIds())) {
            criteria.and("examOrgId").in(param.getOrgIds());
        }

        if (param.getNotExamStage() != null) {
            criteria.and("examStage").ne(param.getNotExamStage());
        }
        if (param.getSignUpStart() != null) {
            criteria.and("signUpStart").gte(param.getSignUpStart());
        }
        if (param.getSignUpEnd() != null) {
            criteria.and("signUpEnd").lte(param.getSignUpEnd());
        }

        if (!CollectionUtils.isEmpty(param.getExamIds())) {
            List<ObjectId> objectIds = new ArrayList<>();
            for (String id : param.getExamIds()) {
                objectIds.add(new ObjectId(id));
            }
            criteria.and("_id").in(objectIds);
        }

        List<AggregationOperation> options = new ArrayList<>();
        options.add(Aggregation.match(criteria));
        options.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "id")));
        if (param.getLimit() > 0) {
            options.add(Aggregation.limit(param.getLimit()));
        }

        Aggregation aggregation = Aggregation.newAggregation(options.toArray(AggregationOperation[]::new));
        List<Exam> mappedResults = mongoTemplate.aggregate(aggregation, Exam.class, Exam.class).getMappedResults();

        if (CollectionUtils.isEmpty(mappedResults)) {
            return mappedResults;
        }

        ExamOrg examOrg = new ExamOrg();
        if (StringUtils.isNotBlank(param.getOrgId())) {
            examOrg = examOrgService.get(param.getOrgId());
        }

        for (Exam mappedResult : mappedResults) {
            mappedResult.setExamOrg(examOrg);
        }

        return mappedResults;
    }

    @Override
    public Exam getCurrentExam(String orgId) {

        if (StringUtils.isBlank(orgId)) {
            return new Exam();
        }

        ExamQueryParam examQueryParam = new ExamQueryParam();
        examQueryParam.setOrgId(orgId);
        examQueryParam.setNotExamStage(ExamStage.UPLOAD);
        examQueryParam.setLimit(1);
        List<Exam> exams = query(examQueryParam);

        if (CollectionUtils.isEmpty(exams)) {
            return new Exam();
        }
        return exams.get(0);
    }

    @Override
    public List<Exam> queryVisibleExams(String orgId, String accountId) {
        List<Exam> exams = new ArrayList<>();

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(orgId)) {

            return exams;

        }

        ExamQueryParam param = new ExamQueryParam();
        param.setOrgId(orgId);
        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setAccountId(accountId);
        List<Role> roles = roleService.query(roleQueryParam);

        if (CollectionUtils.isEmpty(roles)) {
            return exams;
        }

        List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        //管理员角色
        if (roleIds.indexOf(LWConstant.SYSADMIN) > -1 || roleIds.indexOf(LWConstant.MUSICADMIN) > -1) {
            return query(param);
        }

        // 领队角色或考级点负责人角色
        if (roleIds.indexOf(LWConstant.LEADROLEID) > -1 || roleIds.indexOf(LWConstant.CHARGEROLEID) > -1) {
            return query(param);
        }

        return null;
    }

    @Override
    public Result editExamPermission(String orgId, String accountId) {

        Result result = new Result();
        result.setCode("403");
        result.setSuccess(true);
        if (StringUtils.isBlank(orgId) || StringUtils.isBlank(accountId)) {
            return result;
        }

        ExamQueryParam param = new ExamQueryParam();
        param.setOrgId(orgId);
        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setAccountId(accountId);
        List<Role> roles = roleService.query(roleQueryParam);

        List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());

        //考官
        if (CollectionUtils.isEmpty(roleIds)) {
            return result;
        }

        //管理员角色
        if (roleIds.indexOf(LWConstant.SYSADMIN) > -1 || roleIds.indexOf(LWConstant.MUSICADMIN) > -1) {
            result.setCode("200");
            return result;
        }

        ExamOrg examOrg = examOrgService.get(orgId);

        if (StringUtils.equals(examOrg.getLeaderId(), accountId)) {
            result.setCode("201");
            return result;
        }

        if (!CollectionUtils.isEmpty(examOrg.getChargeIds()) && examOrg.getChargeIds().indexOf(accountId) > -1) {
            result.setCode("202");
            return result;
        }

        return result;
    }

    @Override
    public Exam get(String examId) {
        if (StringUtils.isBlank(examId)) {
            return null;
        }
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(examId))), Exam.class);
    }

    private Update assembleExamDate(Exam exam) {

        Update update = new Update();
        if (exam.getSignUpStart() != null) {
            update.set("signUpStart", exam.getSignUpStart());
        }

        if (exam.getSignUpEnd() != null) {
            update.set("signUpEnd", exam.getSignUpEnd());
        }

        if (exam.getExamStart() != null) {
            update.set("examStart", exam.getExamStart());
        }

        if (exam.getExamEnd() != null) {
            update.set("examEnd", exam.getExamEnd());
        }

        if (StringUtils.isNotBlank(exam.getExamOrgId())) {
            update.set("examOrgId", exam.getExamOrgId());
        }

        if (exam.getExamStage() != null) {
            update.set("examStage", exam.getExamStage());
        }

        if (StringUtils.isNotBlank(exam.getPaymentUrl())) {
            update.set("paymentUrl", exam.getPaymentUrl());
        }

        if (StringUtils.isNotBlank(exam.getStatementsUrl())) {
            update.set("statementsUrl", exam.getStatementsUrl());
        }

        if (StringUtils.isNotBlank(exam.getName())) {
            update.set("name", exam.getName());
        }

        if (StringUtils.isNotBlank(exam.getFileName())) {
            update.set("fileName", exam.getFileName());
        }

        if (StringUtils.isNotBlank(exam.getFileUrl())) {
            update.set("fileUrl", exam.getFileUrl());
        }

        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");
        return update;
    }

}
