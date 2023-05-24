package com.youkeda.application.art.article.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

/**
 * @Author
 * @DATE 2020-03-16
 */
public class ArticleSource {
    /**
     * 文章来源链接
     */
    private String url;
    /**
     * 文章来源名称
     */
    private String originalName;
    /**
     * 文章来源标题
     */
    @Transient
    private String originalTitle;
    /**
     * 文章来源作者
     */
    private String originalAuthor;
    /**
     * 文章来源创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime originalGmtCreated;
    /**
     * 源文章点赞数
     */
    private int originalLikeNum = 0;
    /**
     * 是否需要版权
     */
    private boolean copyright;
    /**
     * 源文章阅读数
     */
    private int originalReadNum = 0;
    /**
     * 源文章分享数
     */
    private int originalShareNum = 0;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(String originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public LocalDateTime getOriginalGmtCreated() {
        return originalGmtCreated;
    }

    public void setOriginalGmtCreated(LocalDateTime originalGmtCreated) {
        this.originalGmtCreated = originalGmtCreated;
    }

    public int getOriginalReadNum() {
        return originalReadNum;
    }

    public void setOriginalReadNum(int originalReadNum) {
        this.originalReadNum = originalReadNum;
    }

    public int getOriginalLikeNum() {
        return originalLikeNum;
    }

    public void setOriginalLikeNum(int originalLikeNum) {
        this.originalLikeNum = originalLikeNum;
    }

    public int getOriginalShareNum() {
        return originalShareNum;
    }

    public void setOriginalShareNum(int originalShareNum) {
        this.originalShareNum = originalShareNum;
    }

    public boolean isCopyright() {
        return copyright;
    }

    public void setCopyright(boolean copyright) {
        this.copyright = copyright;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
}
