package com.youkeda.application.art.member.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.model.*;
import com.youkeda.application.art.member.param.CompanyQueryParam;
import com.youkeda.application.art.member.param.DepartmentAccountParam;
import com.youkeda.application.art.member.service.AccountService;
import com.youkeda.application.art.member.service.CompanyService;
import com.youkeda.application.art.member.service.DepartmentAccountService;
import com.youkeda.application.art.member.service.DepartmentService;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.model.Company;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2019/11/8, 周五
 */
@NeedLogin
@Controller
@RequestMapping(path = "/api/department")
public class DepartmentApi {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentAccountService departmentAccountService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/save")
    @ResponseBody
    public Result<Department> save(@RequestBody Department department) {
        Result<Department> result = new Result<>();

        // 参数校验
        if (StringUtils.isBlank(department.getName())) {
            return result;
        }
        result.setData(departmentService.save(department));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(path = "/delete")
    @ResponseBody
    public Result delete(String departmentId) {
        Result result = new Result<>();

        try {
            departmentService.delete(departmentId);
            result.setSuccess(true);
        } catch (ErrorException e) {
            e.setResult(result);
            return result;
        }
        return result;
    }

    /**
     * 添加部门成员信息
     */
    @PostMapping(path = "/user/add")
    @ResponseBody
    public Result addUser(@RequestBody AddDepartmentAccountParam addDepartmentAccountParam) {
        Result result = new Result<>();

        try {
            departmentAccountService.addAccount(addDepartmentAccountParam.getDepartmentId(),
                                                addDepartmentAccountParam.getAccount());
        } catch (ErrorException e) {
            e.setResult(result);
            return result;
        }

        result.setSuccess(true);
        return result;
    }

    /**
     * 批量删除用户信息
     */
    @PostMapping(path = "/users/delete")
    @ResponseBody
    public Result deleteUsers(@RequestBody DeleteAccountParam deleteAccountParam) {
        Result result = new Result();
        departmentAccountService.deleteAccounts(deleteAccountParam.getDepartmentId(),
                                                deleteAccountParam.getAccountIds());
        return result.setSuccess(true);
    }

    @GetMapping(value = "/query")
    @ResponseBody
    public Result<Department> query(HttpServletRequest request,
                                    @RequestParam(value = "companyId", required = false) String companyId,
                                    @RequestParam(value = "companyCode", required = false) String companyCode) {
        if (companyId == null) {
            LoginUser loginUser = (LoginUser)request.getSession().getAttribute(MemberUtil.LOGIN_KEY);
            if (loginUser != null) {
                companyId = loginUser.getCompanyId();
            }
        }

        if (companyCode != null) {
            Company company = companyService.find(CompanyQueryParam.create().setCode(companyCode));
            if (company != null) {
                companyId = company.getId();
            }
        }

        Result<Department> result = new Result<>();
        if (StringUtils.isBlank(companyId)) {
            return result;
        }
        Department department = departmentService.getAllWithTree(companyId);
        result.setSuccess(true);
        result.setData(department);
        return result;
    }

    @GetMapping(value = "/queryaccounts")
    @ResponseBody
    public Result<Paging<Account>> queryAccounts(DepartmentAccountParam param) {
        Result<Paging<Account>> result = new Result<>();
        result.setSuccess(true);
        Paging<Account> accountPaging = departmentAccountService.queryAccounts(param);
        result.setData(accountPaging);
        return result;
    }

    @GetMapping(path = "/querybyuserinfo")
    @ResponseBody
    public Result<List<Department>> queryByUserInfo(
        @RequestParam(value = "companyId", required = false) String companyId,
        @RequestParam(value = "keywords", required = false) String keywords) {
        Result<List<Department>> result = new Result<>();
        if (StringUtils.isEmpty(companyId)) {
            companyId = "5dd20ef87eab793d1bbc53ab";
        }
        List<Department> departments = departmentService.queryByUserInfo(companyId, keywords);
        result.setSuccess(true);
        result.setData(departments);
        return result;
    }

    @PostMapping(value = "/adjust")
    @ResponseBody
    public Result adjust(@RequestBody AdjustDepartmentParam param) {
        Result result = new Result();
        if (StringUtils.isEmpty(param.getDestDepartmentId()) || CollectionUtils.isEmpty(param.getAccountIds())) {
            result.setMessage("目的部门Id或者员工信息不能为空！");
            return result;
        }
        String originDepartmentId = param.getOriginDepartmentId();
        String departmentId = param.getDestDepartmentId();
        List<String> accountIds = param.getAccountIds();
        departmentAccountService.adjustAccountDepartment(originDepartmentId, departmentId, accountIds);
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/departmentkeyword")
    @ResponseBody
    public Result<List<Account>> queryByKeyword(@RequestParam("departmentId") String departmentId,
                                                @RequestParam("keyword") String keyword, HttpServletRequest request) {

        Result<List<Account>> result = new Result<>();
        String companyId = null;
        LoginUser loginUser = (LoginUser)request.getSession().getAttribute(MemberUtil.LOGIN_KEY);
        if (loginUser != null) {
            companyId = loginUser.getCompanyId();
        }

        if (StringUtils.isBlank(companyId)) {
            return result;
        }

        result.setData(departmentAccountService.queryAccountsByKeyword(departmentId, keyword, companyId));
        result.setSuccess(true);
        return result;

    }

    @RequestMapping(path = "/query/acountdepartment", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Result<List<Department>> queryByAccountId(
        @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false) LoginUser loginUser) {

        Result<List<Department>> result = new Result<>();
        String accountId = loginUser.getAccountId();
        if (StringUtils.isBlank(accountId)) {
            return result;
        }

        result.setData(departmentAccountService.getByAccountId(accountId));
        result.setSuccess(true);
        return result;
    }

    @GetMapping("/existondepartment")
    @ResponseBody
    public Result accountExistOnDepartment(HttpServletRequest request,
                                           @RequestParam(value = "departmentId", required = false)
                                               String departmentId) {

        Result result = new Result();
        LoginUser loginUser = (LoginUser)request.getSession().getAttribute(MemberUtil.LOGIN_KEY);

        if (StringUtils.isBlank(departmentId) && loginUser != null) {
            departmentId = loginUser.getCompanyId();
        }

        String accountId = null;
        if (loginUser != null) {
            accountId = loginUser.getAccountId();
        }

        result.setSuccess(departmentAccountService.accountExistOnDepartment(accountId, departmentId));
        return result;

    }

}
