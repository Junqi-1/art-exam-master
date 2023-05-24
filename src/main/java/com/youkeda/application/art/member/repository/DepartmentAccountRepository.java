package com.youkeda.application.art.member.repository;

import com.youkeda.application.art.member.model.DepartmentAccount;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentAccountRepository extends MongoRepository<DepartmentAccount, String> {

    @CountQuery("{departmentId:?0}")
    long countByDepartmentId(ObjectId departmentId);
}
