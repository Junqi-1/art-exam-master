package com.youkeda.application.art.role.service;

import com.youkeda.application.art.role.model.PermissionAccount;
import com.youkeda.application.art.role.param.PagePermissionAccountParam;
import com.youkeda.application.art.role.param.PermissionAccountsParam;
import com.youkeda.application.art.role.param.PermissionBatchAddParam;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/22, 周一
 */
public interface PermissionAccountsService {

    /**
     * 添加或者修改权限用户关系
     *
     * @param permissionAccount 权限用户
     * @return PermissionAccount
     */
    PermissionAccount save(PermissionAccount permissionAccount);

    /**
     * 权限和用户查询
     *
     * @param permissionAccountsParam 权限用户查询参数
     * @return List<PermissionAccount>
     */
    List<PermissionAccount> query(PermissionAccountsParam permissionAccountsParam);

    /**
     * 删除权限和人的关联关系
     *
     * @param permissionId 权限主键
     * @param accountIds   用户主键
     * @return Boolean
     */
    Boolean delete(String permissionId, List<String> accountIds);

    /**
     * 分页查询
     *
     * @param pagePermissionAccountParam 分页参数
     * @return Paging<PermissionAccount>
     */
    Paging<PermissionAccount> pageQuery(PagePermissionAccountParam pagePermissionAccountParam);

    /**
     * 批量插入
     *
     * @param param 批量添加参数
     * @return Boolean
     */
    Boolean batchAdd(PermissionBatchAddParam param);
}
