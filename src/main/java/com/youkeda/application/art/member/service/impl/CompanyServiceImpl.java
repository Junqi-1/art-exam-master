package com.youkeda.application.art.member.service.impl;

import com.youkeda.application.art.member.param.CompanyQueryParam;
import com.youkeda.application.art.member.repository.CompanyRepository;
import com.youkeda.application.art.member.service.CompanyService;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.model.Company;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.youkeda.model.Company.DEFAULT;

@Component
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        boolean present = companyRepository.findById(DEFAULT.getId()).isPresent();
        if (!present) {
            companyRepository.save(DEFAULT);
        }
    }

    @Override
    public Company save(Company company) {

        if (StringUtils.isEmpty(company.getCode())) {
            String code = IDUtils.generateId();
            company.setCode(code);
        }

        company.setGmtModified(LocalDateTime.now());
        companyRepository.save(company);

        return company;
    }

    @Override
    public Company find(CompanyQueryParam param) {

        if (param != null) {
            if (StringUtils.isNotEmpty(param.getDomain())) {
                return companyRepository.findByDomain(param.getDomain()).orElse(null);
            }

            if (StringUtils.isNotEmpty(param.getCode())) {
                return companyRepository.findByCode(param.getCode()).orElse(null);
            }
        }
        //找不到企业就是默认企业
        return DEFAULT;
    }

    @Override
    public List<Company> queryByAccountId(String accountId) {

        if (StringUtils.isBlank(accountId)) {
            return new ArrayList<>();
        }

        Optional<List<Company>> companies = companyRepository.findByOwnerUserId(accountId);

        //return companies.orElseGet(ArrayList::new);
        return companies.orElse(new ArrayList());
    }

    @Override
    public Company getByDomain(String domainUrl) {

        if (StringUtils.isBlank(domainUrl)) {
            return new Company();
        }

        Optional<Company> company = companyRepository.findByDomain(domainUrl);

        return company.orElseGet(Company::new);

    }

    @Override
    public void delete(String companyId) {
        if (StringUtils.isEmpty(companyId)) {
            return;
        }
        Optional<Company> company = companyRepository.findById(companyId);

        if (!company.isPresent()) {
            return;
        }

        companyRepository.deleteById(companyId);
    }

    @Override
    public Company get(String companyId) {
        Company company = new Company();
        if (StringUtils.isBlank(companyId)) {
            return company;
        }
        //todo
        company = mongoTemplate.findById(new ObjectId(companyId), Company.class);

        if (company != null) {
            return company;
        }
        return company;
    }
}
