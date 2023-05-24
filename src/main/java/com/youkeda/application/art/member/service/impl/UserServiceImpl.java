package com.youkeda.application.art.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.youkeda.application.art.member.exception.UserNameInUseException;
import com.youkeda.application.art.member.exception.UserPwdErrorException;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.repository.UserRepository;
import com.youkeda.application.art.member.service.UserService;
import com.youkeda.application.art.member.util.IDUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author joe
 */
@Component
public class UserServiceImpl implements UserService {
    // 默认的盐值
    private static final String DEFAULT_SLOT = "ykd";

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User getWithUserId(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getWithUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User getWithMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public String getMd5Pwd(String slot, String pwd) {
        if (slot == null) {
            slot = DEFAULT_SLOT;
        }
        return DigestUtils.md5Hex(slot + "_" + pwd).toUpperCase();
    }

    @Override
    public User update(User user) {

        // 防止多条 user 有相同的手机号
        if (StringUtils.isEmpty(user.getId())) {
            if (user.getMobile() != null) {
                User user1 = userRepository.findByMobile(user.getMobile());
                if (user1 != null) {
                    user.setId(user1.getId());
                }
            }
            if (StringUtils.isEmpty(user.getId())) {
                user.setId(IDUtils.getId());
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(user.getId())));
        Update update = new Update();

        if (user.getAvatarUrl() != null) {
            update.set("avatarUrl", user.getAvatarUrl());
        }
        if (user.getMobile() != null) {
            update.set("mobile", user.getMobile());
        }
        if (user.getAgreementVersion() != null) {
            update.set("agreementVersion", user.getAgreementVersion());
        }
        if (user.getGender() != null) {
            update.set("gender", user.getGender());
        }
        if (user.getBirthday() != null) {
            update.set("birthday", user.getBirthday());
        }
        if (user.getDescription() != null) {
            update.set("description", user.getDescription());
        }
        if (user.getEmail() != null) {
            update.set("email", user.getEmail());
        }
        if (user.getName() != null) {
            update.set("name", user.getName());
        }
        if (user.getNickName() != null) {
            update.set("nickName", user.getNickName());
        }
        if (user.getUserName() != null) {
            update.set("userName", user.getUserName());
        }
        if (user.getStatus() != null) {
            update.set("status", user.getStatus());
        }
        update.set("gmtModified", LocalDateTime.now());
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        mongoTemplate.upsert(query, update, User.class);

        return user;
    }

    @Override
    public User reg(User user) throws UserNameInUseException {
        String userName = user.getUserName();
        userName = StringUtils.trim(userName);
        User dbUser = userRepository.findByUserName(userName);
        if (dbUser != null) {
            throw new UserNameInUseException(dbUser);
        }
        String slot = DEFAULT_SLOT;
        User newUser = new User();
        newUser.setId(IDUtils.getId());
        newUser.setUserName(userName);
        newUser.setMobile(user.getMobile());
        String md5Hex = getMd5Pwd(slot, user.getPwd());
        newUser.setPwd(md5Hex);
        newUser.setGmtCreated(LocalDateTime.now());
        newUser.setGmtModified(LocalDateTime.now());
        return userRepository.save(newUser);
    }

    @Override
    public void changePwd(String userId, String newPwd) throws UserPwdErrorException {

        // 6-18 位，必须包含字母和数字
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}$";

        if (!newPwd.matches(regex)) {
            throw new UserPwdErrorException();
        }
        restPwd(userId, newPwd);
    }

    @Override
    public void restPwd(String userId, String newPwd) {
        User dbUser = userRepository.findById(userId).orElse(null);
        Assert.notNull(dbUser, "userId:" + userId + " has no user");
        Update update = new Update();
        update.set("pwd", getMd5Pwd(dbUser.getSlot(), newPwd));
        update.set("gmtModified", LocalDateTime.now());
        mongoTemplate.upsert(Query.query(Criteria.where("_id").is(new ObjectId((userId)))), update, User.class);
    }

    public static void main(String[] args) {
        String a = null;
        List<String> datas = new ArrayList<>();
        datas.add(a);
        datas.forEach(id -> {
            System.out.println(new ObjectId(id));
        });
        System.out.println(datas.size());
        System.out.println(JSON.toJSON(datas));
    }
}
