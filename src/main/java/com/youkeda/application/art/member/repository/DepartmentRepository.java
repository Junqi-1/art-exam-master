package com.youkeda.application.art.member.repository;

import com.youkeda.application.art.member.model.Department;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DepartmentRepository extends MongoRepository<Department, String> {

    List<Department> findDepartmentByCompanyId(ObjectId companyId);

    List<Department> findDepartmentByParentId(ObjectId parentId);

    @CountQuery("{parentId:?0}")
    long countByParentId(ObjectId departmentId);

}
