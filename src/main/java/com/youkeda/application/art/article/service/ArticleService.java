package com.youkeda.application.art.article.service;

import com.youkeda.application.art.article.model.Article;
import com.youkeda.application.art.article.model.ArticleDetail;
import com.youkeda.application.art.article.model.RefCategory;
import com.youkeda.application.art.article.param.QueryArticlePagingParam;
import com.youkeda.application.art.article.param.QueryArticleRefCategoryPagingParam;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.model.Paging;

import java.util.List;

/**
 * @Author
 * @DATE 2020-03-16
 */
public interface ArticleService {
    /**
     * 添加或更新
     */
    Result<Article> saveArticle(Article article);

    /**
     * 添加文章详情
     */
    ArticleDetail saveArticleDetail(ArticleDetail articleDetail);

    /**
     * 分页获取文章
     */
    Paging<Article> queryPagingArticle(QueryArticlePagingParam param);

    /**
     * 获取单个文章信息
     */
    Article getArticle(String articleId, boolean content);

    /**
     * 删除文章
     *
     * @param articleId
     * @return
     */
    boolean delete(String articleId);

    /**
     * 获取所有的文章类别
     */
    List<String> getArticleCategory();

    /**
     * 根据关联的活动类型分页查询
     *
     * @param param
     * @return
     */
    Paging<Article> getArticleByRefCategory(QueryArticleRefCategoryPagingParam param);

    /**
     * 获取当前文章的下一篇
     */
    Article getNextArticle(String articleId, boolean next, String articleCategory, String companyId,
                           RefCategory refCategory);

}
