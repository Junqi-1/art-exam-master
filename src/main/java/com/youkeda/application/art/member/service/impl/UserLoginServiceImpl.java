package com.youkeda.application.art.member.service.impl;

import com.youkeda.application.art.member.exception.UserPwdErrorException;
import com.youkeda.application.art.member.exception.UserPwdNeedChangeException;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.repository.UserRepository;
import com.youkeda.application.art.member.service.UserLoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private static Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loginWithPwd(User user) throws UserPwdErrorException, UserPwdNeedChangeException {
        if (StringUtils.isEmpty(user.getPwd())) {
            return null;
        }

        User dbUser = null;
        // 优先使用用户名登录
        if (StringUtils.isNotEmpty(user.getUserName())) {
            dbUser = userRepository.findByUserName(user.getUserName());
        } else if ((StringUtils.isNotEmpty(user.getEmail()))) {
            // 然后用邮箱登录
            dbUser = userRepository.findByEmail(user.getEmail());
        } else if ((StringUtils.isNotEmpty(user.getMobile()))) {
            // 然后用手机号登录
            dbUser = userRepository.findByMobile(user.getMobile());
        }

        if (dbUser == null) {
            return null;
        }

        String md5Hex = DigestUtils.md5Hex(dbUser.getSlot() + "_" + user.getPwd()).toUpperCase();
        // 密码相同，登录成功
        if (StringUtils.equals(md5Hex, dbUser.getPwd())) {
            String defaultPwd = DigestUtils.md5Hex(dbUser.getSlot() + "_123456").toUpperCase();
            // 但是，密码太简单，需要修改
            if (StringUtils.equals(defaultPwd, md5Hex)) {
                throw new UserPwdNeedChangeException(dbUser);
            }
            return dbUser;
        }
        throw new UserPwdErrorException();
    }
}
