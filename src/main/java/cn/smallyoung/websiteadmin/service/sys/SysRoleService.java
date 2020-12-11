package cn.smallyoung.websiteadmin.service.sys;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.sys.SysRoleDao;
import cn.smallyoung.websiteadmin.entity.sys.SysRole;
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
public class SysRoleService extends BaseService<SysRole, String> {

    @Resource
    private SysRoleDao sysRoleDao;

    @Override
    public SysRole findOne(String id){
        return super.findOne(id);
    }

    /**
     * 根据id集合查询所有角色
     *
     * @param idList 角色id列表
     * @return 角色对象列表
     */
    public List<SysRole> findByIdInAndIsDelete(List<String> idList) {
        return sysRoleDao.findByIdInAndIsDelete(idList, "N");
    }

}
