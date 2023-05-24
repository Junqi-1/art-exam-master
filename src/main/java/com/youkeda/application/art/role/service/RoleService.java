package com.youkeda.application.art.role.service;


import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/6, 周五
 */
public interface RoleService {

    /**
     * 添加或修改角色
     *
     * @param role 角色
     * @return Role
     */
    Role save(Role role);

    /**
     * 查询角色
     *
     * @param param 角色查询参数
     * @return List<Role>
     */
    List<Role> query(RoleQueryParam param);

    /**
     * 删除角色
     *
     * @param id 角色主键
     * @return Role
     */
    Role delete(String id);

    /**
     * 删除用户角色
     *
     * @param accountId 用户主键
     * @param roleId    角色主键
     * @return
     */
    Role removeRole(List<String> accountId, String roleId);

    /**
     * 根据角色查询用户
     *
     * @param roleQueryParam 角色查询参数
     * @return Paging<Account>
     */
    Paging<Account> queryByRoleId(RoleQueryParam roleQueryParam);

    /**
     * 根据角色查询用户
     *
     * @param roleId    角色
     * @param companyId 公司
     * @return List<Account>
     */
    List<Account> queryAllByRoleId(String roleId, String companyId);

    /**
     * 批量添加用户或者部门权限
     *
     * @param roleId        角色
     * @param accountIds    用户
     * @param departmentIds 部门
     * @return Role
     */
    Role batchAddRole(String roleId, List<String> accountIds, List<String> departmentIds);

    /**
     * 批量添加用户或者部门权限
     *
     * @param roleIds        角色
     * @param permissionId    权限
     * @return Role
     */
    boolean batchAddRolePermission(List<String> roleIds, String permissionId);

    /**
     * 根据角色查询部门
     *
     * @param roleId 角色主键
     * @return List<Department>
     */
    List<Department> queryDepartmentByRoleId(String roleId);

    /**
     * 批量更新
     * @param roles 角色
     * @return Boolean
     */
    Boolean batchUpdate(List<Role> roles);

    /**
     * 根据角色主键查询角色
     *
     * @param roleId 角色主键
     * @return Role
     */
    Role get(String roleId);

    /**
     * 删除角色权限
     * @param permissionId 主键
     * @param roleIds 角色
     * @return boolean
     */
    Boolean deletePermissions(String permissionId,List<String> roleIds);

}
