package com.youkeda.application.art.member.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.exception.UserPwdErrorException;
import com.youkeda.application.art.member.exception.UserPwdNeedChangeException;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.model.User;
import com.youkeda.application.art.member.param.AccountQueryParam;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.UserLoginService;
import com.youkeda.application.art.member.service.UserService;
import com.youkeda.application.art.member.util.MemberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/api/member")
public class LoginAPI {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    /**
     * 处理用户密码登录
     */
    @PostMapping(path = "loginwithusername")
    @ResponseBody
    public Result<String> doLoginWithUserName(@RequestBody @Valid User user, BindingResult errors,
                                              HttpServletRequest request) {
        Result<String> result = new Result<>();
        // 如果有错误，返回验证失败
        if (errors.hasErrors()) {
            result.setCode("403");
            result.setMessage(errors.getAllErrors().get(0).getDefaultMessage());
            return result;
        }

        boolean needChangepwd = false;
        User dbUser = null;
        try {
            dbUser = userLoginService.loginWithPwd(user);
        } catch (UserPwdErrorException e) {
            result.setCode("403");
            result.setMessage("密码错误");
            return result;
        } catch (UserPwdNeedChangeException e) {
            needChangepwd = true;
            dbUser = e.getUser();
        }
        if (dbUser == null) {
            result.setCode("401");
            result.setMessage("用户名错误");
            return result;
        }

        // 密码过于简单，但可以登录
        if (needChangepwd) {
            result.setCode("505");
            result.setMessage("密码需要修改");
        }

        String companyId = user.getCompanyId();
        AccountQueryParam param = new AccountQueryParam();
        param.setUserId(dbUser.getId());
        List<Account> accounts = accountService.query(companyId, param);

        if (CollectionUtils.isEmpty(accounts)) {
            result.setCode("500");
            result.setMessage("未知错误，请联系管理员。");
            return result;
        }

        Account account = accounts.get(0);

        // 校验是否可用
        if (account.isBan()) {
            result.setCode("403");
            result.setMessage("账户已经被封禁");
            return result;
        }

        LoginUser loginUser = LoginUser.create().setAccountId(account.getId()).setUserId(account.getUserId())
            .setCompanyId(account.getCompanyId());

        // 登录信息写入 session
        request.getSession().setAttribute(MemberUtil.LOGIN_KEY, loginUser);
        result.setSuccess(true);
        return result;
    }

    @PostMapping(path = "changepwd")
    @NeedLogin
    @ResponseBody
    public Result<String> doChangepwd(@RequestBody User user, BindingResult errors, HttpServletRequest request,
                                      @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {
        Result<String> result = new Result<>();
        try {
            userService.changePwd(loginUser.getUserId(), user.getPwd());
        } catch (UserPwdErrorException e) {
            result.setCode("500");
            result.setMessage("密码不匹配规则，必须包含字母和数字，长度 6-18 个字符");
            return result;
        }
        result.setSuccess(true);
        return result;
    }
}
