package com.youkeda.application.art.exam.service;

import com.youkeda.application.art.exam.model.Exam;
import com.youkeda.application.art.exam.model.ExamStage;
import com.youkeda.application.art.exam.param.ExamQueryParam;
import com.youkeda.application.art.member.model.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * TODO
 *
 * @author zr
 * @date 2020/3/19, 周四
 */
public interface ExamService {
    /**
     * 添加修改考试信息
     *
     * @param exam 考试
     * @return Exam
     */
    Exam save(Exam exam);

    /**
     * 删除考试
     *
     * @param examId 考试id
     * @return Exam
     */
    Result<Exam> delete(String examId);

    /**
     * 查询考试
     *
     * @param param 考试查询参数
     * @return List<Exam>
     */
    List<Exam> query(ExamQueryParam param);

    /**
     * 获取当前正在进行的考试
     *
     * @param orgId 考级点id
     * @return Exam
     */
    Exam getCurrentExam(String orgId);

    /**
     * 查询可视的考试（根据权限判断）
     *
     * @param accountId 用户主键
     * @param orgId     考级点主键
     * @return List<Exam>
     */
    List<Exam> queryVisibleExams(String orgId, String accountId);

    /**
     * 查询有无修改权限
     *
     * @param orgId     考级点
     * @param accountId 用户
     * @return Result
     */
    Result editExamPermission(String orgId, String accountId);

    /**
     * 获取exam
     *
     * @param examId 主键
     * @return Exam
     */
    Exam get(String examId);

}
