package com.youkeda.application.art.member.exception;

import com.youkeda.application.art.member.model.User;

/**
 * 用户名已经存在
 */
public class UserNameInUseException extends Exception {

    private User user;

    public UserNameInUseException(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
