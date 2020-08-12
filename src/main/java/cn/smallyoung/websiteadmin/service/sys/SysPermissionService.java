package cn.smallyoung.websiteadmin.service.sys;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.sys.SysPermissionDao;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author smallyoung
 * @date 2020/7/26
 */

@Service
@Transactional(readOnly = true)
public class SysPermissionService extends BaseService<SysPermission, Long> {

    @Resource
    private SysPermissionDao sysPermissionDao;

}
