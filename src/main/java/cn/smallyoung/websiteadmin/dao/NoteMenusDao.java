package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.NoteMenus;

import java.util.List;

/**
 * @author smallyoung
 */
public interface NoteMenusDao extends BaseDao<NoteMenus, String> {

    /**
     * 根据id和用户id查询笔记菜单
     *
     * @param id       笔记菜单的id
     * @param userId   用户id
     * @param isDelete 删除标识
     * @return 查询到的笔记菜单
     */
    NoteMenus findByIdAndUserIdAndIsDelete(String id, String userId, String isDelete);

    /**
     * 根据用户id查询用户笔记菜单
     * @param userId  用户id
     * @return  该用户的笔记菜单
     */
    List<NoteMenus> findByUserIdOrderByCreateTimeDesc(String userId);
}
