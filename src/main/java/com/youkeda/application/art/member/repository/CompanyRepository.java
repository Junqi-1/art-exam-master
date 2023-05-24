package com.youkeda.application.art.member.repository;

import com.youkeda.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByCode(String code);

    Optional<Company> findByDomain(String domain);

    /**
     * 根据accountId查询公司信息
     *
     * @param accountId 用户主键
     * @return Optional<Company>
     */
    Optional<List<Company>> findByOwnerUserId(String accountId);

}
