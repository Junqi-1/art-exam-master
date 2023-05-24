package com.youkeda.application.art.member.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.AccountStatus;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.repository.AccountRepository;
import com.youkeda.application.art.member.repository.UserRepository;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.UserService;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.youkeda.model.Company.DEFAULT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@Component
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Account save(Account account) {
        ObjectId companyId = new ObjectId(account.getCompanyId());
        Query query = new Query();
        if (StringUtils.isNotEmpty(account.getId())) {
            Criteria criteria = new Criteria();
            criteria.andOperator(Criteria.where("_id").is(new ObjectId(account.getId())));
            query.addCriteria(criteria);
        } else {
            if (StringUtils.isNotEmpty(account.getAccountMobile())) {
                Criteria criteria = new Criteria();
                criteria.andOperator(Criteria.where("accountMobile").is(account.getAccountMobile()),
                                     Criteria.where("companyId").is(companyId));
                query.addCriteria(criteria);
            } else if (account.getUser() != null && account.getUser().getId() != null) {
                query.addCriteria(Criteria.where("userId").is(new ObjectId(account.getUser().getId())).and("companyId")
                                      .is(companyId));
            }
        }

        Update update = new Update();
        if (account.getAccountMobile() != null) {
            update.set("accountMobile", account.getAccountMobile());
        } else if (account.getUser() != null && StringUtils.isNotEmpty(account.getUser().getMobile())) {
            update.set("accountMobile", account.getUser().getMobile());
        }
        if (account.getEmail() != null) {
            update.set("email", account.getEmail());
        }

        if (StringUtils.isNotBlank(account.getJobNumber())) {
            update.set("jobNumber", account.getJobNumber());
        }
        if (account.getAccountName() != null) {
            update.set("accountName", account.getAccountName());
        } else if (account.getUser() != null && StringUtils.isNotEmpty(account.getUser().getName())) {
            update.set("accountName", account.getUser().getName());
        }
        if (account.getProfile() != null) {
            update.set("profile", account.getProfile());
        }
        if (account.getStatus() != null) {
            update.set("status", account.getStatus());
        }
        if (account.getCompanyId() != null) {
            update.setOnInsert("companyId", account.getCompanyId());
        }
        if (account.getJoinTime() != null) {
            update.set("joinTime", account.getJoinTime());
        } else {
            update.set("joinTime", LocalDateTime.now());
            account.setJoinTime(LocalDateTime.now());
        }
        update.set("gmtModified", LocalDateTime.now());
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        if (account.getUser() != null && account.getUser().getId() != null) {
            update.set("userId", new ObjectId(account.getUser().getId()));
        }
        UpdateResult upResult = mongoTemplate.upsert(query, update, Account.class);
        if (upResult.getUpsertedId() != null) {
            String id = upResult.getUpsertedId().asObjectId().getValue().toString();
            account.setId(id);
        } else {
            Account one = mongoTemplate.findOne(query, Account.class);
            account.setId(one.getId());
        }
        account.setGmtCreated(LocalDateTime.now());
        account.setGmtModified(LocalDateTime.now());
        return account;
    }

    @Override
    public void delete(String accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            return;
        }
        // 设置用户为离职员工
        account.get().setStatus(AccountStatus.separating);
        save(account.get());
    }

    private List<Account> getByUserId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts;
    }

    @Override
    public Paging<Account> query(AccountQueryParam param) {

        if (param == null) {
            param = new AccountQueryParam();
        }

        if (StringUtils.isNotEmpty(param.getUserId())) {
            List<Account> accounts = getByUserId(param.getUserId());
            Paging<Account> result = new Paging<>();
            if (accounts == null) {
                return result;
            }
            if (StringUtils.equals(param.getCompanyId(), "all")) {
                result.setData(accounts);
            } else {
                List<Account> selectAccounts = new ArrayList<>();
                for (Account account : accounts) {
                    if (StringUtils.isEmpty(param.getCompanyId()) && StringUtils.equals(DEFAULT.getId(),
                                                                                        account.getCompanyId())) {
                        selectAccounts.add(account);
                        break;
                    }
                    if (StringUtils.equals(param.getCompanyId(), account.getCompanyId())) {
                        selectAccounts.add(account);
                        break;
                    }
                }
                result.setData(selectAccounts);
            }
            return result;
        }

        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(lookup("users", "userId", "_id", "user"));
        operations.add(unwind("user", true));

        Criteria companyCriteria = new Criteria();
        //账户没有accountId的话，就需要限定company了
        if (StringUtils.isEmpty(param.getAccountId())) {
            companyCriteria = Criteria.where("companyId").is(new ObjectId(DEFAULT.getId()));
            if (StringUtils.isNotEmpty(param.getCompanyId())) {
                companyCriteria = Criteria.where("companyId").is(new ObjectId(param.getCompanyId()));
            }
        }
        Criteria statusCriteria = Criteria.where("status").ne(AccountStatus.separating);
        if (param.getStatus() != null) {
            statusCriteria = Criteria.where("status").is(param.getStatus());
        }

        Criteria query = initQueryMath(param);
        if (query == null) {
            return new Paging<>();
        }

        Criteria ca = new Criteria();
        operations.add(match(ca.andOperator(query, companyCriteria, statusCriteria)));
        Paging<Account> result = Paging.compute(mongoTemplate, Account.class, operations, param);

        operations.add(sort(Sort.by(Sort.Direction.DESC, "id")));
        operations.add(skip(Long.valueOf(param.getPagination() * param.getPageSize())));
        operations.add(limit(param.getPageSize()));

        List<Account> accounts = mongoTemplate.aggregate(Aggregation.newAggregation(operations), Account.class,
                                                         Account.class).getMappedResults();
        result.setData(accounts);
        return result;
    }

    @Override
    public List<Account> query(String companyId, AccountQueryParam param) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(lookup("users", "userId", "_id", "user"));
        operations.add(unwind("user", true));

        Criteria ca = null;
        if (!CollectionUtils.isEmpty(param.getAccountIds())) {

            List<String> accountIds = param.getAccountIds().stream().distinct().filter(Objects::nonNull).collect(
                Collectors.toList());
            List<ObjectId> objectIds = new ArrayList<>();
            accountIds.forEach(accountId -> objectIds.add(new ObjectId(accountId)));
            ca = Criteria.where("_id").in(objectIds);
            operations.add(match(ca));
        } else if (StringUtils.isNotBlank(param.getAccountId())) {
            ca = Criteria.where("_id").is(new ObjectId(param.getAccountId()));
            operations.add(match(ca));
        } else {
            if (StringUtils.isNotBlank(param.getMobile())) {
                ca = Criteria.where("accountMobile").is(param.getMobile());
                initCompanyCriteria(companyId, operations, ca);
            } else if (!CollectionUtils.isEmpty(param.getMobiles())) {
                ca = Criteria.where("accountMobile").in(param.getMobiles());
                initCompanyCriteria(companyId, operations, ca);
            } else if (!StringUtils.isEmpty(param.getUserName())) {
                ca = Criteria.where("user.userName").is(param.getUserName());
                initCompanyCriteria(companyId, operations, ca);
            } else if (StringUtils.isNotBlank(param.getName())) {
                if (companyId != null) {
                    ca = Criteria.where("companyId").is(new ObjectId(companyId));
                } else {
                    ca = new Criteria();
                }
                ca.orOperator(Criteria.where("accountName").regex(param.getName()));
                ca.and("status").ne(AccountStatus.separating);
                operations.add(match(ca));
                operations.add(sort(Sort.by("id").descending()));
                operations.add(limit(20));
            } else if (StringUtils.isNotBlank(param.getUserId())) {
                ca = Criteria.where("userId").is(new ObjectId(param.getUserId()));
                initCompanyCriteria(companyId, operations, ca);
            } else {
                return Collections.EMPTY_LIST;
            }
        }

        return mongoTemplate.aggregate(Aggregation.newAggregation(operations), Account.class, Account.class)
            .getMappedResults();
    }

    private void initCompanyCriteria(String companyId, List<AggregationOperation> operations, Criteria ca) {
        if (StringUtils.isNotBlank(companyId)) {
            ca.and("companyId").is(new ObjectId(companyId));
        }
        operations.add(match(ca));
    }

    private Criteria initQueryMath(AccountQueryParam param) {
        Criteria criteria = new Criteria();

        if (param.getAccountIds() != null && !param.getAccountIds().isEmpty()) {
            List<ObjectId> ids = new ArrayList<>();
            param.getAccountIds().forEach(id -> {
                ids.add(new ObjectId(id));
            });
            if (ids.isEmpty()) {
                return null;
            }
            criteria.and("_id").in(ids);
        } else if (StringUtils.isNotEmpty(param.getAccountId())) {
            String accountId = param.getAccountId();
            if (accountId.length() != 24) {
                return null;
            }
            criteria.and("_id").is(new ObjectId(accountId));
        } else if (StringUtils.isNotEmpty(param.getMobile())) {
            criteria.orOperator(Criteria.where("user.mobile").is(param.getMobile()),
                                Criteria.where("accountMobile").is(param.getMobile()));
        } else if (param.getMobiles() != null && !param.getMobiles().isEmpty()) {
            criteria.orOperator(Criteria.where("user.mobile").in(param.getMobiles()),
                                Criteria.where("accountMobile").in(param.getMobiles()));
        } else if (StringUtils.isNotEmpty(param.getName())) {
            //联文的逻辑
            criteria.orOperator(Criteria.where("accountName").regex(param.getName()),
                                Criteria.where("accountMobile").regex(param.getName()));
        }
        return criteria;
    }
}
