package com.youkeda.application.art.role.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.model.Permission;
import com.youkeda.application.art.role.param.PagePermissionQueryParam;
import com.youkeda.application.art.role.param.PermissionQueryParam;
import com.youkeda.application.art.role.service.PermissionAccountsService;
import com.youkeda.application.art.role.service.PermissionService;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PermissionApi
 *
 * @author zr
 * @date 2020/6/22, 周一
 */
@NeedLogin
@Controller
@RequestMapping("/api/permission")
public class PermissionApi {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionAccountsService permissionAccountsService;

    @PostMapping("/save")
    @ResponseBody
    public Result<Permission> save(@RequestBody Permission permission) {

        Result<Permission> result = new Result<>();
        if (permission == null) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(permissionService.save(permission));
        result.setSuccess(true);
        return result;

    }

    @PostMapping("/delete")
    @ResponseBody
    public Result delete(@RequestParam String permissionId) {

        Result<Permission> result = new Result<>();
        if (StringUtils.isBlank(permissionId)) {
            result.setMessage("缺少参数");
            return result;
        }

        return permissionService.delete(permissionId);

    }

    @GetMapping("/query/accountid")
    @ResponseBody
    public Result<List<Permission>> queryByAccountId(@RequestParam String accountId) {

        Result<List<Permission>> result = new Result<>();
        if (StringUtils.isBlank(accountId)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(permissionService.queryByAccountId(accountId));
        result.setSuccess(true);
        return result;

    }

    @GetMapping("/query")
    @ResponseBody
    public Result<List<Permission>> query(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                          PermissionQueryParam param) {

        Result<List<Permission>> result = new Result<>();
        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (StringUtils.isBlank(param.getCompanyId())) {
            param.setCompanyId(loginUser.getCompanyId());
        }

        result.setData(permissionService.query(param));
        result.setSuccess(true);
        return result;
    }

    @GetMapping("/pagequery")
    @ResponseBody
    public Result<Paging<Permission>> pageQuery(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                                PagePermissionQueryParam param) {

        Result<Paging<Permission>> result = new Result<>();
        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (StringUtils.isBlank(param.getCompanyId())) {
            param.setCompanyId(loginUser.getCompanyId());
        }

        result.setData(permissionService.pageQuery(param));
        result.setSuccess(true);
        return result;
    }

}
