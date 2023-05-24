package com.youkeda.application.art.role.api;

import com.google.common.collect.Lists;
import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.model.PermissionAccount;
import com.youkeda.application.art.role.param.PagePermissionAccountParam;
import com.youkeda.application.art.role.param.PermissionBatchAddParam;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/24, 周三
 */
@NeedLogin
@Controller
@RequestMapping("/api/permissionaccount")
public class PermissionAccountApi {

    @Autowired
    private PermissionAccountsService permissionAccountsService;

    @GetMapping("/pagequery")
    @ResponseBody
    public Result<Paging<PermissionAccount>> pageQuery(PagePermissionAccountParam param) {

        Result<Paging<PermissionAccount>> result = new Result<>();
        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(permissionAccountsService.pageQuery(param));
        result.setSuccess(true);
        return result;
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<PermissionAccount> add(@RequestBody PermissionAccount permissionAccount) {

        Result<PermissionAccount> result = new Result<>();
        if (permissionAccount == null) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(permissionAccountsService.save(permissionAccount));
        result.setSuccess(true);
        return result;
    }

    @PostMapping("/batchadd")
    @ResponseBody
    public Result batchAdd(@RequestBody PermissionBatchAddParam param,
                           @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result result = new Result<>();
        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (StringUtils.isBlank(param.getCompanyId())) {
            param.setCompanyId(loginUser.getCompanyId());
        }

        result.setSuccess(permissionAccountsService.batchAdd(param));
        return result;

    }

    @PostMapping("/remove/permissions")
    @ResponseBody
    public Result removePermissions(@RequestParam String permissionId, @RequestParam List<String> accountIds) {

        Result result = new Result<>();
        if (StringUtils.isBlank(permissionId) || CollectionUtils.isEmpty(accountIds)) {
            return result;
        }

        result.setSuccess(permissionAccountsService.delete(permissionId, accountIds));
        return result;
    }

    @PostMapping("/remove/permission")
    @ResponseBody
    public Result removePermission(@RequestParam String permissionId, @RequestParam String accountId) {

        Result result = new Result<>();
        if (StringUtils.isBlank(permissionId) || StringUtils.isBlank(accountId)) {
            return result;
        }

        result.setSuccess(permissionAccountsService.delete(permissionId, Lists.newArrayList(accountId)));
        return result;
    }

}
