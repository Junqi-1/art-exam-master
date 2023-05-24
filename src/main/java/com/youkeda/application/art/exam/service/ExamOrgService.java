package com.youkeda.application.art.exam.service;

import com.youkeda.application.art.exam.model.ExamOrg;
import com.youkeda.application.art.exam.param.OrgQueryParam;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
public interface ExamOrgService {
    /**
     * 添加修改考级点信息
     *
     * @param examOrg 考级点
     * @return ExamOrg
     */
    ExamOrg save(ExamOrg examOrg);

    /**
     * 删除考级点
     *
     * @param orgId 考级点id
     * @return ExamOrg
     */
    Result<ExamOrg> delete(String orgId);

    /**
     * 查询考级点
     *
     * @param orgQueryParam 考级点查询参数
     * @return List<ExamOrg>
     */
    List<ExamOrg> query(OrgQueryParam orgQueryParam);

    /**
     * 分页查询考级点
     *
     * @param orgQueryParam 分页查询考级点参数
     * @return List<ExamOrg>
     */
    Paging<ExamOrg> pageQuery(OrgQueryParam orgQueryParam);

    /**
     * 查询所有可以被选为考级点负责人用户
     *
     * @param companyId    公司
     * @param keyword      关键词
     * @param departmentId 部门
     * @return List<Account>
     */
    List<Account> queryAllExaminers(String departmentId, String keyword, String companyId);

    /**
     * 通过主键查询考级点
     *
     * @param examOrgId 考级点
     * @return ExamOrg
     */
    ExamOrg get(String examOrgId);

    /**
     * 更新考级点报名人数
     *
     * @param orgId 考级点
     * @return ExamOrg
     */
    ExamOrg updateSignUpIndex(String orgId);

    /**
     * 查询可视的考级点（根据权限判断）
     *
     * @param orgQueryParam 分页查询可以查看的考级点
     * @return List<ExamOrg>
     */
    Paging<ExamOrg> queryVisibleOrgs(OrgQueryParam orgQueryParam);

    /**
     * 查询成员有无修改权限
     *
     * @param accountId 用户
     * @return boolean
     */
    Result<List<String>> editOrgPermission(String accountId);

    /**
     * 查询最大的考级点编号
     *
     * @return String
     */
    String getLargestNum();

    /**
     * 查询考级点
     *
     * @return List<ExamOrg>
     */
    List<ExamOrg> queryExam(OrgQueryParam orgQueryParam);
}
