package com.youkeda.application.art.article.model;

import com.youkeda.model.Base;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 * @Author
 * @DATE 2020-03-16
 */
@Document(collection = "art_article_detail", collation = "{locale:'zh'}")
public class ArticleDetail extends Base<ArticleDetail> {
    /**
     * 文章内容
     */
    private String content;

    @Field(targetType = FieldType.OBJECT_ID)
    private String articleId;

    /**
     * 源文章内容
     */
    private String originalContent;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}
