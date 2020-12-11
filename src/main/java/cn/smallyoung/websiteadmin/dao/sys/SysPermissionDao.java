package cn.smallyoung.websiteadmin.dao.sys;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;

import java.util.List;

/**
 * @author yangn
 */
public interface SysPermissionDao extends BaseDao<SysPermission, Long> {

    List<SysPermission> findByIdInAndIsDelete(List<Long> idList, String isDelete);

}
