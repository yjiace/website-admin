package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.SysRole;

import java.util.List;

/**
 * @author yangn
 */
public interface SysRoleDao extends BaseDao<SysRole, String> {

    /**
     * 根据ID、状态查询角色列表
     *
     * @param idList   id列表
     * @param isDelete 删除状态
     * @return 查询的角色列表
     */
    List<SysRole> findByIdInAndIsDelete(List<String> idList, String isDelete);
}
