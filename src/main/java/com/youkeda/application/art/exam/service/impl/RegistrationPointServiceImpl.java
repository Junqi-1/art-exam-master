package com.youkeda.application.art.exam.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.youkeda.application.art.exam.model.ExamOrg;
import com.youkeda.application.art.exam.model.RegistrationPoint;
import com.youkeda.application.art.exam.param.OrgQueryParam;
import com.youkeda.application.art.exam.param.RegistrationPointPagingParam;
import com.youkeda.application.art.exam.service.ExamOrgService;
import com.youkeda.application.art.exam.service.RegistrationPointService;
import com.youkeda.application.art.exam.util.LWConstant;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @Author songchuanming
 * @DATE 2021/4/14.
 */
@Service
public class RegistrationPointServiceImpl implements RegistrationPointService {

    private static Logger logger = LoggerFactory.getLogger(RegistrationPointServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExamOrgService examOrgService;

    @Autowired
    private AccountService accountService;

    @PostConstruct
    private void initData() {
        logger.info("RegistrationPointServiceImpl init successful.");
    }

    @Override
    public RegistrationPoint save(RegistrationPoint registrationPoint) {
        if (registrationPoint == null) {
            return new RegistrationPoint();
        }
        Update update = new Update();
        if (StringUtils.isNotBlank(registrationPoint.getName())) {
            update.set("name", registrationPoint.getName());
        }
        if (StringUtils.isNotBlank(registrationPoint.getAddress())) {
            update.set("address", registrationPoint.getAddress());
        }
        if (StringUtils.isNotBlank(registrationPoint.getChargeId())) {
            update.set("chargeId", registrationPoint.getChargeId());
        }
        if (StringUtils.isNotBlank(registrationPoint.getAliaName())) {
            update.set("aliaName", registrationPoint.getAliaName());
        }
        if (StringUtils.isNotBlank(registrationPoint.getExamOrgId())) {
            update.set("examOrgId", registrationPoint.getExamOrgId());
        }
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");
        if (StringUtils.isEmpty(registrationPoint.getId())) {
            registrationPoint.setId(IDUtils.getId());
        }
        mongoTemplate.upsert(Query.query(Criteria.where("_id").is(new ObjectId(registrationPoint.getId()))), update,
                             RegistrationPoint.class);
        return registrationPoint;
    }

    @Override
    public Paging<RegistrationPoint> pageQuery(RegistrationPointPagingParam pagingParam) {
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria ca = new Criteria();
        if (StringUtils.isNotBlank(pagingParam.getOrgId()) && !pagingParam.getOrgId().equals("index")) {
            ca.and("examOrgId").is(pagingParam.getOrgId());
        }
        if (StringUtils.isNotBlank(pagingParam.getKeyword())) {
            ca.orOperator(Criteria.where("name").regex(pagingParam.getKeyword().trim()),
                          Criteria.where("aliaName").regex(pagingParam.getKeyword().trim()));
        }
        if (StringUtils.isNotBlank(pagingParam.getAccountId())) {
            ca.and("chargeId").is(pagingParam.getAccountId());
        }

        operations.add(match(ca));
        Paging<RegistrationPoint> result = Paging.compute(mongoTemplate, RegistrationPoint.class, operations,
                                                          pagingParam);
        operations.add(sort(Sort.by(Sort.Direction.DESC, "gmtCreated")));
        operations.add(skip((long)(pagingParam.getPagination() * pagingParam.getPageSize())));
        operations.add(limit(pagingParam.getPageSize()));
        // 分页查询所有的报名点
        List<RegistrationPoint> registrationPoints = mongoTemplate.aggregate(Aggregation.newAggregation(operations),
                                                                             RegistrationPoint.class,
                                                                             RegistrationPoint.class)
            .getMappedResults();

        List<String> orgIds = new ArrayList<>();
        List<String> orgAccountIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(registrationPoints)) {
            // 收集所有的考级点 id
            orgIds = registrationPoints.stream().filter(registrationPoint -> {
                return StringUtils.isNotBlank(registrationPoint.getExamOrgId());
            }).map(RegistrationPoint::getExamOrgId).distinct().collect(Collectors.toList());

            // 收集所有的负责人 id
            orgAccountIds = registrationPoints.stream().filter(registrationPoint -> {
                return StringUtils.isNotBlank(registrationPoint.getChargeId());
            }).map(RegistrationPoint::getChargeId).distinct().collect(Collectors.toList());
        }

        // 如果需要返回负责人account对象
        if (!CollectionUtils.isEmpty(orgAccountIds)) {
            AccountQueryParam accountQueryParam = new AccountQueryParam();
            accountQueryParam.setAccountIds(orgAccountIds);
            // 查查询，这里指定了特定的公司
            List<Account> queryAccounts = accountService.query(LWConstant.LWCOMPANYID, accountQueryParam);
            if (!CollectionUtils.isEmpty(queryAccounts)) {
                // 转换成以 account id 作为 KEY 的 map
                Map<String, Account> accountMap = queryAccounts.stream().collect(
                    Collectors.toMap(Account::getId, t -> t));

                // 历报名点，取出对应的account
                registrationPoints.forEach((registrationPoint -> {
                    if (accountMap.get(registrationPoint.getChargeId()) != null) {
                        Account account = accountMap.get(registrationPoint.getChargeId());
                        registrationPoint.setChargeAccount(account);
                    }
                }));
            }
        }

        // 如果需要返回考级点对象，则先根据收集的考级点 id 查询
        if (pagingParam.isNeedOrg()) {
            OrgQueryParam orgQueryParam = new OrgQueryParam();
            orgQueryParam.setOrgIds(orgIds);
            List<ExamOrg> examOrgList = examOrgService.query(orgQueryParam);
            if (!CollectionUtils.isEmpty(examOrgList)) {
                // 转换成以考级点 id 作为 KEY 的 map
                Map<String, ExamOrg> examOrgMap = examOrgList.stream().collect(
                    Collectors.toMap(ExamOrg::getId, t -> t));

                // 遍历报名点，取出对应的考级点对象
                registrationPoints.forEach((registrationPoint -> {
                    if (examOrgMap.get(registrationPoint.getExamOrgId()) != null) {
                        ExamOrg examOrg = examOrgMap.get(registrationPoint.getExamOrgId());
                        registrationPoint.setExamOrg(examOrg);
                    }
                }));
            }
        }

        result.setData(registrationPoints);
        return result;
    }

    @Override
    public List<RegistrationPoint> queryplus(RegistrationPointPagingParam pagingParam) {
        if (pagingParam == null) {
            pagingParam = new RegistrationPointPagingParam();
        }
        Criteria criteria1 = new Criteria();
        if (StringUtils.isNotBlank(pagingParam.getOrgId())) {
            criteria1 = Criteria.where("examOrgId").is(pagingParam.getOrgId());
        }
        Query query = new Query(criteria1);
        query.limit(30);
        List<RegistrationPoint> res = mongoTemplate.find(query, RegistrationPoint.class);
        return res;
    }

    @Override
    public RegistrationPoint get(String registrationPointId) {
        if (StringUtils.isBlank(registrationPointId)) {
            return null;
        }
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(registrationPointId))),
                                     RegistrationPoint.class);
    }

    @Override
    public boolean delete(String registrationPointId) {
        Criteria ca = new Criteria().andOperator(Criteria.where("_id").in(new ObjectId(registrationPointId)));
        DeleteResult remove = mongoTemplate.remove(new Query(ca), RegistrationPoint.class);
        if (remove.wasAcknowledged()) {
            return true;
        }
        return false;
    }

}
