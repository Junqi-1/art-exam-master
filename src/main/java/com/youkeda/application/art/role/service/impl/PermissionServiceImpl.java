package com.youkeda.application.art.role.service.impl;

import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.role.model.Permission;
import com.youkeda.application.art.role.model.PermissionAccount;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.PagePermissionQueryParam;
import com.youkeda.application.art.role.param.PermissionAccountsParam;
import com.youkeda.application.art.role.param.PermissionQueryParam;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.application.art.role.service.PermissionService;
import com.youkeda.application.art.role.service.RoleService;
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
 * TODO
 *
 * @author zr
 * @date 2020/6/22, 周一
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PermissionAccountsService permissionAccountsService;

    @Autowired
    private RoleService roleService;

    @Override
    public Permission save(Permission permission) {

        if (permission == null) {
            return null;
        }

        Update update = getPermissionUpdate(permission);

        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");
        permission.setGmtModified(LocalDateTime.now());
        if (StringUtils.isEmpty(permission.getId())) {
            permission.setId(IDUtils.getId());
        }

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(new ObjectId(permission.getId()))), update,
                             Permission.class);
        return permission;
    }

    @Override
    public Result delete(String permissionId) {
        Result result = new Result();
        if (StringUtils.isBlank(permissionId)) {
            return null;
        }

        //权限如果有用户绑定则不能删除
        PermissionAccountsParam permissionAccountsParam = new PermissionAccountsParam();
        permissionAccountsParam.setPermissionId(permissionId);
        List<PermissionAccount> permissionAccounts = permissionAccountsService.query(permissionAccountsParam);

        if (!CollectionUtils.isEmpty(permissionAccounts)) {
            result.setCode("5050");
            result.setMessage("accounts is existing");
            return result;
        }

        //权限如果有角色则不能删除
        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setPermissionId(permissionId);
        List<Role> roles = roleService.query(roleQueryParam);
        if (!CollectionUtils.isEmpty(roles)) {
            result.setCode("5060");
            result.setMessage("roles is existing");
            return result;
        }

        result.setSuccess(
            mongoTemplate.remove(new Query(Criteria.where("_id").is(new ObjectId(permissionId))), Permission.class)
                .wasAcknowledged());
        result.setCode("2000");
        return result;

    }

    @Override
    public List<Permission> queryByAccountId(String accountId) {

        if (StringUtils.isBlank(accountId)) {
            return null;
        }

        PermissionAccountsParam permissionAccountsParam = new PermissionAccountsParam();
        permissionAccountsParam.setAccountId(accountId);
        List<PermissionAccount> permissionAccounts = permissionAccountsService.query(permissionAccountsParam);

        if (CollectionUtils.isEmpty(permissionAccounts)) {
            return null;
        }

        List<String> permissionIds = permissionAccounts.stream().map(PermissionAccount::getPermissionId).collect(
            Collectors.toList());

        PermissionQueryParam param = new PermissionQueryParam();
        param.setPermissionIds(permissionIds);
        return query(param);
    }

    @Override
    public List<Permission> query(PermissionQueryParam param) {

        if (param == null) {
            param = new PermissionQueryParam();
        }

        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(param.getCompanyId())) {
            criteria.and("companyId").is(param.getCompanyId());
        }

        if (StringUtils.isNotBlank(param.getName())) {
            criteria.and("name").is(param.getName());
        }

        if (StringUtils.isNotBlank(param.getRefId())) {
            criteria.and("refId").is(param.getRefId());
        }

        List<String> permissionIds = param.getPermissionIds();
        if (!CollectionUtils.isEmpty(permissionIds)) {
            List<ObjectId> objectIds = new ArrayList<>();

            for (String permissionId : permissionIds) {
                objectIds.add(new ObjectId(permissionId));
            }

            criteria.and("_id").in(objectIds);
        }

        Sort.Direction sort = Sort.Direction.DESC;

        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(sort(Sort.by(sort, "gmtCreated")));
        operations.add(match(criteria));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, Permission.class, Permission.class).getMappedResults();
    }

    @Override
    public Paging<Permission> pageQuery(PagePermissionQueryParam param) {

        if (param == null) {
            param = new PagePermissionQueryParam();
        }

        List<AggregationOperation> operations = new ArrayList<>();

        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(param.getCompanyId())) {
            criteria.and("companyId").is(param.getCompanyId());
        }

        if (StringUtils.isNotBlank(param.getKeyword())) {
            criteria.orOperator(Criteria.where("name").regex(param.getKeyword()),
                                Criteria.where("title").regex(param.getKeyword()));
        }

        operations.add(match(criteria));
        Paging<Permission> result = Paging.compute(mongoTemplate, Permission.class, operations, param);
        operations.add(sort(Sort.by(Sort.Direction.DESC, "id")));
        operations.add(skip(param.getPagination() * param.getPageSize()));
        operations.add(limit(param.getPageSize()));

        List<Permission> mappedResults = mongoTemplate.aggregate(newAggregation(operations), Permission.class,
                                                                 Permission.class).getMappedResults();
        result.setData(mappedResults);
        return result;
    }

    private Update getPermissionUpdate(Permission permission) {

        Update update = new Update();

        if (StringUtils.isNotBlank(permission.getName())) {
            update.set("name", permission.getName());
        }

        if (StringUtils.isNotBlank(permission.getTitle())) {
            update.set("title", permission.getTitle());
        }

        if (StringUtils.isNotBlank(permission.getRemark())) {
            update.set("remark", permission.getRemark());
        }

        if (StringUtils.isNotBlank(permission.getRefId())) {
            update.set("refId", permission.getRefId());
        }

        if (StringUtils.isNotBlank(permission.getCompanyId())) {
            update.set("companyId", permission.getCompanyId());
        }

        return update;

    }

}
