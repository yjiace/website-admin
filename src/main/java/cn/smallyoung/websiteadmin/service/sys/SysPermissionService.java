package cn.smallyoung.websiteadmin.service.sys;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.sys.SysPermissionDao;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/7/26
 */

@Service
@Transactional(readOnly = true)
public class SysPermissionService extends BaseService<SysPermission, String> {

    @Resource
    private SysPermissionDao sysPermissionDao;

    /**
     * 根据id集合查询所有权限
     *
     * @param idList 权限id列表
     * @return 权限对象列表
     */
    public List<SysPermission> findByIdInAndIsDelete(List<String> idList) {
        return sysPermissionDao.findByIdInAndIsDelete(idList, "N");
    }

}
