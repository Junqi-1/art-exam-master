package com.youkeda.application.art.article.param;
import com.youkeda.application.art.article.model.RefCategory;
import com.youkeda.model.BasePagingParam;

/**
 * @Author
 * @DATE 2020-03-27
 */
public class QueryArticleRefCategoryPagingParam extends BasePagingParam {
    // 关联活动类型
    private RefCategory refCategory;

    // 公司id
    private String companyId;

    public RefCategory getRefCategory() {
        return refCategory;
    }

    public void setRefCategory(RefCategory refCategory) {
        this.refCategory = refCategory;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
