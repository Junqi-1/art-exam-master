package com.youkeda.application.art.role.service;

import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.role.model.Permission;
import com.youkeda.application.art.role.param.PagePermissionQueryParam;
import com.youkeda.application.art.role.param.PermissionQueryParam;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/22, 周一
 */
public interface PermissionService {

    /**
     * 添加或修改权限
     *
     * @param permission 权限
     * @return Permission
     */
    Permission save(Permission permission);

    /**
     * 删除权限
     *
     * @param permissionId 权限主键
     * @return Permission
     */
    Result delete(String permissionId);

    /**
     * 查询权限
     *
     * @param accountId 用户主键
     * @return List<Permission>
     */
    List<Permission> queryByAccountId(String accountId);

    /**
     * 查询权限
     *
     * @param param 权限查询参数
     * @return List<Permission>
     */
    List<Permission> query(PermissionQueryParam param);

    /**
     * 分页查询权限
     *
     * @param param 分页参数
     * @return Paging<Permission>
     */
    Paging<Permission> pageQuery(PagePermissionQueryParam param);

}
