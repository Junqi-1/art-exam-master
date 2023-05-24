package com.youkeda.application.art.role.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.model.Role;
import com.youkeda.application.art.role.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/6/30, 周二
 */
@NeedLogin
@Controller
@RequestMapping("/api/auth")
public class AuthApi {

    @Autowired
    private AuthService authService;

    @PostMapping("/ref")
    @ResponseBody
    public Result authByRefId(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                              @RequestParam("refId") String refId) {

        Result result = new Result();

        if (StringUtils.isBlank(refId)) {
            return result;
        }

        result.setSuccess(authService.authByRefId(loginUser.getAccountId(), refId));
        return result;

    }

    @PostMapping("/permission")
    @ResponseBody
    public Result authByPermissionId(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                     @RequestParam("permissionId") String permissionId) {

        Result result = new Result();

        if (StringUtils.isBlank(permissionId)) {
            return result;
        }

        result.setSuccess(authService.authByPermissionId(loginUser.getAccountId(), permissionId));
        return result;

    }

    @PostMapping("/getroles")
    @ResponseBody
    public Result<List<Role>> getRoles(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<Role>> result = new Result<>();

        result.setData(authService.getRoles(loginUser.getAccountId(),loginUser.getCompanyId()));
        result.setSuccess(true);
        return result;

    }

}
