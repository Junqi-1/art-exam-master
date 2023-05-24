package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.model.Account;
import com.youkeda.application.art.member.model.Department;
import com.youkeda.application.art.member.param.DepartmentAccountParam;
import com.youkeda.model.Paging;

import java.util.List;

public interface DepartmentAccountService {

    /**
     * 分页查询部门员工
     *
     * @param param
     * @return
     */
    Paging<Account> queryAccounts(DepartmentAccountParam param);

    /**
     * 添加部门成员信息
     */
    void addAccount(String departmentId, Account account) throws ErrorException;

    /**
     * 批量删除用户信息
     *
     * @param departmentId
     * @param accountIds
     */
    void deleteAccounts(String departmentId, List<String> accountIds);

    /**
     * 批量调整用户部门
     *
     * @param originDepartmentId 源部门
     * @param destDepartmentId   目标部门
     * @param accountIdList      账户
     * @return
     */
    void adjustAccountDepartment(String originDepartmentId, String destDepartmentId, List<String> accountIdList);

    /**
     * 根据关键词查询部门用户
     *
     * @param departmentId 部门
     * @param keyword      关键词
     * @param companyId    公司
     * @return List<Account>
     */
    List<Account> queryAccountsByKeyword(String departmentId, String keyword, String companyId);

    /**
     * 查询用户是否在某个部门
     *
     * @param accountId    用户主键
     * @param departmentId 部门
     * @return boolean
     */
    boolean accountExistOnDepartment(String accountId, String departmentId);

    /**
     * 根据用户id查询
     * @param accountId 主键
     * @return List<Department>
     */
    List<Department> getByAccountId(String accountId);
}
