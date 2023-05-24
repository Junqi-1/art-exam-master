package com.youkeda.model;

import java.io.Serializable;

public abstract class BasePagingParam<T> implements Serializable {
    /**
     * 页数
     */
    private int pagination = 0;

    /**
     * 每页数量
     */
    private int pageSize = 15;

    public int getPagination() {
        return pagination;
    }

    public T setPagination(int pagination) {
        this.pagination = pagination;
        return (T)this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public T setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return (T)this;
    }
}
