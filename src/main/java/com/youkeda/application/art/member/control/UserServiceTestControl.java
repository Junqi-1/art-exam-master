package com.youkeda.application.art.member.control;

import com.youkeda.application.art.member.exception.UserNameInUseException;
import com.youkeda.application.art.member.model.Gender;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.model.UserStatus;
import com.youkeda.application.art.member.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/user")
public class UserServiceTestControl {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTestControl.class);

    @Autowired
    private UserService userService;

    @GetMapping(path = "/reg")
    public User testRegUser() {
        User user = new User();
        user.setUserName("zhangsan");
        user.setMobile("13344444444");
        user.setPwd("12345");
        user.setAgreementVersion("1");
        user.setName("张三");
        user.setEmail("12345@qq.com");
        user.setGender(Gender.male);
        user.setStatus(UserStatus.enabled);
        user.setNickName("小张");
        try {
            user = userService.reg(user);
        } catch (UserNameInUseException e) {
            log.error("reg user error.", e);
        }

        return user;
    }
}
