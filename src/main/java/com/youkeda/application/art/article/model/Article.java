package com.youkeda.application.art.article.model;

import com.youkeda.model.Base;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * @Author
 * @DATE 2020-03-16
 */
@Document(collection = "art_article", collation = "{locale:'zh'}")
public class Article extends Base<Article> {

    /**
     * 文章标题
     */
    private String title;
    /**
     * 发布人accountId
     */
    private String accountId;

    /**
     * 评论数量
     */
    private int commentNum;

    /**
     * 关联活动类型
     */
    private RefCategory refCategory;
    /**
     * 附件url
     */
    private List<String> ossUrl;
    /**
     * 文章详情
     */
    private ArticleDetail articleDetail;

    private ArticleSource articleSource;
    /**
     * 总阅读数
     */
    private long totalReadNum = 0;
    /**
     * 总点赞数
     */
    private long totalLikeNum = 0;
    /**
     * 编辑次数
     */
    private int editNum;
    /**
     * 总分享数
     */
    private long totalShareNum = 0;
    /**
     * 文章banner
     */
    private String banner;

    /**
     * 文章来源
     */
    private ArticleSourceType articleSourceType;
    /**
     * 文章作者
     */
    private String author;
    /**
     * 作者头像
     */
    private String avatarUrl;
    /**
     * 文章类型
     */
    private String articleCategory;
    /**
     * 状态
     */
    private String status;

    /**
     * 链接地址
     */
    private String linkUrl;
    /**
     * 额外属性
     */
    private Map<String, String> context;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArticleDetail getArticleDetail() {
        return articleDetail;
    }

    public void setArticleDetail(ArticleDetail articleDetail) {
        this.articleDetail = articleDetail;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public ArticleSourceType getArticleSourceType() {
        return articleSourceType;
    }

    public void setArticleSourceType(ArticleSourceType articleSourceType) {
        this.articleSourceType = articleSourceType;
    }

    public String getArticleCategory() {
        return articleCategory;
    }

    public void setArticleCategory(String articleCategory) {
        this.articleCategory = articleCategory;
    }

    public long getTotalReadNum() {
        return totalReadNum;
    }

    public void setTotalReadNum(long totalReadNum) {
        this.totalReadNum = totalReadNum;
    }

    public long getTotalLikeNum() {
        return totalLikeNum;
    }

    public void setTotalLikeNum(long totalLikeNum) {
        this.totalLikeNum = totalLikeNum;
    }

    public ArticleSource getArticleSource() {
        return articleSource;
    }

    public void setArticleSource(ArticleSource articleSource) {
        this.articleSource = articleSource;
    }

    public long getTotalShareNum() {
        return totalShareNum;
    }

    public void setTotalShareNum(long totalShareNum) {
        this.totalShareNum = totalShareNum;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(List<String> ossUrl) {
        this.ossUrl = ossUrl;
    }

    public RefCategory getRefCategory() {
        return refCategory;
    }

    public void setRefCategory(RefCategory refCategory) {
        this.refCategory = refCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public int getEditNum() {
        return editNum;
    }

    public void setEditNum(int editNum) {
        this.editNum = editNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
