package com.youkeda.application.art.exam.api;

import com.google.common.collect.Lists;
import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.exam.model.RegistrationPoint;
import com.youkeda.application.art.exam.param.RegistrationPointPagingParam;
import com.youkeda.application.art.exam.service.RegistrationPointService;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.service.RoleService;
import com.youkeda.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author songchuanming
 * @DATE 2021/4/14.
 */
@NeedLogin
@Controller
@RequestMapping(value = "/api/registrationPoint")
public class RegistrationPointApi {

    private static Logger logger = LoggerFactory.getLogger(RegistrationPointApi.class);

    @Autowired
    private RegistrationPointService registrationPointService;

    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/save")
    @ResponseBody
    public Result<RegistrationPoint> add(@RequestBody RegistrationPoint registrationPoint,
                                         @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {
        Result<RegistrationPoint> result = new Result<>();

        //自动配置证书管理员角色信息
        roleService.batchAddRole("6062f57292929d86e65374bb", Lists.newArrayList(registrationPoint.getChargeId()),
                                 new ArrayList<>());
        RegistrationPoint saveRegistrationPoint = registrationPointService.save(registrationPoint);
        result.setSuccess(true);
        result.setData(saveRegistrationPoint);
        return result;
    }

    @GetMapping(value = "/querypaging")
    @ResponseBody
    public Result<Paging<RegistrationPoint>> querypaging(RegistrationPointPagingParam registrationPointPagingParam,
                                                         @SessionAttribute(value = MemberUtil.LOGIN_KEY)
                                                             LoginUser loginUser) {
        Result<Paging<RegistrationPoint>> result = new Result<>();
        result.setSuccess(true);
        if (loginUser == null) {
            return result;
        }

        String accountId = loginUser.getAccountId();
        //判断是不是系统管理员
        List<Account> adminAccounts = roleService.queryAllByRoleId("5e9136edf036142912d47725",
                                                                   loginUser.getCompanyId());
        if (!CollectionUtils.isEmpty(adminAccounts)) {
            List<String> accountIds = adminAccounts.stream().map(Account::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(accountIds) && accountIds.contains(accountId)) {
                // 当前登录用户是系统管理员角色，可以查询
                result.setData(registrationPointService.pageQuery(registrationPointPagingParam));
                return result;
            }
        }

        // 判断是不是报名点负责人
        List<Account> accounts = roleService.queryAllByRoleId("6062f57292929d86e65374bb", loginUser.getCompanyId());
        if (!CollectionUtils.isEmpty(accounts)) {
            List<String> accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(accountIds) && accountIds.contains(accountId)) {
                // 报名点负责人只能查询自己负责的报名点
                registrationPointPagingParam.setAccountId(accountId);
            }
        }

        result.setData(registrationPointService.pageQuery(registrationPointPagingParam));
        return result;
    }

    @GetMapping(value = "/querypagingplus")
    @ResponseBody
    public Result<List<RegistrationPoint>> queryPlus(RegistrationPointPagingParam registrationPointPagingParam) {
        Result<List<RegistrationPoint>> result = new Result<>();
        result.setSuccess(true);
        result.setData(registrationPointService.queryplus(registrationPointPagingParam));
        return result;
    }

    @GetMapping(value = "/delete")
    @ResponseBody
    public Result<Boolean> delete(@RequestParam("id") String id) {
        Result<Boolean> result = new Result<>();
        result.setData(registrationPointService.delete(id));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/get")
    @ResponseBody
    public Result<RegistrationPoint> get(@RequestParam("id") String id) {
        Result<RegistrationPoint> result = new Result<>();
        result.setData(registrationPointService.get(id));
        result.setSuccess(true);
        return result;
    }
}
