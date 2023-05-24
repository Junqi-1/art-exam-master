package com.youkeda.application.art.role.service.impl;

import com.youkeda.application.art.role.model.PermissionAccount;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.PermissionAccountsParam;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.AuthService;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.application.art.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/30, 周二
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PermissionAccountsService permissionAccountsService;

    @Autowired
    private RoleService roleService;

    @Override
    public boolean authByRefId(String accountId, String refId) {

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(refId)) {
            return false;
        }

        PermissionAccountsParam param = new PermissionAccountsParam();
        param.setAccountId(accountId);
        param.setRefId(refId);
        return isAuth(param);
    }

    @Override
    public boolean authByPermissionId(String accountId, String permissionId) {

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(permissionId)) {
            return false;
        }

        PermissionAccountsParam param = new PermissionAccountsParam();
        param.setAccountId(accountId);
        param.setPermissionId(permissionId);
        return isAuth(param);
    }

    @Override
    public List<Role> getRoles(String accountId, String companyId) {

        List<Role> roles = new ArrayList<>();

        if (StringUtils.isBlank(accountId)) {
            return roles;
        }

        RoleQueryParam param = new RoleQueryParam();
        param.setAccountId(accountId);
        param.setCompanyId(companyId);
        return roleService.query(param);

    }

    private boolean isAuth(PermissionAccountsParam param) {

        List<PermissionAccount> permissionAccounts = permissionAccountsService.query(param);
        return !CollectionUtils.isEmpty(permissionAccounts);

    }
}
