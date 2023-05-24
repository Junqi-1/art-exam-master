package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.exception.UserPwdErrorException;
import com.youkeda.application.art.member.exception.UserPwdNeedChangeException;
import com.youkeda.application.art.member.model.User;

public interface UserLoginService {
    User loginWithPwd(User user) throws UserPwdErrorException, UserPwdNeedChangeException;
}
