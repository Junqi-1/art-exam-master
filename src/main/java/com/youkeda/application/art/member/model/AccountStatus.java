package com.youkeda.application.art.member.model;

/**
 * 账户状态
 */
public enum AccountStatus {
    /**
     * 创建的账户
     */
    created,

    /**
     * 激活过的账户（就是用户登录过）
     */
    enabled,

    /**
     * 离职员工
     */
    separating,
    /**
     * 正式员工
     */
    regular,
    /**
     * 兼职员工
     */
    partial,
    /**
     * 实习员工
     */
    intern

}
