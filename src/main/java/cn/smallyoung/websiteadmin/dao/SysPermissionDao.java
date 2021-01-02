package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.SysPermission;

import java.util.List;

/**
 * @author smallyoung
 */
public interface SysPermissionDao extends BaseDao<SysPermission, String> {

    /**
     * 根据id、状态查询权限列表
     *
     * @param idList   id列表
     * @param isDelete 状态标识
     * @return 查询的权限列表
     */
    List<SysPermission> findByIdInAndIsDelete(List<String> idList, String isDelete);

}
