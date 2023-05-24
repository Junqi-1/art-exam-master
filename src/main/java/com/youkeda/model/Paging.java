package com.youkeda.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * 分页模型
 */
public class Paging<R> implements Serializable {

    private static final long serialVersionUID = 522660448543880825L;
    /**
     * 页数
     */
    private int pagination;

    /**
     * 每页数量
     */
    private int pageSize = 15;
    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总记录数
     */
    private long totalCount;

    private List<R> data;

    public Paging() {

    }

    public Paging(int pagination, int pageSize, long totalCount, List<R> data) {
        this.pagination = pagination;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.data = data;

    }

    public int getPagination() {
        return pagination;
    }

    public Paging setPagination(int pagination) {
        this.pagination = pagination;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Paging setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getTotalPage() {

        if (totalPage != 0) {
            return totalPage;
        }
        int page = (int)(totalCount % pageSize);
        if (page <= 0) {
            totalPage = (int)(totalCount / pageSize);
        } else {
            totalPage = (int)(totalCount / pageSize) + 1;
        }

        return totalPage;
    }

    public Paging setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public Paging setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public List<R> getData() {
        return data;
    }

    public Paging setData(List<R> data) {
        this.data = data;
        return this;
    }

    /**
     * 计算分页数据
     *
     * @param mongoTemplate
     * @param collectionClass
     * @param operations
     * @param pagingParam
     * @param <T>
     * @return
     */
    @SuppressWarnings(value = {"unchecked"})
    public static <T> Paging<T> compute(MongoTemplate mongoTemplate, Class collectionClass,
                                        List<AggregationOperation> operations, BasePagingParam pagingParam) {
        List<AggregationOperation> newOperations = new ArrayList<>(operations);
        newOperations.add(count().as("totalCount"));
        Aggregation ag = newAggregation(newOperations);
        //System.out.println(ag.toString());
        Paging<T> paging = mongoTemplate.aggregate(ag, collectionClass, Paging.class)
            .getUniqueMappedResult();
        if (paging == null) {
            paging = new Paging<>();
        }
        paging.setPageSize(pagingParam.getPageSize());
        paging.setPagination(pagingParam.getPagination());
        return paging;
    }

    public static <T> Paging<T> compute(MongoTemplate mongoTemplate, List<AggregationOperation> operations,
                                        Class<T> collectionClass, String collectionName, int pageNum, int pageSize) {
        List<AggregationOperation> newOperations = new ArrayList<>(operations);
        newOperations.add(count().as("totalCount"));

        Paging paging = mongoTemplate.aggregate(newAggregation(newOperations), collectionName, Paging.class)
            .getUniqueMappedResult();
        if (paging == null) {
            paging = new Paging<>();
            return paging;
        }
        paging.setPageSize(pageSize);
        paging.setPagination(pageNum);

        //计算总页数
        int totalPage = (int)Math.ceil(paging.getTotalCount() / (pageSize * 1.0));
        paging.setTotalPage(totalPage);
        operations.add(skip(pageNum * pageSize));
        operations.add(limit(pageSize));

        List<T> results = mongoTemplate.aggregate(newAggregation(operations), collectionName, collectionClass)
            .getMappedResults();
        paging.setData(results);
        return paging;
    }

    public static <T> Paging<T> page(MongoTemplate mongoTemplate, Query query, Class<T> collectionClass, int pageNum,
                                     int pageSize) {
        return page(mongoTemplate, query, collectionClass, mongoTemplate.getCollectionName(collectionClass), pageNum,
                    pageSize);
    }

    public static <T> Paging<T> page(MongoTemplate mongoTemplate, Query query, Class<T> collectionClass,
                                     String collectionName, int pageNum, int pageSize) {
        Paging<T> paging = new Paging<>();
        paging.setPageSize(pageSize);
        paging.setPagination(pageNum);

        long count = mongoTemplate.count(query, collectionName);
        paging.setTotalCount(count);
        if (count == 0) {
            return paging;
        }
        List<T> data = mongoTemplate.find(query.with(PageRequest.of(pageNum, pageSize)), collectionClass,
                                          collectionName);
        paging.setData(data);
        return paging;
    }

    public static void main(String[] args) {
        Paging p = new Paging();
        p.setPageSize(10);
        p.setTotalCount(10);
        int a = 20 % 10;
        System.out.println(a);
    }
}
