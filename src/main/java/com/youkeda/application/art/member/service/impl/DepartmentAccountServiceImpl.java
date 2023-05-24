package com.youkeda.application.art.member.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.exception.UserNameInUseException;
import com.youkeda.application.art.member.model.*;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.param.DepartmentAccountParam;
import com.youkeda.application.art.member.repository.DepartmentAccountRepository;
import com.youkeda.application.art.member.repository.DepartmentRepository;
import com.youkeda.application.art.member.service.*;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class DepartmentAccountServiceImpl implements DepartmentAccountService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentAccountRepository departmentAccountRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentAccountCountService countService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public Paging<Account> queryAccounts(DepartmentAccountParam param) {
        List<AggregationOperation> operations = new ArrayList<>();
        Paging<DepartmentAccount> paging = getDepartmentMemberPager(param, operations);

        if (paging.getTotalCount() == 0) {
            return null;
        }

        operations.add(sort(Sort.by("id").descending()));
        operations.add(skip((long)param.getPagination() * param.getPageSize()));
        operations.add(limit(param.getPageSize()));

        List<DepartmentAccount> departmentAccountS = mongoTemplate.aggregate(Aggregation.newAggregation(operations),
                                                                             DepartmentAccount.class,
                                                                             DepartmentAccount.class)
            .getMappedResults();
        List<String> accountIds = new ArrayList<>();
        for (DepartmentAccount departmentAccount : departmentAccountS) {
            accountIds.add(departmentAccount.getAccountId());
        }
        List<Account> accounts = accountService.query(null, AccountQueryParam.create().setAccountIds(accountIds));

        List<Account> sortAccounts = new ArrayList<>();
        accountIds.forEach(accountId -> sortAccounts.add(accounts.stream().filter(
            account -> StringUtils.equals(accountId, account.getId())).findFirst().orElse(null)));

        Paging<Account> result = new Paging<>();
        result.setData(sortAccounts);
        result.setPageSize(paging.getPageSize());
        result.setPagination(paging.getPagination());
        result.setTotalCount(paging.getTotalCount());
        result.setTotalPage(paging.getTotalPage());
        return result;
    }

    private Paging<DepartmentAccount> getDepartmentMemberPager(DepartmentAccountParam param,
                                                               List<AggregationOperation> operations) {
        Criteria ca = new Criteria("departmentId");
        ca.is(new ObjectId(param.getDepartmentId())).and("accountId").exists(true);
        operations.add(match(ca));

        return Paging.<DepartmentAccount>compute(mongoTemplate, DepartmentAccount.class, operations, param);
    }

    @Override
    public void addAccount(String departmentId, Account account) throws ErrorException {
        Assert.notNull(account.getCompanyId(), "account.companyId not null");
        // 如果在顶级部门里添加用户，先初始化
        account.setStatus(AccountStatus.enabled);
        //新用户
        if (StringUtils.isEmpty(account.getId())) {
            checkAddAccount(account);
        } else {
            checkUserName(account.getUser());
            checkMobile(account);
            userService.update(account.getUser());
        }

        accountService.save(account);

        DepartmentAccount departmentAccount = mongoTemplate.findOne(Query.query(Criteria.where("accountId").is(
                                                                        new ObjectId(account.getId())).and("departmentId").is(new ObjectId(departmentId))),
                                                                    DepartmentAccount.class);
        if (departmentAccount == null) {
            departmentAccount = new DepartmentAccount();
            departmentAccount.setAccountId(account.getId());
            departmentAccount.setDepartmentId(departmentId);
            departmentAccount.setGmtCreated(LocalDateTime.now());
            departmentAccount.setGmtModified(LocalDateTime.now());
            departmentAccountRepository.save(departmentAccount);
            countService.calculate(departmentId);
        }

    }

    private void checkAddAccount(Account account) throws ErrorException {
        //如果有用户名，则进行注册用户，并设置默认密码
        if (account.getUser() != null && StringUtils.isNotBlank(account.getUser().getUserName())) {
            if (StringUtils.isBlank( account.getUser().getPwd())) {
                account.getUser().setPwd("aaaaaa");
            }

            if (StringUtils.isNotBlank(account.getAccountMobile())) {
                account.getUser().setMobile(account.getAccountMobile());
            }
            try {
                User user = userService.reg(account.getUser());
                account.setUser(user);
            } catch (UserNameInUseException e) {
                List<Account> accounts = accountService.query(account.getCompanyId(), AccountQueryParam.create()
                    .setUserId(e.getUser().getId()));
                if (CollectionUtils.isEmpty(accounts)) {
                    throw new ErrorException("70003", "the userName:" + account.getUser().getUserName() + " exist");
                } else {
                    account.setId(accounts.get(0).getId());
                }
                account.getUser().setId(e.getUser().getId());
            }

        } else if (StringUtils.isNotBlank(account.getAccountMobile())) {
            // 如果有手机号码，则查找账户
            checkMobile(account);
            // 自动关联用户
            User user = userService.getWithMobile(account.getAccountMobile());
            if (user != null) {
                account.setUser(user);
            } else {
                user = new User();
                user.setMobile(account.getAccountMobile());
                user.setName(account.getAccountName());
                User updateUser = userService.update(user);
                account.setUser(updateUser);
            }
        }
    }

    private void checkUserName(User user) throws ErrorException {
        if (user == null || StringUtils.isEmpty(user.getUserName())) {
            return;
        }

        User u = userService.getWithUserName(user.getUserName());
        if (u != null && !StringUtils.equals(u.getId(), user.getId())) {
            //如果账户相同说明执行的是update，可以不处理
            throw new ErrorException("70003", "the userName:" + user.getUserName() + " exist");
        }

        if (StringUtils.isNotBlank(user.getId())) {
            u = userService.getWithUserId(user.getId());
            if (u != null && !StringUtils.equals(u.getUserName(), user.getUserName())) {
                throw new ErrorException("70005", "the userName can't update");
            }
        }

    }

    private void checkMobile(Account account) throws ErrorException {
        if (StringUtils.isEmpty(account.getAccountMobile())) {
            return;
        }
        List<Account> accounts = accountService.query(account.getCompanyId(),
                                                      AccountQueryParam.create().setMobile(account.getAccountMobile()));
        if (!CollectionUtils.isEmpty(accounts) && !StringUtils.equals(accounts.get(0).getId(), account.getId())) {
            //如果账户相同说明执行的是update，可以不处理
            throw new ErrorException("70004", "the accountMobile:" + account.getAccountMobile() + " exist");
        }
    }

    @Override
    public void deleteAccounts(String departmentId, List<String> accountIds) {
        Department department = departmentRepository.findById(departmentId).orElse(null);
        if (department == null) {
            return;
        }

        List<ObjectId> accountObjIds = new ArrayList<>();
        accountIds.forEach(accountId -> {
            ObjectId objectId = new ObjectId(accountId);
            accountObjIds.add(objectId);
            //删除账户
            accountService.delete(accountId);
        });

        MongoCollection<Document> collection = mongoTemplate.getCollection(DepartmentAccount.C_NAME);
        collection.deleteMany(Filters.in("accountId", accountObjIds));
        countService.calculate(departmentId);
    }

    @Override
    public void adjustAccountDepartment(String originDepartmentId, String destDepartmentId,
                                        List<String> accountIdList) {

        List<ObjectId> acIds = new ArrayList<>();
        accountIdList.forEach(accountId -> acIds.add(new ObjectId(accountId)));

        mongoTemplate.getCollection(DepartmentAccount.C_NAME).find(Filters.in("accountId", acIds)).forEach(
            (Consumer<? super Document>)document -> {
                String departmentId = document.getObjectId("departmentId").toString();
                if (StringUtils.equals(originDepartmentId, departmentId)) {
                    DepartmentAccount departmentAccount = new DepartmentAccount();
                    departmentAccount.setId(document.getObjectId("_id").toString());
                    departmentAccount.setAccountId(document.getObjectId("accountId").toString());
                    departmentAccount.setDepartmentId(destDepartmentId);
                    departmentAccount.setGmtModified(LocalDateTime.now());
                    Instant instant = document.getDate("gmtCreated").toInstant();
                    LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    departmentAccount.setGmtCreated(localDateTime);
                    departmentAccountRepository.save(departmentAccount);
                }
            });
        //计算新部门人数
        countService.calculate(destDepartmentId);
        //计算老部门人数
        countService.calculate(originDepartmentId);

    }

    @Override
    public List<Account> queryAccountsByKeyword(String departmentId, String keyword, String companyId) {

        List<Account> accountList = new ArrayList<>();
        if (StringUtils.isBlank(departmentId) || StringUtils.isBlank(keyword)) {
            return accountList;
        }

        List<Account> accounts = accountService.query(companyId, AccountQueryParam.create().setName(keyword));

        if (CollectionUtils.isEmpty(accounts)) {
            return accountList;
        }

        Criteria ca = new Criteria("departmentId");
        ca.is(new ObjectId(departmentId)).and("accountId").exists(true);

        List<DepartmentAccount> departmentAccounts = mongoTemplate.find(new Query(ca), DepartmentAccount.class);
        if (CollectionUtils.isEmpty(departmentAccounts)) {
            return accountList;
        }

        List<String> accountIdList = departmentAccounts.stream().map(DepartmentAccount::getAccountId).collect(
            Collectors.toList());

        for (Account account : accounts) {
            if (accountIdList.contains(account.getId())) {
                accountList.add(account);
            }
        }
        return accountList;
    }

    @Override
    public boolean accountExistOnDepartment(String accountId, String departmentId) {

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(departmentId)) {
            return false;
        }

        Criteria ca = new Criteria();
        ca.and("accountId").is(new ObjectId(accountId));
        ca.and("departmentId").is(new ObjectId(departmentId));
        return mongoTemplate.exists(new Query(ca), DepartmentAccount.class);
    }

    @Override
    public List<Department> getByAccountId(String accountId) {
        List<Department> departments = new ArrayList<>();
        if (StringUtils.isBlank(accountId)) {
            return departments;
        }

        List<DepartmentAccount> departmentAccounts = mongoTemplate.find(
            new Query(Criteria.where("accountId").is(new ObjectId(accountId))), DepartmentAccount.class);

        if (CollectionUtils.isEmpty(departmentAccounts)) {
            return departments;
        }

        List<String> departmentIds = departmentAccounts.stream().map(DepartmentAccount::getDepartmentId).collect(
            Collectors.toList());

        return departmentService.getByIds(departmentIds);
    }
}
