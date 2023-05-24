package com.youkeda.application.art.article.api;

import com.google.common.collect.Lists;
import com.youkeda.application.art.article.model.Article;
import com.youkeda.application.art.article.model.RefCategory;
import com.youkeda.application.art.article.param.QueryArticlePagingParam;
import com.youkeda.application.art.article.param.QueryArticleRefCategoryPagingParam;
import com.youkeda.application.art.article.service.ArticleService;
import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.model.Result;
import com.youkeda.application.art.member.util.MemberUtil;
import com.youkeda.model.Company;
import com.youkeda.model.Paging;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author jiaoheng
 * @DATE 2020-03-16
 */
@Controller
@RequestMapping(path = "/api/article")
public class ArticleApi {
    @Autowired
    private ArticleService articleService;

    /**
     * 添加或更新文章
     *
     * @return Result
     */
    @PostMapping(path = "/save")
    @ResponseBody
    @NeedLogin
    public Result<Article> save(@SessionAttribute(value = MemberUtil.LOGIN_KEY) LoginUser loginUser,
                                @RequestBody Article article) {
        Result<Article> result = new Result<>();

        if (article == null) {
            result.setMessage("缺少参数");
            return result;
        }

        article.setCompanyId(loginUser.getCompanyId());
        article.setAccountId(loginUser.getAccountId());

        result.setData(articleService.saveArticle(article).getData());
        result.setSuccess(true);
        return result;
    }

    /**
     * 分页查询文章
     *
     * @return Result
     */
    @GetMapping(path = "/paging")
    @ResponseBody
    public Result<Paging<Article>> queryArticle(QueryArticlePagingParam param,
                                                @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false)
                                                    LoginUser loginUser) {

        Result<Paging<Article>> result = new Result<>();
        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (loginUser == null) {
            param.setCompanyId(Company.DEFAULT.getId());
        } else {
            param.setCompanyId(loginUser.getCompanyId());
            param.setAccountId(loginUser.getAccountId());
        }

        result.setData(articleService.queryPagingArticle(param));
        result.setSuccess(true);
        return result;
    }

    /**
     * 点击下一篇文章
     *
     * @return Result
     */
    @GetMapping(path = "/getnextarticle")
    @ResponseBody
    public Result<Article> getNextArticle(
        @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false) LoginUser loginUser,
        @RequestParam("articleId") String articleId,
        @RequestParam(value = "companyId", required = false) String companyId,
        @RequestParam(value = "articleCategory", required = false) String articleCategory,
        @RequestParam(value = "refCategory", required = false) RefCategory refCategory) {

        Result<Article> result = new Result<>();

        if (StringUtils.isEmpty(articleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        if (StringUtils.isEmpty(companyId)) {
            if (loginUser != null) {
                companyId = loginUser.getCompanyId();
            } else {
                companyId = Company.DEFAULT.getId();
            }
        }

        result.setData(articleService.getNextArticle(articleId, true, articleCategory, companyId, refCategory));
        result.setSuccess(true);
        return result;
    }

    /**
     * 点击上一篇文章
     *
     * @return Result
     */
    @GetMapping(path = "/getpreviousarticle")
    @ResponseBody
    public Result<Article> getPreviousArticle(
        @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false) LoginUser loginUser,
        @RequestParam("articleId") String articleId,
        @RequestParam(value = "refCategory", required = false) RefCategory refCategory,
        @RequestParam(value = "companyId", required = false) String companyId,
        @RequestParam(value = "articleCategory", required = false) String articleCategory) {

        Result<Article> result = new Result<>();

        if (StringUtils.isEmpty(articleId)) {
            result.setMessage("缺少参数");
            return result;
        }

        if (StringUtils.isEmpty(companyId)) {
            if (loginUser != null) {
                companyId = loginUser.getCompanyId();
            } else {
                companyId = Company.DEFAULT.getId();
            }
        }

        result.setData(articleService.getNextArticle(articleId, false, articleCategory, companyId, refCategory));
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取单个文章详情
     *
     * @return Result
     */
    @GetMapping(path = "/getarticle")
    @ResponseBody
    public Result<Article> getArticle(@RequestParam("id") String id) {
        Result<Article> result = new Result<>();
        if (StringUtils.isEmpty(id)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(articleService.getArticle(id, true));
        result.setSuccess(true);
        return result;
    }

    /**
     * 删除文章
     *
     * @return Result
     */
    @NeedLogin
    @PostMapping(path = "/delete")
    @ResponseBody
    public Result<Boolean> delete(@RequestParam("id") String id) {
        Result<Boolean> result = new Result<>();
        if (StringUtils.isEmpty(id)) {
            result.setMessage("缺少参数");
            return result;
        }

        result.setData(articleService.delete(id));
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取所有文章类型
     *
     * @return Result
     */
    @GetMapping(path = "/getarticlecategory")
    @ResponseBody
    public Result<List<String>> getArticleCategory() {
        Result<List<String>> result = new Result<>();
        result.setData(articleService.getArticleCategory());
        result.setSuccess(true);

        return result;
    }

    /**
     * 根据refCategory获取文章
     *
     * @return Result
     */
    @GetMapping(path = "/getarticlebyrecategory")
    @ResponseBody
    public Result<Paging<Article>> getArticleByRefCategory(
        @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false) LoginUser loginUser,
        QueryArticleRefCategoryPagingParam param) {
        Result<Paging<Article>> result = new Result<>();

        if (param == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (loginUser != null) {
            param.setCompanyId(loginUser.getCompanyId());
        }

        if (StringUtils.isEmpty(param.getCompanyId())) {
            param.setCompanyId(Company.DEFAULT.getId());
        }

        result.setData(articleService.getArticleByRefCategory(param));
        result.setSuccess(true);

        return result;
    }

    /**
     * 根据refCategory获取文章
     *
     * @return Result
     */
    @GetMapping(path = "/getarticlebyrecategorylist")
    @ResponseBody
    public Result<List<Article>> getArticleByRefCategorys(
        @SessionAttribute(value = MemberUtil.LOGIN_KEY, required = false) LoginUser loginUser,
        @RequestParam("refCategory") RefCategory refCategory,
        @RequestParam(value = "companyId", required = false) String companyId) {
        Result<List<Article>> result = new Result<>();
        if (refCategory == null) {
            result.setMessage("缺少参数");
            return result;
        }

        if (loginUser != null) {
            companyId = loginUser.getCompanyId();
        }

        if (StringUtils.isEmpty(companyId)) {
            companyId = Company.DEFAULT.getId();
        }

        QueryArticleRefCategoryPagingParam param = new QueryArticleRefCategoryPagingParam();
        param.setRefCategory(refCategory);
        param.setCompanyId(companyId);
        param.setPageSize(100);
        param.setPagination(1);

        Paging<Article> articlePageing = articleService.getArticleByRefCategory(param);
        if (articlePageing != null) {
            result.setData(articlePageing.getData());
        } else {
            result.setData(Lists.newArrayList());
        }

        result.setSuccess(true);

        return result;
    }

}
