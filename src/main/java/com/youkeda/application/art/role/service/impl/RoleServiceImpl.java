package com.youkeda.application.art.role.service.impl;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.result.UpdateResult;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.DepartmentService;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.member.util.TreeUtil;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.BatchUpdateRoleOptions;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.RoleService;
import com.youkeda.model.Company;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
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
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/6, 周五
 */
@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @PostConstruct
    void initCourseIds() {
        log.info("RoleServiceImpl init complete.");
    }

    @Override
    public Role save(Role role) {
        if (role == null) {
            return null;
        }

        Update update = getRoleUpdate(role);

        if (StringUtils.isEmpty(role.getId())) {
            role.setId(IDUtils.getId());
        }

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(new ObjectId(role.getId()))), update, Role.class);
        return role;
    }

    @Override
    public List<Role> query(RoleQueryParam param) {
        if (param == null) {
            return new ArrayList<>();
        }

        Criteria ca = new Criteria();

        if (StringUtils.isNotBlank(param.getCompanyId())) {
            ca.and("companyId").is(param.getCompanyId());
        }

        if (!CollectionUtils.isEmpty(param.getRoleIds())) {
            List<ObjectId> objectIds = new ArrayList<>();

            for (String permissionId : param.getRoleIds()) {
                objectIds.add(new ObjectId(permissionId));
            }

            ca.and("_id").in(objectIds);
        }

        if (StringUtils.isNotBlank(param.getAccountId())) {
            ca.and("accountIds").is(param.getAccountId());
        }

        if (StringUtils.isNotBlank(param.getPermissionId())) {
            ca.and("permissionIds").is(param.getPermissionId());
        }

        Sort.Direction sort = Sort.Direction.DESC;

        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(sort(Sort.by(sort, "gmtCreated")));
        operations.add(match(ca));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, Role.class, Role.class).getMappedResults();

    }

    @Override
    public Role delete(String id) {

        if (StringUtils.isBlank(id)) {
            return new Role();
        }

        Role role = get(id);
        if (!CollectionUtils.isEmpty(role.getAccountIds())) {
            return null;
        }

        return mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(new ObjectId(id))), Role.class);

    }

    @Override
    public Role removeRole(List<String> accountIds, String roleId) {
        if (CollectionUtils.isEmpty(accountIds) || StringUtils.isBlank(roleId)) {
            return new Role();
        }

        Role role = get(roleId);
        if (role == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(role.getAccountIds())) {
            return null;
        }

        role.getAccountIds().removeAll(accountIds);
        Update update = new Update();
        update.set("accountIds", role.getAccountIds());
        UpdateResult updateResult = mongoTemplate.upsert(
            new Query(Criteria.where("_id").is(new ObjectId(role.getId()))), update, Role.class);

        if (updateResult.getModifiedCount() > 0) {
            return role;
        }
        return new Role();

    }

    @Override
    public Paging<Account> queryByRoleId(RoleQueryParam roleQueryParam) {

        Paging<Account> accountPaging = new Paging<>();
        if (StringUtils.isBlank(roleQueryParam.getRoleId())) {
            return accountPaging;
        }

        Role role = get(roleQueryParam.getRoleId());
        List<String> accountIds = role.getAccountIds();
        if (CollectionUtils.isEmpty(accountIds)) {
            accountPaging.setPageSize(roleQueryParam.getPageSize());
            accountPaging.setTotalCount(0);
            accountPaging.setPagination(roleQueryParam.getPagination());
            return accountPaging;
        }

        List<String> pageAccountIds = accountIds.stream().skip(roleQueryParam.getPagination() * roleQueryParam.getPageSize()).limit(
            roleQueryParam.getPageSize()).collect(Collectors.toList());

        List<String> pageAccountIds2 = new ArrayList<>();
        for (int i = (roleQueryParam.getPagination() * roleQueryParam.getPageSize());
               i < (roleQueryParam.getPagination() * roleQueryParam.getPageSize() + roleQueryParam.getPageSize());
               i++) {
            pageAccountIds2.add(accountIds.get(i));
        }

        if (CollectionUtils.isEmpty(pageAccountIds)) {
            return accountPaging;
        }
        int size = accountIds.size();
        int totalPage = 0;
        totalPage = (size / roleQueryParam.getPageSize()) == 0 ? size / roleQueryParam.getPageSize() :
            size / roleQueryParam.getPageSize() + 1;

        AccountQueryParam param = new AccountQueryParam();
        param.setPageSize(roleQueryParam.getPageSize());
        param.setAccountIds(pageAccountIds);
        param.setCompanyId(Company.DEFAULT.getId());
        Paging<Account> paging = accountService.query(param);
        paging.setTotalCount(size);
        paging.setPagination(roleQueryParam.getPagination());
        paging.setTotalPage(totalPage);
        paging.setPageSize(roleQueryParam.getPageSize());

        return paging;
    }

    @Override
    public List<Account> queryAllByRoleId(String roleId, String companyId) {

        if (StringUtils.isBlank(roleId)) {
            return new ArrayList<>();
        }

        List<String> accountIds = get(roleId).getAccountIds();
        AccountQueryParam param = new AccountQueryParam();
        param.setAccountIds(accountIds);

        return accountService.query(companyId, param);

    }

    @Override
    public Role batchAddRole(String roleId, List<String> accountIds, List<String> departmentIds) {

        if (StringUtils.isBlank(roleId) || (CollectionUtils.isEmpty(accountIds) && CollectionUtils.isEmpty(
            departmentIds))) {
            return new Role();
        }

        Role role = get(roleId);
        if (role == null) {
            return new Role();
        }
        List<String> roleAccountIds = role.getAccountIds();
        List<String> roleDepartmentIds = role.getDepartmentIds();
        if (CollectionUtils.isEmpty(roleAccountIds)) {
            roleAccountIds = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(roleDepartmentIds)) {
            roleDepartmentIds = new ArrayList<>();
        }

        for (String accountId : accountIds) {
            if (!roleAccountIds.contains(accountId)) {
                roleAccountIds.add(accountId);
            }
        }

        for (String departmentId : departmentIds) {
            if (!roleDepartmentIds.contains(departmentId)) {
                roleDepartmentIds.add(departmentId);
            }
        }

        role.setAccountIds(roleAccountIds);

        return save(role);
    }

    @Override
    public boolean batchAddRolePermission(List<String> roleIds, String permissionId) {

        if (StringUtils.isBlank(permissionId) || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        RoleQueryParam roleQueryParam = new RoleQueryParam();
        roleQueryParam.setRoleIds(roleIds);
        List<Role> roles = query(roleQueryParam);

        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        for (Role role : roles) {
            List<String> permissionIds = role.getPermissionIds();
            if (!CollectionUtils.isEmpty(permissionIds) && permissionIds.indexOf(permissionId) < 0) {
                permissionIds.add(0, permissionId);
                role.setPermissionIds(permissionIds);
            } else {
                role.setPermissionIds(Lists.newArrayList(permissionId));
            }
        }
        return batchUpdate(roles);
    }

    @Override
    public List<Department> queryDepartmentByRoleId(String roleId) {

        if (StringUtils.isBlank(roleId)) {
            return new ArrayList<>();
        }

        Role role = get(roleId);

        if (role == null || CollectionUtils.isEmpty(role.getDepartmentIds())) {
            return new ArrayList<>();
        }

        return departmentService.getByIds(role.getDepartmentIds());

    }

    @Override
    public Boolean batchUpdate(List<Role> roles) {

        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        List<BatchUpdateRoleOptions> options = new ArrayList<>();
        for (Role role : roles) {
            BatchUpdateRoleOptions option = new BatchUpdateRoleOptions();
            option.setUpdate(getRoleUpdate(role));
            option.setQuery(new Query(Criteria.where("_id").is(new ObjectId(role.getId()))));
            options.add(option);
        }
        int num = doBatchUpdate(mongoTemplate, "roles", options, true);
        return num > 0;
    }

    @Override
    public Role get(String roleId) {

        if (StringUtils.isBlank(roleId)) {
            return new Role();
        }

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(roleId))), Role.class);

    }

    @Override
    public Boolean deletePermissions(String permissionId, List<String> roleIds) {

        if (StringUtils.isBlank(permissionId) || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        RoleQueryParam param = new RoleQueryParam();
        param.setRoleIds(roleIds);
        List<Role> roles = query(param);

        for (Role role : roles) {
            List<String> permissionIds = role.getPermissionIds();

            if (!CollectionUtils.isEmpty(permissionIds)) {
                permissionIds.remove(permissionId);
            }

            role.setPermissionIds(permissionIds);
        }

        List<BatchUpdateRoleOptions> options = new ArrayList<>();

        for (Role role : roles) {
            BatchUpdateRoleOptions option = new BatchUpdateRoleOptions();
            Update update = new Update();
            update.set("permissionIds", role.getPermissionIds());
            option.setUpdate(update);
            option.setQuery(new Query(Criteria.where("_id").is(new ObjectId(role.getId()))));
            options.add(option);
        }
        int num = doBatchUpdate(mongoTemplate, "roles", options, true);
        return num > 0;
    }

    private int doBatchUpdate(MongoTemplate mongoTemplate, String collName, List<BatchUpdateRoleOptions> options,
                              boolean ordered) {
        DBObject command = new BasicDBObject();
        command.put("update", collName);
        List<BasicDBObject> updateList = new ArrayList<BasicDBObject>();
        for (BatchUpdateRoleOptions option : options) {
            BasicDBObject update = new BasicDBObject();
            update.put("q", option.getQuery().getQueryObject());
            update.put("u", option.getUpdate().getUpdateObject());
            update.put("upsert", option.isUpsert());
            update.put("multi", option.isMulti());
            updateList.add(update);
        }
        command.put("updates", updateList);
        command.put("ordered", ordered);
        Document document = mongoTemplate.getDb().runCommand((Bson)command);
        Object n = document.get("n");
        if (n.getClass() == Integer.class) {
            return (Integer)n;
        }
        return Integer.parseInt(String.valueOf(n));
    }

    private Update getRoleUpdate(Role role) {

        Update update = new Update();

        if (StringUtils.isNotBlank(role.getCompanyId())) {
            update.set("companyId", role.getCompanyId());
        }

        if (StringUtils.isNotBlank(role.getName())) {
            update.set("name", role.getName());
        }

        if (StringUtils.isNotBlank(role.getRemark())) {
            update.set("remark", role.getRemark());
        }

        if (StringUtils.isNotBlank(role.getTitle())) {
            update.set("title", role.getTitle());
        }

        if (!CollectionUtils.isEmpty(role.getAccountIds())) {
            update.set("accountIds", role.getAccountIds());
        }

        if (!CollectionUtils.isEmpty(role.getPermissionIds())) {
            update.set("permissionIds", role.getPermissionIds());
        }

        if (role.getRoleType() != null) {
            update.set("roleType", role.getRoleType());
        }

        update.currentDate("gmtModified");
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        return update;
    }

}
