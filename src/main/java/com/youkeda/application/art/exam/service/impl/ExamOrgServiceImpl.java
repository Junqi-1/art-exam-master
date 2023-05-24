package com.youkeda.application.art.exam.service.impl;

import com.google.common.collect.Lists;
import com.youkeda.application.art.exam.model.Association;
import com.youkeda.application.art.exam.model.Exam;
import com.youkeda.application.art.exam.model.ExamOrg;
import com.youkeda.application.art.exam.param.ExamQueryParam;
import com.youkeda.application.art.exam.param.OrgQueryParam;
import com.youkeda.application.art.exam.service.ExamOrgService;
import com.youkeda.application.art.exam.service.ExamService;
import com.youkeda.application.art.exam.util.LWConstant;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.DepartmentAccountService;
import com.youkeda.application.art.member.util.DateUtil;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.application.art.role.service.PermissionService;
import com.youkeda.application.art.role.service.RoleService;
import com.youkeda.model.Paging;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
@Service
public class ExamOrgServiceImpl implements ExamOrgService {

    private static Logger logger = LoggerFactory.getLogger(ExamOrgServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Lazy
    private ExamService examService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentAccountService departmentAccountService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionAccountsService permissionAccountsService;

    @PostConstruct
    private void initData() {
        logger.info("ExamOrgServiceImpl init successful.");
    }

    @Override
    public ExamOrg save(ExamOrg examOrg) {

        if (examOrg == null) {
            return new ExamOrg();
        }

        Update update = assembleExamOrgUpdate(examOrg);

        ExamOrg org = new ExamOrg();
        if (StringUtils.isNotBlank(examOrg.getLeaderId())) {
            org = get(examOrg.getId());
            // 修改模式时，先删除原领队的角色
            if (StringUtils.isNotBlank(examOrg.getId()) && StringUtils.isNotBlank(org.getLeaderId())) {
                // 查询领队账户管理的所有考级点
                OrgQueryParam param = new OrgQueryParam();
                param.setLeadId(org.getLeaderId());
                List<ExamOrg> examOrgList = query(param);
                //如果领队账户带领的考级点只有一个，就可以删除领队账户的领队角色
                if (examOrgList.size() <= 1) {
                    List<String> leadIds = new ArrayList<>();
                    String leaderId = org.getLeaderId();
                    leadIds.add(leaderId);
                    roleService.removeRole(leadIds, LWConstant.LEADROLEID);
                }
            }
            // 为领队添加领队角色
            List<String> leadIds = Lists.newArrayList(examOrg.getLeaderId());
            update.set("leaderId", examOrg.getLeaderId());
            roleService.batchAddRole(LWConstant.LEADROLEID, leadIds, new ArrayList<>());
        }

        if (!CollectionUtils.isEmpty(examOrg.getChargeIds())) {
            // 修改模式时，先删除原负责人的角色
            if (StringUtils.isNotBlank(examOrg.getId()) && !CollectionUtils.isEmpty(org.getChargeIds())) {
                //获取负责人管理的考级点
                Map<String, List<ExamOrg>> chargeOrgMap = queryChargeOrg(org.getChargeIds());
                List<String> chargeIds = new ArrayList<>();
                //将只管理一个考级点的负责人移除角色
                for (String chargeId : org.getChargeIds()) {
                    List<ExamOrg> examOrgs = chargeOrgMap.get(chargeId);
                    if (examOrgs.size() <= 1) {
                        chargeIds.add(chargeId);
                    }
                }
                //移除只管理一个的负责人
                roleService.removeRole(chargeIds, LWConstant.CHARGEROLEID);

            }

            // 为负责人添加角色
            update.set("chargeIds", examOrg.getChargeIds());
            roleService.batchAddRole(LWConstant.CHARGEROLEID, examOrg.getChargeIds(), new ArrayList<>());

        }

        if (StringUtils.isBlank(examOrg.getId())) {
            examOrg.setId(IDUtils.getId());
        }

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(new ObjectId(examOrg.getId()))), update, ExamOrg.class);

        return examOrg;
    }

    @Override
    public Result<ExamOrg> delete(String orgId) {
        Result<ExamOrg> result = new Result<>();
        if (StringUtils.isBlank(orgId)) {
            return result;
        }

        // 检查考级点关联的考试，有考试则不允许删除
        ExamQueryParam param = new ExamQueryParam();
        param.setOrgId(orgId);
        List<Exam> exams = examService.query(param);

        if (!CollectionUtils.isEmpty(exams)) {
            result.setCode("5000");
            return result;
        }

        ExamOrg examOrg = get(orgId);
        List<String> leadIds = new ArrayList<>();
        leadIds.add(examOrg.getLeaderId());

        //如果领队带领只有一个考级点 删除用户领队角色
        OrgQueryParam orgQueryParam = new OrgQueryParam();
        orgQueryParam.setLeadId(examOrg.getLeaderId());
        List<ExamOrg> examOrgList = query(orgQueryParam);
        if (examOrgList.size() == 1) {
            roleService.removeRole(leadIds, LWConstant.LEADROLEID);
        }

        //获取负责人管理的考级点
        if (!CollectionUtils.isEmpty(examOrg.getChargeIds())) {
            Map<String, List<ExamOrg>> chargeOrgMap = queryChargeOrg(examOrg.getChargeIds());
            List<String> chargeIds = new ArrayList<>();
            //将只管理一个考级点的负责人移除权限
            for (String chargeId : examOrg.getChargeIds()) {
                List<ExamOrg> examOrgs = chargeOrgMap.get(chargeId);
                if (examOrgs.size() == 1) {
                    chargeIds.add(chargeId);
                }
            }
            //移除只管理一个的负责人
            roleService.removeRole(chargeIds, LWConstant.CHARGEROLEID);
        }

        result.setData(
            mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(new ObjectId(orgId))), ExamOrg.class));
        result.setCode("2000");
        result.setSuccess(true);
        return result;

    }

    @Override
    public List<ExamOrg> query(OrgQueryParam orgQueryParam) {

        if (orgQueryParam == null) {
            orgQueryParam = new OrgQueryParam();
        }

        Criteria ca = getOrgQuery(orgQueryParam);

        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(orgQueryParam.getAdminId())) {
            criteria.orOperator(Criteria.where("chargeIds").is(orgQueryParam.getAdminId()),
                                Criteria.where("leaderId").is(orgQueryParam.getAdminId()));
        }

        Criteria cr = new Criteria();
        if (StringUtils.isNotBlank(orgQueryParam.getKeyword())) {
            cr.orOperator(Criteria.where("name").regex(orgQueryParam.getKeyword()),
                          Criteria.where("alias").regex(orgQueryParam.getKeyword()));
        }
        Aggregation aggregation = null;

        if (StringUtils.isNotBlank(orgQueryParam.getAdminId())) {
            aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().andOperator(criteria, cr)),
                                                     Aggregation.sort(Sort.by(Sort.Direction.DESC, "id")));
        } else {
            aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().andOperator(ca, cr)),
                                                     Aggregation.sort(Sort.by(Sort.Direction.DESC, "id")));
        }

        List<ExamOrg> examOrgs = mongoTemplate.aggregate(aggregation, ExamOrg.class, ExamOrg.class).getMappedResults();

        if (examOrgs.size() > 20) {
            examOrgs = examOrgs.subList(0, 20);
        }

        //组装负责人和领队
        if (orgQueryParam.wantAccount()) {
            List<ExamOrg> examOrgDetail = getExamOrgDetail(examOrgs);
            return examOrgDetail;
        }

        return examOrgs;

    }

    @Override
    public Paging<ExamOrg> pageQuery(OrgQueryParam orgQueryParam) {
        List<AggregationOperation> operations = new ArrayList<>();
        Paging<ExamOrg> pageDate = new Paging<>();
        Criteria ca = getOrgQuery(orgQueryParam);
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(orgQueryParam.getAdminId())) {
            criteria.orOperator(Criteria.where("chargeIds").is(orgQueryParam.getAdminId()),
                                Criteria.where("leaderId").is(orgQueryParam.getAdminId()));

            getOrgQueryTime(orgQueryParam, criteria);
        }

        Criteria cr = new Criteria();
        if (StringUtils.isNotBlank(orgQueryParam.getKeyword())) {
            cr.orOperator(Criteria.where("name").regex(orgQueryParam.getKeyword()),
                          Criteria.where("alias").regex(orgQueryParam.getKeyword()));
        }

        if (StringUtils.isNotBlank(orgQueryParam.getAdminId())) {
            operations.add(match((new Criteria().andOperator(criteria, cr))));
        } else {
            operations.add(match((new Criteria().andOperator(ca, cr))));
        }

        pageDate = Paging.compute(mongoTemplate, ExamOrg.class, operations, orgQueryParam);
        operations.add(sort(Sort.by(Sort.Direction.DESC, "gmtCreated")));

        operations.add(skip(orgQueryParam.getPagination() * orgQueryParam.getPageSize()));
        operations.add(limit(orgQueryParam.getPageSize()));

        List<ExamOrg> examOrgs = mongoTemplate.aggregate(Aggregation.newAggregation(operations), ExamOrg.class,
                                                         ExamOrg.class).getMappedResults();

        if (orgQueryParam.wantAccount()) {
            List<ExamOrg> examOrgDetail = getExamOrgDetail(examOrgs);
            pageDate.setData(examOrgDetail);
            return pageDate;
        }

        pageDate.setData(examOrgs);
        return pageDate;
    }

    @Override
    public List<Account> queryAllExaminers(String departmentId, String keyword, String companyId) {

        List<Account> examiners = new ArrayList<>();

        if (StringUtils.isBlank(departmentId)) {
            return new ArrayList<>();
        }

        List<Account> accountList = departmentAccountService.queryAccountsByKeyword(departmentId, keyword, companyId);

        if (CollectionUtils.isEmpty(accountList)) {
            return examiners;
        }

        return accountList;
    }

    @Override
    public ExamOrg get(String examOrgId) {

        if (StringUtils.isBlank(examOrgId)) {
            return new ExamOrg();
        }

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(examOrgId)), ExamOrg.class);

    }

    @Override
    public ExamOrg updateSignUpIndex(String orgId) {

        if (StringUtils.isBlank(orgId)) {
            return new ExamOrg();
        }

        Update update = new Update();
        // 设置 signUpIndex 为自增字段
        update.inc("signUpIndex");

        // findAndModify() 是原子操作函数，它可以指定将获取某个键并同时进行增长一定的值
        ExamOrg examOrg = mongoTemplate.findAndModify(new Query(Criteria.where("_id").is(new ObjectId(orgId))), update,
                                                      options().returnNew(true).upsert(true), ExamOrg.class);
        return examOrg;
    }

    @Override
    public Paging<ExamOrg> queryVisibleOrgs(OrgQueryParam orgQueryParam) {

        Paging<ExamOrg> examOrgs = new Paging<>();
        String accountId = orgQueryParam.getAdminId();
        String keyword = orgQueryParam.getKeyword();

        if (StringUtils.isBlank(accountId)) {
            return examOrgs;
        }

        //查询用户所有角色
        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setAccountId(accountId);
        List<Role> roles = roleService.query(roleQueryParam);
        if (CollectionUtils.isEmpty(roles)) {
            return examOrgs;
        }

        OrgQueryParam param = new OrgQueryParam();
        param.setKeyword(keyword);
        param.setWantAccount(true);
        param.setPageSize(orgQueryParam.getPageSize());
        param.setPagination(orgQueryParam.getPagination());
        param.setYear(orgQueryParam.getYear());

        List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        //总管理员角色
        if (roleIds.indexOf(LWConstant.SYSADMIN) > -1) {
            return pageQuery(param);
        }

        //音乐管理员
        if (roleIds.indexOf(LWConstant.MUSICADMIN) > -1) {
            param.setAssociation(Association.MUSIC);
            return pageQuery(param);
        }

        // 领队角色或考级点负责人角色
        if (roleIds.indexOf(LWConstant.LEADROLEID) > -1 || roleIds.indexOf(LWConstant.CHARGEROLEID) > -1) {
            param.setAdminId(accountId);
            return pageQuery(param);
        }

        return null;
    }

    @Override
    public Result<List<String>> editOrgPermission(String accountId) {

        Result<List<String>> result = new Result<>();
        result.setCode("403");
        if (StringUtils.isBlank(accountId)) {
            return result;
        }

        //查询用户所有角色
        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setAccountId(accountId);
        List<Role> roles = roleService.query(roleQueryParam);

        if (CollectionUtils.isEmpty(roles)) {
            return result;
        }

        List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        if (roleIds.indexOf(LWConstant.SYSADMIN) > -1 || roleIds.indexOf(LWConstant.MUSICADMIN) > -1) {
            result.setCode("200");
            return result;
        }

        OrgQueryParam orgQueryParam = new OrgQueryParam();
        orgQueryParam.setAdminId(accountId);
        List<ExamOrg> examOrgs = query(orgQueryParam);

        if (CollectionUtils.isEmpty(examOrgs)) {
            return result;
        }

        result.setCode("201");
        result.setData(examOrgs.stream().map(ExamOrg::getId).collect(Collectors.toList()));
        return result;
    }

    private Map<String, List<ExamOrg>> queryChargeOrg(List<String> chargeIds) {

        Map<String, List<ExamOrg>> chargeExamOrgMap = new HashMap<>();
        if (CollectionUtils.isEmpty(chargeIds)) {
            return chargeExamOrgMap;
        }

        OrgQueryParam orgQueryParam = new OrgQueryParam();
        orgQueryParam.setChargeIds(chargeIds);
        List<ExamOrg> examOrgList = query(orgQueryParam);

        for (String chargeId : chargeIds) {
            List<ExamOrg> examOrgs = new ArrayList<>();
            for (ExamOrg examOrg : examOrgList) {
                if (!CollectionUtils.isEmpty(examOrg.getChargeIds()) && examOrg.getChargeIds().indexOf(chargeId) > -1) {
                    examOrgs.add(examOrg);
                }
            }
            chargeExamOrgMap.put(chargeId, examOrgs);
        }

        return chargeExamOrgMap;
    }

    @Override
    public String getLargestNum() {

        // findDistinct() 去重查询
        List<String> orgNums = mongoTemplate.findDistinct(new Query(), "num", ExamOrg.class, String.class);

        if (CollectionUtils.isEmpty(orgNums)) {
            return "001";
        }
        String num = orgNums.stream().max(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        }).get();

        return num;
    }

    @Override
    public List<ExamOrg> queryExam(OrgQueryParam orgQueryParam) {
        List<ExamOrg> examOrgList = mongoTemplate.find(new Query(), ExamOrg.class);
        //组装负责人和领队
        if (orgQueryParam.wantAccount()) {
            List<ExamOrg> examOrgDetail = getExamOrgDetail(examOrgList);
            return examOrgDetail;
        }
        return examOrgList;
    }

    private Update assembleExamOrgUpdate(ExamOrg examOrg) {

        Update update = new Update();

        if (examOrg.getAssociation() != null) {
            update.set("association", examOrg.getAssociation());
        }

        if (StringUtils.isNotBlank(examOrg.getName())) {
            update.set("name", examOrg.getName());
        }

        if (StringUtils.isNotBlank(examOrg.getAlias())) {
            update.set("alias", examOrg.getAlias());
        }

        if (StringUtils.isNotBlank(examOrg.getAreaCode())) {
            update.set("areaCode", examOrg.getAreaCode());
        }

        if (StringUtils.isNotBlank(examOrg.getAddress())) {
            update.set("address", examOrg.getAddress());
        }

        if (StringUtils.isNotBlank(examOrg.getNum())) {
            update.set("num", examOrg.getNum());
        }

        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");

        return update;
    }

    private Criteria getOrgQuery(OrgQueryParam orgQueryParam) {

        Criteria ca = new Criteria();

        if (StringUtils.isNotBlank(orgQueryParam.getLeadId())) {
            ca.and("leaderId").is(orgQueryParam.getLeadId());
        }

        if (!StringUtils.isEmpty(orgQueryParam.getKeyword())) {
            ca.and("name").regex(orgQueryParam.getKeyword());
        }
        if (!StringUtils.isEmpty(orgQueryParam.getNum())) {
            ca.and("num").regex(orgQueryParam.getNum());
        }

        if (orgQueryParam.getAssociation() != null) {
            ca.and("association").is(orgQueryParam.getAssociation());
        }

        if (!CollectionUtils.isEmpty(orgQueryParam.getOrgIds())) {
            List<ObjectId> objectIds = new ArrayList<>();
            for (String id : orgQueryParam.getOrgIds()) {
                objectIds.add(new ObjectId(id));
            }
            ca.and("_id").in(objectIds);
        }

        if (!CollectionUtils.isEmpty(orgQueryParam.getChargeIds())) {
            ca.and("chargeIds").in(orgQueryParam.getChargeIds());
        }

        getOrgQueryTime(orgQueryParam, ca);
        return ca;

    }

    // 构建根据年份筛选的查询条件
    private Criteria getOrgQueryTime(OrgQueryParam orgQueryParam, Criteria ca) {
        int year = orgQueryParam.getYear();

        if (year <= 2000) {
            return ca;
        }

        LocalDateTime start = DateUtil.getYearFirst(year);
        LocalDateTime end = DateUtil.getYearFirst(year + 1);

        ca.andOperator(ca.where("gmtCreated").gte(start), ca.where("gmtCreated").lte(end));

        return ca;
    }

    private List<ExamOrg> getExamOrgDetail(List<ExamOrg> examOrgs) {

        if (CollectionUtils.isEmpty(examOrgs)) {
            return examOrgs;
        }

        List<String> leaderIds = examOrgs.stream().map(ExamOrg::getLeaderId).collect(Collectors.toList());

        List<String> chargeIds = new ArrayList<>();

        for (ExamOrg examOrg : examOrgs) {
            if (!CollectionUtils.isEmpty(examOrg.getChargeIds())) {
                chargeIds.addAll(examOrg.getChargeIds());
            }
        }

        AccountQueryParam param = AccountQueryParam.create().setAccountIds(leaderIds);
        List<Account> leaderList = accountService.query(null, param);
        param.setAccountIds(chargeIds);
        List<Account> chargeList = accountService.query(null, param);

        for (ExamOrg examOrg : examOrgs) {
            for (Account account : leaderList) {
                if (StringUtils.equals(examOrg.getLeaderId(), account.getId())) {
                    examOrg.setLeaderAccount(simpleAccount(account));
                }
            }

            if (!CollectionUtils.isEmpty(examOrg.getChargeIds())) {
                List<Account> accounts = new ArrayList<>();
                for (Account account : chargeList) {
                    if (examOrg.getChargeIds().indexOf(account.getId()) > -1) {
                        accounts.add(simpleAccount(account));
                    }

                }
                examOrg.setChargeAccounts(accounts);
            }

        }

        return examOrgs;

    }

    private Account simpleAccount(Account account) {

        account.setEmail(null);
        account.setJobNumber(null);
        account.setJoinTime(null);
        account.setProfile(null);
        account.setGmtCreated(null);
        account.setGmtModified(null);
        User user = account.getUser();
        user.setDescription(null);
        user.setBirthday(null);
        user.setAgreementVersion(null);
        user.setStatus(null);
        user.setEmail(null);
        user.setGender(null);
        user.setNamespace(null);
        user.setAvatarUrl(null);
        user.setCompanyId(null);
        user.setGmtCreated(null);
        user.setGmtModified(null);
        user.setNamespace(null);
        account.setUser(user);
        return account;
    }
}
