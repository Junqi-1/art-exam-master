package com.youkeda.application.art.role.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.param.RoleQueryParam;
import com.youkeda.application.art.role.service.RoleService;
import com.youkeda.model.Company;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/6, 周五
 */
@NeedLogin
@Controller
@RequestMapping(path = "/api/role")
public class RoleApi {

    @Autowired
    private RoleService roleService;

    @GetMapping(value = "/query/accountid")
    @ResponseBody
    Result<List<Role>> queryByAccount(@RequestParam("accountId") String accountId,
                                      @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<Role>> result = new Result<>();
        if (StringUtils.isBlank(accountId)) {
            result.setMessage("缺少参数");
            return result;
        }

        RoleQueryParam roleParam = setRoleParamCompanyId(loginUser);
        roleParam.setAccountId(accountId);

        result.setData(roleService.query(roleParam));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/permissionid")
    @ResponseBody
    Result<List<Role>> queryByPermissionId(@RequestParam("permissionId") String permissionId) {

        Result<List<Role>> result = new Result<>();
        if (StringUtils.isBlank(permissionId)) {
            result.setMessage("缺少参数");
            return result;
        }

        RoleQueryParam param = new RoleQueryParam();
        param.setPermissionId(permissionId);

        result.setData(roleService.query(param));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/all")
    @ResponseBody
    Result<List<Role>> queryAllRoles(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<Role>> result = new Result<>();
        RoleQueryParam roleParam = setRoleParamCompanyId(loginUser);

        result.setData(roleService.query(roleParam));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/roleid")
    @ResponseBody
    Result<Paging<Account>> queryByRoleId(RoleQueryParam roleQueryParam,
                                          @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<Paging<Account>> result = new Result<>();
        if (roleQueryParam == null) {
            result.setMessage("缺少参数");
            return result;
        }

        roleQueryParam.setCompanyId(loginUser.getCompanyId());
        result.setData(roleService.queryByRoleId(roleQueryParam));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/queryall/roleid")
    @ResponseBody
    Result<List<Account>> queryAllByRoleId(@RequestParam("roleId") String roleId,
                                           @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<Account>> result = new Result<>();
        if (StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.queryAllByRoleId(roleId, loginUser.getCompanyId()));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/querydepartment/roleid")
    @ResponseBody
    Result<List<Department>> queryDepartments(@RequestParam("roleId") String roleId) {

        Result<List<Department>> result = new Result<>();
        if (StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.queryDepartmentByRoleId(roleId));
        result.setSuccess(true);
        return result;

    }

    @PostMapping(value = "/add/accountrole")
    @ResponseBody
    Result<Role> batchAddAccountsRole(@RequestParam("accountIds") List<String> accountIds,
                                      @RequestParam("roleId") String roleId) {
        Result<Role> result = new Result<>();
        if (StringUtils.isBlank(roleId) || CollectionUtils.isEmpty(accountIds)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.batchAddRole(roleId, accountIds, new ArrayList<>()));
        return result;
    }

    @PostMapping(value = "/add/rolepermssion")
    @ResponseBody
    Result<Role> addRolesPermission(@RequestParam("roleIds") List<String> roleIds,
                                    @RequestParam("permissionId") String permissionId) {
        Result<Role> result = new Result<>();
        if (StringUtils.isBlank(permissionId) || CollectionUtils.isEmpty(roleIds)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setSuccess(roleService.batchAddRolePermission(roleIds, permissionId));
        return result;
    }

    @PostMapping(value = "/add/departmentrole")
    @ResponseBody
    Result<Role> batchAddDepartmentRole(@RequestParam("departments") List<String> departments,
                                        @RequestParam("roleId") String roleId) {
        Result<Role> result = new Result<>();
        if (CollectionUtils.isEmpty(departments) || StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        Role role = roleService.batchAddRole(roleId, new ArrayList<>(), departments);
        result.setData(role);
        return result;
    }

    @PostMapping(value = "/add")
    @ResponseBody
    Result<Role> add(@RequestBody Role role) {
        Result<Role> result = new Result<>();
        if (role == null) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.save(role));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/update")
    @ResponseBody
    Result<Role> update(@RequestBody Role role) {
        Result<Role> result = new Result<>();
        if (role == null || StringUtils.isBlank(role.getId())) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.save(role));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/remove/accountid")
    @ResponseBody
    Result<Role> removeRole(@RequestParam("roleId") String roleId,
                            @RequestParam("accountIds") List<String> accountIds) {
        Result<Role> result = new Result<>();
        if (CollectionUtils.isEmpty(accountIds) || StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.removeRole(accountIds, roleId));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    Result<Role> delete(@RequestParam("roleId") String roleId) {
        Result<Role> result = new Result<>();
        if (StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(roleService.delete(roleId));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/delete/permission")
    @ResponseBody
    Result<Role> deletePermissions(@RequestParam("permissionId") String permissionId,
                                   @RequestParam("roleIds") List<String> roleIds) {
        Result<Role> result = new Result<>();
        if (CollectionUtils.isEmpty(roleIds) || StringUtils.isBlank(permissionId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setSuccess(roleService.deletePermissions(permissionId, roleIds));
        return result;
    }

    @GetMapping(value = "/check/registrationpoint/role")
    @ResponseBody
    Result<Boolean> checkRole(@RequestParam("roleId") String roleId,
                              @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {
        Result<Boolean> result = new Result<>();
        if (StringUtils.isBlank(roleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        List<Account> accounts = roleService.queryAllByRoleId(roleId, loginUser.getCompanyId());
        if (CollectionUtils.isEmpty(accounts)) {
            result.setSuccess(true);
            result.setData(true);
            return result;
        } else {
            List<String> accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());
            result.setSuccess(true);
            if (!accountIds.contains(loginUser.getAccountId())) {
                result.setData(false);
                return result;
            } else {
                result.setData(true);
                return result;
            }
        }
    }

    @GetMapping(value = "/check/permission")
    @ResponseBody
    Result<Boolean> checkPermission(@RequestParam("permissionId") String permissionId,
                                    @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<Boolean> result = new Result<>();
        if (StringUtils.isBlank(permissionId)) {
            result.setMessage("缺少参数");
            return result;
        }

        RoleQueryParam param = new RoleQueryParam();
        param.setPermissionId(permissionId);
        param.setAccountId(loginUser.getAccountId());
        List<Role> roleList = roleService.query(param);
        if (CollectionUtils.isEmpty(roleList)) {
            result.setData(false);
        } else {
            result.setData(true);
        }
        result.setSuccess(true);
        return result;
    }

    private RoleQueryParam setRoleParamCompanyId(LoginUser loginUser) {
        RoleQueryParam param = new RoleQueryParam();
        String companyId = loginUser.getCompanyId();

        if (StringUtils.isBlank(companyId)) {
            companyId = Company.DEFAULT.getId();
        }

        param.setCompanyId(companyId);

        return param;
    }
}
