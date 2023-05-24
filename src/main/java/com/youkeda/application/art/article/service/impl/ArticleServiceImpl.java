package com.youkeda.application.art.article.service.impl;

import com.youkeda.application.art.article.model.*;
import com.youkeda.application.art.article.param.QueryArticlePagingParam;
import com.youkeda.application.art.article.param.QueryArticleRefCategoryPagingParam;
import com.youkeda.application.art.article.service.ArticleService;
import com.youkeda.application.art.article.util.Markdown2Html;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.IDUtils;
import com.youkeda.model.Company;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @Author jiaoheng
 * @DATE 2020-03-16
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Result<Article> saveArticle(Article article) {

        Result<Article> result = new Result<>();
        if (article == null) {
            result.setMessage("参数为空");
            return result;
        }
        Update update = new Update();
        if (article.getArticleCategory() != null) {
            update.set("articleCategory", article.getArticleCategory());
        }
        if (article.getArticleSourceType() != null) {
            update.set("articleSourceType", article.getArticleSourceType());
        }
        if (article.getRefCategory() != null) {
            update.set("refCategory", article.getRefCategory());
        }
        if (!StringUtils.isEmpty(article.getAccountId())) {
            update.set("accountId", article.getAccountId());
        }
        if (article.getContext() != null) {
            update.set("context", article.getContext());
        }
        if (StringUtils.isNotEmpty(article.getBanner())) {
            update.set("banner", article.getBanner());
        }
        if (!StringUtils.isEmpty(article.getStatus())) {
            update.set("status", article.getStatus());
        }
        if (!StringUtils.isEmpty(article.getCompanyId())) {
            update.set("companyId", article.getCompanyId());
        } else {
            update.set("companyId", Company.DEFAULT.getId());
        }
        if (StringUtils.isNotEmpty(article.getTitle())) {
            update.set("title", article.getTitle());
        }
        if (!CollectionUtils.isEmpty(article.getOssUrl())) {
            update.set("ossUrl", article.getOssUrl());
        }

        if (StringUtils.isNotEmpty(article.getAuthor())) {
            update.set("author", article.getAuthor());
        }
        if (StringUtils.isNotEmpty(article.getAvatarUrl())) {
            update.set("avatarUrl", article.getAvatarUrl());
        }
        if (!StringUtils.isEmpty(article.getLinkUrl())) {
            update.set("linkUrl", article.getLinkUrl());
        }
        if (article.getArticleSource() != null) {
            update.set("articleSource", article.getArticleSource());

            if (article.getTotalLikeNum() == 0) {
                update.set("totalLikeNum", article.getArticleSource().getOriginalLikeNum());
            }
            if (article.getTotalReadNum() == 0) {
                update.set("totalReadNum", article.getArticleSource().getOriginalReadNum());
            }
            if (article.getTotalShareNum() == 0) {
                update.set("totalShareNum", article.getArticleSource().getOriginalShareNum());
            }

            if (StringUtils.isEmpty(article.getAuthor()) && StringUtils.isNotEmpty(
                article.getArticleSource().getOriginalAuthor())) {
                update.set("author", article.getArticleSource().getOriginalAuthor());
            }
            if (StringUtils.isEmpty(article.getTitle()) && StringUtils.isNotEmpty(
                article.getArticleSource().getOriginalTitle())) {
                update.set("title", article.getArticleSource().getOriginalTitle());
            }
        }

        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");

        if (StringUtils.isEmpty(article.getId())) {
            article.setId(IDUtils.getId());
        }
        mongoTemplate.upsert(Query.query(Criteria.where("_id").is(new ObjectId(article.getId()))), update,
                             Article.class);
        //添加文章详情
        if (article.getArticleDetail() != null) {
            article.getArticleDetail().setArticleId(article.getId());
            this.saveArticleDetail(article.getArticleDetail());

        }

        result.setSuccess(true);
        result.setData(article);
        return result;
    }

    @Override
    public ArticleDetail saveArticleDetail(ArticleDetail articleDetail) {
        Assert.notNull(articleDetail, "articleDetail not null");
        Assert.notNull(articleDetail.getArticleId(), "articleId not null");
        Assert.notNull(articleDetail.getOriginalContent(), "originalContent not null");

        Update update = new Update();
        update.setOnInsert("gmtCreated", LocalDateTime.now());
        update.currentDate("gmtModified");
        update.setOnInsert("articleId", articleDetail.getArticleId());

        update.set("originalContent", articleDetail.getOriginalContent());
        if (StringUtils.isEmpty(articleDetail.getId())) {
            articleDetail.setId(IDUtils.getId());
        }
        //先查寻一下是否有这个详情
        Criteria ca = new Criteria();
        ca.and("articleId").is(new ObjectId(articleDetail.getArticleId()));
        Query query = new Query();
        query.addCriteria(ca);
        ArticleDetail mongoTemplateOne = mongoTemplate.findOne(query, ArticleDetail.class);
        if (mongoTemplateOne != null) {
            update.set("content", articleDetail.getContent());
        } else {
            articleDetail.setContent(Markdown2Html.convert(articleDetail.getOriginalContent()));
            update.set("content", articleDetail.getContent());
        }
        mongoTemplate.upsert(query, update, ArticleDetail.class);
        return articleDetail;
    }

    @Override
    public Paging<Article> queryPagingArticle(QueryArticlePagingParam param) {
        Criteria ca = new Criteria();

        if (param.isFilterMe()) {
            ca.and("accountId").is(param.getAccountId());
        }
        if (param.getArticleCategory() != null) {
            ca.and("articleCategory").is(param.getArticleCategory());
        }
        if (!StringUtils.isEmpty(param.getTitle())) {
            ca.and("title").is(param.getTitle());
        }
        if (!StringUtils.isEmpty(param.getCompanyId())) {
            ca.and("companyId").is(param.getCompanyId());
        }
        if (!StringUtils.isEmpty(param.getStatus())) {
            ca.and("status").is(param.getStatus());
        }
        if (StringUtils.isNotEmpty(param.getAuthor())) {
            ca.and("author").is(param.getAuthor());
        }
        if (!CollectionUtils.isEmpty(param.getAuthors())) {
            ca.and("author").in(param.getAuthors());
        }

        //分页
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(match(ca));
        Paging<Article> articlePaging = Paging.compute(mongoTemplate, Article.class, operations, param);
        if (param.getOrderByType() != null) {
            if (param.getOrderByType().equals(OrderByType.LIKE)) {
                operations.add(sort(Sort.by(Sort.Direction.DESC, "totalLikeNum")));
            } else if (param.getOrderByType().equals(OrderByType.READ)) {
                operations.add(sort(Sort.by(Sort.Direction.DESC, "totalReadNum")));
            }
        } else if (StringUtils.isNotEmpty(param.getAuthor())) {
            operations.add(sort(Sort.by(Sort.Direction.DESC, "articleSource.originalGmtCreated", "gmtModified")));
        } else {
            operations.add(sort(Sort.by(Sort.Direction.DESC, "gmtModified", "articleSource.originalGmtCreated")));
        }

        operations.add(skip((long)(param.getPagination() * param.getPageSize())));
        operations.add(limit(param.getPageSize()));
        List<Article> articles = mongoTemplate.aggregate(Aggregation.newAggregation(operations), "yof_article",
                                                         Article.class).getMappedResults();

        List<ObjectId> articleIds = new ArrayList<>();
        articles.forEach(article -> {
            articleIds.add(new ObjectId(article.getId()));
            if (article.getArticleSource() != null) {
                article.getArticleSource().setOriginalAuthor(article.getAuthor());
            }
        });

        //判断是否需要详情
        if (param.isShowArticleDetail()) {
            //根据articleId查询详情
            Query query = new Query();
            Criteria criteria = new Criteria();
            if (!CollectionUtils.isEmpty(articleIds)) {
                criteria.and("articleId").in(articleIds);
            }

            query.addCriteria(criteria);
            //过滤掉原始的html
            List<ArticleDetail> articleDetail = mongoTemplate.find(query, ArticleDetail.class);
            Map<String, ArticleDetail> articleDetailMap = articleDetail.stream().collect(
                Collectors.toMap(ArticleDetail::getArticleId, t -> t));
            articles.forEach(article -> {
                if (articleDetailMap.get(article.getId()) != null) {
                    article.setArticleDetail(articleDetailMap.get(article.getId()));
                }

            });
        }
        articlePaging.setData(articles);
        return articlePaging;

    }

    @Override
    public Article getArticle(String articleId, boolean content) {
        if (StringUtils.isEmpty(articleId)) {
            return null;
        }
        Article article = null;
        Criteria ca = new Criteria();
        ca.and("_id").is(new ObjectId(articleId));
        Query query = new Query();
        query.addCriteria(ca);
        article = mongoTemplate.findOne(query, Article.class);

        if (content) {
            //根据articleId查询详情
            Query queryDetail = new Query();
            Criteria criteria = new Criteria();
            criteria.and("articleId").is(new ObjectId(article.getId()));
            queryDetail.addCriteria(criteria);
            //过滤掉原始的html
            ArticleDetail articleDetail = mongoTemplate.findOne(queryDetail, ArticleDetail.class);
            article.setArticleDetail(articleDetail);
        }

        return article;
    }

    @Override
    public boolean delete(String articleId) {
        if (StringUtils.isEmpty(articleId)) {
            return false;
        }
        Article article = mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(new ObjectId(articleId))),
                                                      Article.class);
        if (article != null) {
            ArticleDetail articleDetail = mongoTemplate.findAndRemove(
                new Query(Criteria.where("articleId").is(new ObjectId(articleId))), ArticleDetail.class);

            return true;
        }
        return false;
    }

    @Override
    public List<String> getArticleCategory() {
        List<String> articleCategories = new ArrayList<>();
        for (ArticleCategoryDefine articleCategoryDefine : ArticleCategoryDefine.values()) {
            articleCategories.add(articleCategoryDefine.name());
        }
        return articleCategories;
    }

    @Override
    public Paging<Article> getArticleByRefCategory(QueryArticleRefCategoryPagingParam param) {
        if (param == null) {
            return null;
        }
        //
        Criteria ca = new Criteria();
        if (param.getRefCategory() != null) {
            ca.and("refCategory").is(param.getRefCategory());
        }
        if (!StringUtils.isEmpty(param.getCompanyId())) {
            ca.and("companyId").is(param.getCompanyId());
        }
        //分页
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(match(ca));
        Paging<Article> articlePaging = Paging.compute(mongoTemplate, Article.class, operations, param);

        operations.add(sort(Sort.by(Sort.Direction.DESC, "gmtCreated")));

        operations.add(skip((long)(param.getPagination() * param.getPageSize())));
        operations.add(limit(param.getPageSize()));
        List<Article> articles = mongoTemplate.aggregate(Aggregation.newAggregation(operations), "yof_article",
                                                         Article.class).getMappedResults();
        //查询文章
        List<ObjectId> objectIds = new ArrayList<>();
        for (Article article : articles) {
            objectIds.add(new ObjectId(article.getId()));
        }
        Criteria caDetail = new Criteria();
        caDetail.and("articleId").in(objectIds);

        Query query = new Query();
        query.addCriteria(caDetail);
        List<ArticleDetail> articleActionRecords = mongoTemplate.find(query, ArticleDetail.class);
        Map<String, ArticleDetail> articleDetailmap = articleActionRecords.stream().collect(
            Collectors.toMap(ArticleDetail::getArticleId, t -> t));
        for (Article article : articles) {
            article.setArticleDetail(articleDetailmap.get(article.getId()));
        }
        articlePaging.setData(articles);
        return articlePaging;
    }

    @Override
    public Article getNextArticle(String articleId, boolean next, String articleCategory, String companyId,
                                  RefCategory refCategory) {
        if (StringUtils.isEmpty(articleId)) {
            return null;
        }
        List<AggregationOperation> operations = new ArrayList<>();

        Criteria ca = new Criteria();
        if (!StringUtils.isEmpty(companyId)) {
            ca.and("companyId").is(companyId);
        }

        //要区分分类
        if (!StringUtils.isEmpty(articleCategory) && !articleCategory.equals("undefined")) {
            ca.and("articleCategory").is(articleCategory);
        }
        if (refCategory != null) {
            ca.and("refCategory").is(refCategory);
        }
        // 表示查询下一条
        if (next) {
            // 按 id 排序，大于参数 id 值的第一条就是所谓的下一条
            ca.and("_id").gt(new ObjectId(articleId));
            operations.add(sort(Sort.by(Sort.Direction.ASC, "_id")));

        } else {
            // 表示查询下一条
            // 按 id 排序，小于参数 id 值的第一条就是所谓的上一条
            ca.and("_id").lt(new ObjectId(articleId));
            operations.add(sort(Sort.by(Sort.Direction.DESC, "_id")));

        }
        operations.add(match(ca));
        operations.add(limit(1));
        List<Article> articles = mongoTemplate.aggregate(Aggregation.newAggregation(operations), "art_article",

                                                         Article.class).getMappedResults();
        Article article = null;
        if (CollectionUtils.isEmpty(articles)) {
            // 如果查不到上一条或下一条，则进行普通的条件查询一条记录即可
            Criteria criteria = new Criteria();
            if (!StringUtils.isEmpty(companyId)) {
                criteria.and("companyId").is(companyId);
            }
            if (refCategory != null) {
                criteria.and("refCategory").is(refCategory);
            }
            List<AggregationOperation> operationsn = new ArrayList<>();
            if (!StringUtils.isEmpty(articleCategory) && !articleCategory.equals("undefined")) {
                criteria.and("articleCategory").is(articleCategory);
            }
            operationsn.add(match(criteria));
            operationsn.add(limit(1));
            List<Article> articleList = mongoTemplate.aggregate(Aggregation.newAggregation(operationsn), "art_article",

                                                                Article.class).getMappedResults();
            if (next) {
                if (!CollectionUtils.isEmpty(articleList)) {
                    article = articleList.get(0);
                }

            } else {
                if (!CollectionUtils.isEmpty(articleList)) {
                    article = articleList.get(articleList.size() - 1);
                }

            }
        } else {
            // 从结果中取一条即可
            article = articles.get(0);
        }

        return article;
    }

}
