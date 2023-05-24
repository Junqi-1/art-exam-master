package com.youkeda.application.art.article.param;

import com.youkeda.application.art.article.model.OrderByType;
import com.youkeda.model.BasePagingParam;

import java.util.List;

/**
 * @Author
 * @DATE 2020-03-16
 */
public class QueryArticlePagingParam extends BasePagingParam {

    /**
     * 文章类型
     */
    private String articleCategory;

    /**
     * 排序类型
     */
    private OrderByType orderByType;

    private String keyWord;

    private String title;

    private String companyId;

    private String status;

    private String author;

    private List<String> authors;

    // 只查询自己的
    private boolean filterMe = false;

    private String accountId;

    // 是否需要文章详情
    private boolean showArticleDetail = false;

    public String getArticleCategory() {
        return articleCategory;
    }

    public void setArticleCategory(String articleCategory) {
        this.articleCategory = articleCategory;
    }

    public OrderByType getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(OrderByType orderByType) {
        this.orderByType = orderByType;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public boolean isFilterMe() {
        return filterMe;
    }

    public void setFilterMe(boolean filterMe) {
        this.filterMe = filterMe;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isShowArticleDetail() {
        return showArticleDetail;
    }

    public void setShowArticleDetail(boolean showArticleDetail) {
        this.showArticleDetail = showArticleDetail;
    }
}
