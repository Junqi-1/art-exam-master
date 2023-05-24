package com.youkeda.application.art.member.model;

import com.youkeda.model.Base;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "addressbook_departments", collation = "{locale: 'zh'}")
public class Department extends Base implements Tree<Department> {
    private static final long serialVersionUID = -1035654878147935923L;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门描述
     */
    private String desc;
    /**
     * 主管，部门经理
     */
    @Field(targetType = FieldType.OBJECT_ID)
    private String managerAccountId;
    /**
     * 父部门Id
     */
    @Field(targetType = FieldType.OBJECT_ID)
    private String parentId;
    /**
     * 上级部门Ids
     */
    private List<String> parentIds = new ArrayList<>();
    /**
     * 子部门
     */
    @Transient
    private List<Department> children;
    /**
     * 排序值
     */
    private int sort;
    /**
     * 员工人数
     */
    private long memberNum = 0;

    public Department() {
    }

    public Department(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getManagerAccountId() {
        return managerAccountId;
    }

    public void setManagerAccountId(String managerAccountId) {
        this.managerAccountId = managerAccountId;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(long memberNum) {
        this.memberNum = memberNum;
    }

    @Override
    public void addChildren(Department tree) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(tree);
    }
}
