package com.youkeda.application.art.exam.service;

import com.youkeda.application.art.exam.model.RegistrationPoint;
import com.youkeda.application.art.exam.param.RegistrationPointPagingParam;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.model.Paging;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author songchuanming
 * @DATE 2021/4/14.
 */
public interface RegistrationPointService {
    /**
     * 报名点保存
     *
     * @param registrationPoint
     * @return
     */
    RegistrationPoint save(RegistrationPoint registrationPoint);

    /**
     * 分页查询报名点信息
     *
     * @param pagingParam 分页查询参数
     * @return Paging<RegistrationPoint>
     */
    Paging<RegistrationPoint> pageQuery(RegistrationPointPagingParam pagingParam);

    /**
     * 根据考级点id查询到全部报名点
     */
    List<RegistrationPoint> queryplus(RegistrationPointPagingParam pagingParam);

    /**
     * 通过报名点id查询报名点
     *
     * @param registrationPointId 报名点id
     * @return RegistrationPoint
     */
    RegistrationPoint get(String registrationPointId);

    /**
     * 删除报名点信息
     *
     * @param registrationPointId 报名点id
     * @return boolean
     */
    boolean delete(String registrationPointId);
}
