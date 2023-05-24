package com.youkeda.application.art.member.model;

import java.util.List;

/**
 * 树形的父类
 */
public interface Tree<T> {

    String getParentId();

    void setParentId(String parentId);

    String getId();

    List<T> getChildren();

    void addChildren(T tree);
}
