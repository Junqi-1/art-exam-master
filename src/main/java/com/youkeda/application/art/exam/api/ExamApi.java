package com.youkeda.application.art.exam.api;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.exam.model.Exam;
import com.youkeda.application.art.exam.param.ExamQueryParam;
import com.youkeda.application.art.exam.service.ExamService;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.application.art.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/20, 周五
 */
@NeedLogin
@Controller
@RequestMapping(value = "/api/exam")
public class ExamApi {

    private Logger logger = LoggerFactory.getLogger(ExamApi.class);

    @Autowired
    private ExamService examService;

    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/add")
    @ResponseBody
    public Result<Exam> add(@RequestBody Exam exam) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isNotBlank(exam.getId())) {
            return result;
        }

        result.setData(examService.save(exam));
        result.setSuccess(true);
        return result;

    }

    @PostMapping(value = "/update")
    @ResponseBody
    public Result<Exam> update(@RequestBody Exam exam) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isBlank(exam.getId())) {
            return result;
        }

        result.setData(examService.save(exam));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public Result<Exam> delete(@RequestParam("examId") String examId) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isBlank(examId)) {
            return result;
        }

        return examService.delete(examId);
    }

    @GetMapping(value = "/query/orgid")
    @ResponseBody
    public Result<List<Exam>> queryByOrgId(@RequestParam("orgId") String orgId) {
        Result<List<Exam>> result = new Result<>();
        ExamQueryParam examQueryParam = new ExamQueryParam();
        examQueryParam.setOrgId(orgId);
        result.setData(examService.query(examQueryParam));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/get/current")
    @ResponseBody
    public Result<Exam> getCurrentExam(@RequestParam("orgId") String orgId) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isBlank(orgId)) {
            return result;
        }
        result.setData(examService.getCurrentExam(orgId));
        result.setSuccess(true);
        return result;
    }

    @PostMapping(value = "/upload/payorstate")
    @ResponseBody
    public Result<Exam> upload(@RequestParam(value = "stateUrl", required = false) String stateUrl,
                               @RequestParam(value = "payUrl", required = false) String payUrl,
                               @RequestParam(value = "examId") String examId) {

        Result<Exam> result = new Result<>();
        if (StringUtils.isBlank(stateUrl) && StringUtils.isBlank(payUrl)) {
            return result;
        }

        if (StringUtils.isBlank(examId)) {
            return result;
        }

        Exam exam = new Exam();
        exam.setId(examId);
        exam.setPaymentUrl(payUrl);
        exam.setStatementsUrl(stateUrl);

        result.setData(examService.save(exam));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/role")
    @ResponseBody
    public Result<List<Exam>> queryByRole(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                          @RequestParam("orgId") String orgId) {

        Result<List<Exam>> result = new Result<>();

        if (StringUtils.isBlank(loginUser.getAccountId())) {
            return result;
        }

        result.setData(examService.queryVisibleExams(orgId, loginUser.getAccountId()));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/query/edit")
    @ResponseBody
    public Result editExamPermission(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                     @RequestParam("orgId") String orgId) {

        Result result = new Result<>();

        if (StringUtils.isBlank(loginUser.getAccountId())) {
            result.setCode("403");
            return result;
        }

        return examService.editExamPermission(orgId, loginUser.getAccountId());
    }

    @GetMapping(value = "/get")
    @ResponseBody
    public Result<Exam> get(@RequestParam("id") String id) {
        Result<Exam> result = new Result<>();

        if (StringUtils.isBlank(id)) {
            return result;
        }

        result.setData(examService.get(id));
        result.setSuccess(true);
        return result;
    }

    @GetMapping(value = "/canSignup")
    @ResponseBody
    public Result<Boolean> canSignup(@RequestParam("id") String id) {
        Result<Boolean> result = new Result<>();

        if (StringUtils.isBlank(id)) {
            result.setData(false);
            result.setMessage("missing id");
            return result;
        }

        if (!ObjectId.isValid(id)) {
            result.setData(false);
            result.setMessage("id not ObjectId");
            return result;
        }

        Exam exam = examService.get(id);
        if (exam == null || exam.getSignUpStart() == null || exam.getSignUpEnd() == null) {
            result.setData(false);
            return result;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exam.getSignUpStart()) && now.isBefore(exam.getSignUpEnd())) {
            result.setData(true);
        } else {
            result.setData(false);
        }

        result.setSuccess(true);
        return result;
    }

}
