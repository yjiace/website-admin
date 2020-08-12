package cn.smallyoung.websiteadmin.service.sys;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.sys.SysUserDao;
import cn.smallyoung.websiteadmin.entity.sys.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author smallyoung
 * @date 2020/7/26
 */
@Service
@Transactional(readOnly = true)
public class SysUserService extends BaseService<SysUser, Long> {

    @Resource
    private SysUserDao sysUserDao;

}
