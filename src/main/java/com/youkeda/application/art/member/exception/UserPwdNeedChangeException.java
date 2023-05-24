package com.youkeda.application.art.member.exception;

import com.youkeda.application.art.member.model.User;

/**
 * 密码需要修改
 *
 * @author joe
 * @date 2020-02-05
 */
public class UserPwdNeedChangeException extends Exception {

    private User user;

    public UserPwdNeedChangeException(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
