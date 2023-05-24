package com.youkeda.application.art.role.service;

import com.youkeda.application.art.role.model.Role;

import java.util.List;

/**
 * @date 2020/6/22, 周一
 */
public interface AuthService {

    /**
     * 根据accountId和refId查询用户是否拥有权限
     *
     * @param accountId 用户id
     * @param refId     关联的refId
     * @return boolean
     */
    boolean authByRefId(String accountId, String refId);

    /**
     * 根据用户和权限查询用户是否有权限
     *
     * @param accountId    用户主键
     * @param permissionId 权限主键
     * @return boolean
     */
    boolean authByPermissionId(String accountId, String permissionId);

    /**
     * 查询用户权限
     *
     * @param accountId 用户主键
     * @param companyId 公司
     * @return List<Role>
     */
    List<Role> getRoles(String accountId, String companyId);

}
