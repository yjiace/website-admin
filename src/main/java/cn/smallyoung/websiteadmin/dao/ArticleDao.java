package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.Article;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 */
public interface ArticleDao  extends BaseDao<Article, String> {


    /**
     * 查询所有有效的文章列表
     * @return 所有有效的文章列表
     */
    @Query(value = "from Article a where a.status = 'Y' and a.isDelete = 'N' and a.htmlContent is not null")
    List<Article> findEffectiveArticle();

    /**
     * 根据ID列表查询文章信息
     *
     * @param ids id列表
     * @return 查询到的文章列表
     */
    List<Article> findByIdIn(List<String> ids);
}
