package com.youkeda.application.art.member.service.impl;

import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.member.repository.DepartmentAccountRepository;
import com.youkeda.application.art.member.repository.DepartmentRepository;
import com.youkeda.application.art.member.service.DepartmentAccountCountService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentAccountCountServiceImpl implements DepartmentAccountCountService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentAccountRepository departmentAccountRepository;

    @Override
    public void calculate(String departmentId) {

        ObjectId objectId = new ObjectId(departmentId);
        // 查询当前部门的员工总数
        long count = departmentAccountRepository.countByDepartmentId(objectId);
        // 查询当前部门的子部门
        List<Department> curDepartments = departmentRepository.findDepartmentByParentId(objectId);
        for (Department department : curDepartments) {
            // 与所有子部门中记录的的员工数累加
            count += department.getMemberNum();
        }
        // 更新员工数
        update(objectId, count);

        Department department = departmentRepository.findById(departmentId).orElse(null);
        List<String> parentIds = department.getParentIds();
        // 更新父部门的员工数
        for (int i = 0; i < parentIds.size(); i++) {
            String parentId = parentIds.get(i);
            ObjectId oid = new ObjectId(parentId);
            // 同样的，先查询父部门的员工总数
            long ctotal = departmentAccountRepository.countByDepartmentId(oid);
            List<Department> departments = departmentRepository.findDepartmentByParentId(oid);
            // 再累加父部门下面所有子部门的员工数
            for (Department department1 : departments) {
                ctotal = ctotal + department1.getMemberNum();
            }
            // 更新员工数
            update(oid, ctotal);
        }
    }

    private void update(ObjectId departmentId, long count) {
        Update update = new Update();
        update.set("memberNum", count);
        mongoTemplate.upsert(Query.query(Criteria.where("_id").is(departmentId)), update, Department.class);
    }
}
