package com.youkeda.application.art.member.service;

import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.model.Department;

import java.util.List;

/**
 * 部门服务
 *
 */
public interface DepartmentService {

    /**
     * 保存部门
     *
     * @param department
     * @return
     */
    Department save(Department department);

    /**
     * 删除部门
     *
     * @param departmentId
     * @return
     * @throws ErrorException
     */
    void delete(String departmentId) throws ErrorException;

    /**
     * Tree查询所有部门信息
     *
     * @param companyId 公司主键
     * @return List<Department>
     */
    Department getAllWithTree(String companyId);

    /**
     * 获取部门信息
     *
     * @param departmentIds
     * @return
     */
    List<Department> getByIds(List<String> departmentIds);

    /**
     * 通过关键字查询部门
     *
     * @return List
     */
    List<Department> queryByUserInfo(String companyId, String keywords);

}
