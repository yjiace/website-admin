package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 */

public interface CategoryDao extends BaseDao<Category, String> {

    /**
     * 根据ID列表查询
     *
     * @param ids id列表
     * @return 查询到的列表
     */
    List<Category> findByIdInOrderByWeightDescCreateTimeDesc(List<String> ids);
    /**
     * 根据ID列表删除文章
     *
     * @param ids id列表
     * @return 删除条数
     */
    @Modifying
    @Query(value = "update t_category set is_delete = 'Y', update_time = now() where id in ?1 ", nativeQuery = true)
    Integer updateIsDeleteByIdIn(List<String> ids);

}
