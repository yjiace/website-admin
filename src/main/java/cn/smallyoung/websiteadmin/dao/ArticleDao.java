package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.Article;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 */
public interface ArticleDao extends BaseDao<Article, String> {


    /**
     * 查询所有有效的文章列表
     *
     * @return 所有有效的文章列表
     */
    @Query(value = "from Article a where a.status = 'Y' and a.isDelete = 'N' and a.htmlContent is not null")
    List<Article> findEffectiveArticle();

    /**
     * 根据ID列表删除文章
     *
     * @param ids id列表
     * @return 删除条数
     */
    @Modifying
    @Query(value = "update t_article set is_delete = 'Y', update_time = now() where id in ?1 ", nativeQuery = true)
    Integer updateIsDeleteByIdIn(List<String> ids);

    /**
     * 根据ID列表删除文章
     *
     * @param ids      id列表
     * @param status   上线状态
     * @param isDelete 删除状态
     * @return 查询到的文章
     */
    List<Article> findByIdInAndStatusAndIsDelete(List<String> ids, String status, String isDelete);
}
