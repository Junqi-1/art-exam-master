package com.youkeda.application.art.member.repository;

import com.youkeda.application.art.member.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String> {

    List<Account> findByUserId(String userId);

    Account findByOldId(String oldId);

}
