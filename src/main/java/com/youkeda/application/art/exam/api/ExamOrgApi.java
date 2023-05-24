package com.youkeda.application.art.exam.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.exam.model.Association;
import com.youkeda.application.art.exam.model.ExamOrg;
import com.youkeda.application.art.exam.param.OrgQueryParam;
import com.youkeda.application.art.exam.service.ExamOrgService;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
@NeedLogin
@Controller
@RequestMapping(value = "/api/examorg")
    public class ExamOrgApi {

    @Autowired
    private ExamOrgService examOrgService;

    @PostMapping(value = "/add")
    @ResponseBody
    public Result<ExamOrg> add(@RequestBody ExamOrg examOrg) {

        Result<ExamOrg> result = new Result<>();
        if (StringUtils.isNotBlank(examOrg.getId())) {
            return result;
        }

        result.setData(examOrgService.save(examOrg));
        result.setSuccess(true);
        return result;

    }

    @PostMapping(value = "/update")
    @ResponseBody
    public Result<ExamOrg> update(@RequestBody ExamOrg examOrg) {

        Result<ExamOrg> result = new Result<>();
        if (StringUtils.isBlank(examOrg.getId())) {
            return result;
        }

        result.setData(examOrgService.save(examOrg));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public Result<ExamOrg> delete(@RequestParam("examOrgId") String examOrgId) {

        return examOrgService.delete(examOrgId);

    }

    @GetMapping(value = "/query/association")
    @ResponseBody
    public Result<List<ExamOrg>> queryByAssociation(@RequestParam("association") Association association) {

        Result<List<ExamOrg>> result = new Result<>();

        OrgQueryParam param = new OrgQueryParam();
        param.setAssociation(association);
        param.setWantAccount(true);
        result.setData(examOrgService.query(param));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query")
    @ResponseBody
    public Result<List<ExamOrg>> query(OrgQueryParam param) {
        Result<List<ExamOrg>> result = new Result<>();
        result.setSuccess(true);
        result.setData(examOrgService.query(param));
        return result;
    }

    @GetMapping(value = "/pagequery")
    @ResponseBody
    public Result<Paging<ExamOrg>> pageQuery(OrgQueryParam param) {
        Result<Paging<ExamOrg>> result = new Result<>();

        if (param.getAssociation() == null) {
            if (CollectionUtils.isEmpty(param.getChargeIds()) && StringUtils.isEmpty(param.getLeadId())) {
                return result;
            }
        }

        result.setSuccess(true);
        param.setWantAccount(true);
        result.setData(examOrgService.pageQuery(param));
        return result;
    }

    @GetMapping(value = "/query/associations")
    @ResponseBody
    public Result<List<String>> queryAllAssociations() {

        Result<List<String>> result = new Result<>();

        List<String> associations = new ArrayList<>();
        for (Association association : Association.values()) {
            associations.add(association.name());
        }
        result.setData(associations);
        result.setSuccess(true);

        return result;
    }

    @GetMapping(value = "/query/keyword")
    @ResponseBody
    public Result<List<ExamOrg>> queryByKeyword(@RequestParam("keyword") String keyword) {

        Result<List<ExamOrg>> result = new Result<>();

        if (keyword == null) {
            return result;
        }
        OrgQueryParam orgQueryParam = new OrgQueryParam();
        orgQueryParam.setKeyword(keyword);
        orgQueryParam.setWantAccount(true);
        result.setData(examOrgService.query(orgQueryParam));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/query/examiners")
    @ResponseBody
    public Result<List<Account>> queryAllExaminers(@RequestParam("departmentId") String departmentId,
                                                   @RequestParam("keyword") String keyword,
                                                   @SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<Account>> result = new Result<>();

        String companyId = loginUser.getCompanyId();
        if (StringUtils.isBlank(companyId)) {
            return result;
        }

        result.setData(examOrgService.queryAllExaminers(departmentId, keyword, companyId));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/query/examinerid")
    @ResponseBody
    public Result<Paging<ExamOrg>> queryByExaminerId(OrgQueryParam param) {

        Result<Paging<ExamOrg>> result = new Result<>();

        result.setData(examOrgService.pageQuery(param));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/get")
    @ResponseBody
    public Result<ExamOrg> get(@RequestParam("examOrgId") String examOrgId) {

        Result<ExamOrg> result = new Result<>();

        result.setData(examOrgService.get(examOrgId));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/query/role")
    @ResponseBody
    public Result<Paging<ExamOrg>> queryByRole(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                               OrgQueryParam orgQueryParam) {

        Result<Paging<ExamOrg>> result = new Result<>();

        if (StringUtils.isBlank(loginUser.getAccountId())) {
            return result;
        }

        orgQueryParam.setAdminId(loginUser.getAccountId());
        orgQueryParam.setWantAccount(true);
        result.setData(examOrgService.queryVisibleOrgs(orgQueryParam));
        result.setSuccess(true);
        return result;

    }

    @GetMapping(value = "/query/edit")
    @ResponseBody
    public Result<List<String>> editOrgPermission(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser) {

        Result<List<String>> result = new Result<>();

        if (StringUtils.isBlank(loginUser.getAccountId())) {
            return result;
        }

        result.setSuccess(true);
        return examOrgService.editOrgPermission(loginUser.getAccountId());

    }

    @GetMapping(value = "/get/maxnum")
    @ResponseBody
    public Result<String> getLargestNum() {

        Result<String> result = new Result<>();

        result.setSuccess(true);
        result.setData(examOrgService.getLargestNum());
        return result;
    }
}
