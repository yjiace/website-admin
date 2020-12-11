package cn.smallyoung.websiteadmin.dao.sys;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;

import java.util.List;

/**
 * @author yangn
 */
public interface SysPermissionDao extends BaseDao<SysPermission, String> {

    List<SysPermission> findByIdInAndIsDelete(List<String> idList, String isDelete);

}
