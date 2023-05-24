package com.youkeda.application.art.member.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.member.model.Tree;
import com.youkeda.application.art.member.repository.DepartmentAccountRepository;
import com.youkeda.application.art.member.repository.DepartmentRepository;
import com.youkeda.application.art.member.service.*;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.application.art.member.util.TreeUtil;
import com.youkeda.model.Company;
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
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class DepartmentServiceImpl implements DepartmentService {

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

    @Override
    public Department save(Department department) {
        // 先初始化一个默认的根部门
        initCompanyDefaultDepartment(department.getCompanyId());
        String sourceParentId = null;
        //新增部门
        if (StringUtils.isEmpty(department.getId())) {
            department.setId(IDUtils.getId());
            // 新增的部门实际上是默认根部门的子部门
            if (StringUtils.isBlank(department.getParentId())) {
                department.setParentId(department.getCompanyId());
            }
        } else {
            sourceParentId = departmentRepository.findById(department.getId()).orElse(null).getParentId();
        }
        Update update = new Update();
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.set("gmtModified", LocalDateTime.now());
        update.setOnInsert("companyId", department.getCompanyId());
        if (StringUtils.isNotBlank(department.getName())) {
            update.set("name", department.getName());
        }
        if (StringUtils.isNotBlank(department.getDesc())) {
            update.set("desc", department.getDesc());
        }
        if (StringUtils.isNotBlank(department.getParentId())) {
            update.set("parentId", department.getParentId());
            department.setParentIds(initParentIds(department.getParentId()));
            update.set("parentIds", department.getParentIds());
        }
        if (StringUtils.isNotBlank(department.getManagerAccountId())) {
            update.set("managerAccountId", new ObjectId(department.getManagerAccountId()));
        }
        if (department.getSort() != 0) {
            update.set("sort", department.getSort());
        }
        UpdateResult upResult = mongoTemplate.upsert(
            Query.query(Criteria.where("_id").is(new ObjectId(department.getId()))), update, Department.class);
        if (upResult.getUpsertedId() != null) {
            String id = upResult.getUpsertedId().asObjectId().getValue().toString();
            department.setId(id);
        } else {
            //说明父部门发生调整
            if (StringUtils.isNotBlank(department.getParentId()) && !StringUtils.equals(sourceParentId,
                                                                                        department.getParentId())) {
                countService.calculate(sourceParentId);
                countService.calculate(department.getParentId());
            }
        }
        return department;
    }

    // 以公司 id 作为部门的主键 id，保存一个默认的部门作为树形的根
    private void initCompanyDefaultDepartment(String companyId) {
        if (departmentRepository.findById(companyId).isEmpty()) {
            Department defaultDepartment = new Department(companyId);
            defaultDepartment.setGmtCreated(LocalDateTime.now());
            defaultDepartment.setGmtModified(LocalDateTime.now());
            departmentRepository.save(defaultDepartment);
        }
    }

    private List<String> initParentIds(String departmentParentId) {
        List<String> ids = new ArrayList<>();
        ids.add(departmentParentId);
        departmentRepository.findById(departmentParentId).ifPresent(parent -> ids.addAll(parent.getParentIds()));
        return ids;
    }

    @Override
    public void delete(String departmentId) throws ErrorException {

        ObjectId objectId = new ObjectId(departmentId);
        long count = departmentAccountRepository.countByDepartmentId(objectId);
        if (count > 0) {
            throw new ErrorException("70010", "departmentId:" + departmentId + " has members");
        }

        long count1 = departmentRepository.countByParentId(objectId);
        if (count1 > 0) {
            throw new ErrorException("70011", "departmentId:" + departmentId + " has children");
        }

        departmentRepository.deleteById(departmentId);
    }

    @Override
    public Department getAllWithTree(String companyId) {
        Assert.notNull(companyId, "companyId not null");
        Company company = companyService.get(companyId);
        String name = company.getName();
        if (StringUtils.isNotBlank(company.getShortName())) {
            name = company.getShortName();
        }
        List<Department> departmentList = departmentRepository.findDepartmentByCompanyId(new ObjectId(companyId));
        //如果没有记录则提供一个默认的顶级部门
        if (CollectionUtils.isEmpty(departmentList)) {
            initCompanyDefaultDepartment(companyId);
            Department department = departmentRepository.findById(companyId).orElse(null);
            department.setName(company.getName());
            return department;
        }
        Consumer<List<Department>> sort = (items) -> {
            items.sort((o1, o2) -> o1.getSort() - o2.getSort());
        };
        Department parent = new Department("0");

        TreeUtil.convertToTree(departmentList.toArray(new Tree[] {}), parent, null, sort);
        Department department = parent.getChildren().get(0);
        department.setName(name);

        return department;
    }

    @Override
    public List<Department> getByIds(List<String> departmentIds) {
        List<ObjectId> ids = new ArrayList<>();
        departmentIds.forEach(departmentId -> ids.add(new ObjectId(departmentId)));
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(ids)), Department.class);
    }

    @Override
    public List<Department> queryByUserInfo(String companyId, String keywords) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(lookup("accounts", "accountId", "_id", "account"));
        operations.add(unwind("account", true));

        operations.add(sort(Sort.by(Sort.Direction.DESC, "id")));
        Criteria criteria3 = Criteria.where("companyId").is(new ObjectId(companyId));
        Criteria ca = new Criteria();
        operations.add(match(ca.andOperator(criteria3)));
        if (!StringUtils.isEmpty(keywords)) {
            Criteria criteria1 = Criteria.where("account.accountMobile").regex(keywords);
            Criteria criteria2 = Criteria.where("account.accountName").regex(keywords);
            Criteria criteria4 = Criteria.where("name").regex(keywords);
            operations.add(match(ca.orOperator(criteria1, criteria2, criteria4)));
        }
        return mongoTemplate.aggregate(Aggregation.newAggregation(operations), Department.class, Department.class)
            .getMappedResults();
    }

}

