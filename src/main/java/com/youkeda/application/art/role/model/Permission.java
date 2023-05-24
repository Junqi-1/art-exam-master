package com.youkeda.application.art.role.model;

import com.youkeda.model.Base;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 权限模型
 */
@Document(collection = "permission", collation = "{locale:'zh'}")
public class Permission extends Base<Permission> {

    /**
     * 权限中文标题
     */
    private String title;

    /**
     * 权限名称，英文单词
     */
    private String name;

    /**
     * 权限备注
     */
    private String remark;

    /**
     * 关联的其他主键
     */
    private String refId;

    /**
     * 关联的其他数据名称
     */
    private String refName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }
}
