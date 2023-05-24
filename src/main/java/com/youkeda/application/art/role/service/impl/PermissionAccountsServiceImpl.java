package com.youkeda.application.art.role.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.role.model.PermissionAccount;
import com.youkeda.application.art.role.param.PagePermissionAccountParam;
import com.youkeda.application.art.role.param.PermissionAccountsParam;
import com.youkeda.application.art.role.param.PermissionBatchAddParam;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * PermissionAccountsServiceImpl
 *
 * @author zr
 * @date 2020/6/22, 周一
 */
@Service
public class PermissionAccountsServiceImpl implements PermissionAccountsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PermissionAccount save(PermissionAccount permissionAccount) {

        if (permissionAccount == null) {
            return null;
        }

        Update update = new Update();
        if (StringUtils.isNotBlank(permissionAccount.getPermissionId())) {
            update.set("permissionId", permissionAccount.getPermissionId());
        }

        if (StringUtils.isNotBlank(permissionAccount.getCompanyId())) {
            update.set("companyId", permissionAccount.getCompanyId());
        }

        if (StringUtils.isNotBlank(permissionAccount.getAccountId())) {
            update.set("accountId", permissionAccount.getAccountId());
        }

        update.currentDate("gmtModified");
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        permissionAccount.setGmtModified(LocalDateTime.now());
        if (StringUtils.isEmpty(permissionAccount.getId())) {
            permissionAccount.setId(IDUtils.getId());
        }

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(new ObjectId(permissionAccount.getId()))), update,
                             PermissionAccount.class);
        return permissionAccount;
    }

    @Override
    public List<PermissionAccount> query(PermissionAccountsParam permissionAccountsParam) {

        if (permissionAccountsParam == null) {
            permissionAccountsParam = new PermissionAccountsParam();
        }

        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(permissionAccountsParam.getAccountId())) {
            criteria.and("accountId").is(permissionAccountsParam.getAccountId());
        }

        if (StringUtils.isNotBlank(permissionAccountsParam.getPermissionId())) {
            criteria.and("permissionId").is(permissionAccountsParam.getPermissionId());
        }

        Sort.Direction sort = Sort.Direction.DESC;

        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(sort(Sort.by(sort, "gmtCreated")));
        operations.add(match(criteria));

        Aggregation aggregation = newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, PermissionAccount.class, PermissionAccount.class)
            .getMappedResults();
    }

    @Override
    public Boolean delete(String permissionId, List<String> accountIds) {

        if (StringUtils.isBlank(permissionId) || CollectionUtils.isEmpty(accountIds)) {
            return false;
        }

        Criteria criteria = new Criteria();
        criteria.and("permissionId").is(permissionId);
        criteria.and("accountId").in(accountIds);

        DeleteResult remove = mongoTemplate.remove(new Query(criteria), PermissionAccount.class);
        return remove.wasAcknowledged();

    }

    @Override
    public Paging<PermissionAccount> pageQuery(PagePermissionAccountParam param) {

        List<AggregationOperation> operations = new ArrayList<>();

        Criteria ca = new Criteria();

        if (StringUtils.isNotBlank(param.getPermissionId())) {
            ca.and("permissionId").is(param.getPermissionId());
        }

        operations.add(match(ca));
        Paging<PermissionAccount> result = Paging.compute(mongoTemplate, PermissionAccount.class, operations, param);

        operations.add(sort(Sort.by(Sort.Direction.DESC, "id")));
        operations.add(skip(param.getPagination() * param.getPageSize()));
        operations.add(limit(param.getPageSize()));

        List<PermissionAccount> mappedResults = mongoTemplate.aggregate(newAggregation(operations),
                                                                        PermissionAccount.class,
                                                                        PermissionAccount.class).getMappedResults();
        result.setData(mappedResults);
        return result;
    }

    @Override
    public Boolean batchAdd(PermissionBatchAddParam param) {

        if (!CollectionUtils.isEmpty(param.getAccountIds())) {
            if (StringUtils.isBlank(param.getPermissionId()) || StringUtils.isBlank(param.getCompanyId())) {
                return false;
            }

            List<PermissionAccount> permissionAccounts = new ArrayList<>();

            // 对重复添加权限的人去重
            PermissionAccountsParam permissionAccountsParam = new PermissionAccountsParam();
            permissionAccountsParam.setPermissionId(param.getPermissionId());
            List<PermissionAccount> permissionAccountList = query(permissionAccountsParam);
            List<String> accountIds = permissionAccountList.stream().map(PermissionAccount::getAccountId).collect(
                Collectors.toList());

            for (String accountId : param.getAccountIds()) {
                PermissionAccount permissionAccount = new PermissionAccount();
                if (!CollectionUtils.isEmpty(accountIds) && accountIds.indexOf(accountId) > -1) {
                    continue;
                }
                permissionAccount.setAccountId(accountId);
                permissionAccount.setCompanyId(param.getCompanyId());
                permissionAccount.setPermissionId(param.getPermissionId());
                permissionAccount.setGmtCreated(LocalDateTime.now());
                permissionAccount.setGmtModified(LocalDateTime.now());
                permissionAccounts.add(permissionAccount);
            }
            return mongoTemplate.insert(permissionAccounts, PermissionAccount.class).size() > 0;

        } else if (!CollectionUtils.isEmpty(param.getPermissionIds())) {
            if (StringUtils.isBlank(param.getAccountId()) || StringUtils.isBlank(param.getCompanyId())) {
                return false;
            }
            List<PermissionAccount> permissionAccountList = new ArrayList<>();

            PermissionAccountsParam accountsParam = new PermissionAccountsParam();
            accountsParam.setAccountId(param.getAccountId());
            List<PermissionAccount> permissionAccounts = query(accountsParam);

            if (!CollectionUtils.isEmpty(permissionAccounts)) {
                List<String> permissionIds = permissionAccounts.stream().map(PermissionAccount::getPermissionId)
                    .collect(Collectors.toList());

                for (String permissionId : param.getPermissionIds()) {
                    PermissionAccount permissionAccount = new PermissionAccount();
                    if (!CollectionUtils.isEmpty(permissionIds) && permissionIds.indexOf(permissionId) > -1) {
                        continue;
                    }

                    permissionAccount.setPermissionId(permissionId);
                    permissionAccount.setCompanyId(param.getCompanyId());
                    permissionAccount.setAccountId(param.getAccountId());
                    permissionAccount.setGmtCreated(LocalDateTime.now());
                    permissionAccount.setGmtModified(LocalDateTime.now());
                    permissionAccounts.add(permissionAccount);
                }

            }
            return mongoTemplate.insert(permissionAccounts, PermissionAccount.class).size() > 0;

        }

        return false;
    }
}
