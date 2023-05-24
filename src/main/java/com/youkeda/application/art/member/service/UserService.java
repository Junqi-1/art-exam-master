package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.exception.UserNameInUseException;
import com.youkeda.application.art.member.exception.UserPwdErrorException;
import com.youkeda.application.art.member.model.User;

public interface UserService {
    /**
     * 获取用户
     *
     * @param userId
     * @return
     */
    User getWithUserId(String userId);

    /**
     * 获取用户
     *
     * @param userName
     * @return
     */
    User getWithUserName(String userName);

    /**
     * 获取用户
     *
     * @param mobile
     * @return
     */
    User getWithMobile(String mobile);

    /**
     * 生成密码
     *
     * @param slot
     * @param pwd
     * @return
     */
    String getMd5Pwd(String slot, String pwd);

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    User update(User user);

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    User reg(User user) throws UserNameInUseException;

    /**
     * 修改密码
     *
     * @param userId
     * @param newPwd
     * @return
     */
    void changePwd(String userId, String newPwd) throws UserPwdErrorException;

    /**
     * 重置密码
     *
     * @param userId
     * @param newPwd
     */
    void restPwd(String userId, String newPwd);

}
