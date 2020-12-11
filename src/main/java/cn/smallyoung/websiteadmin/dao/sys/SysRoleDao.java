package cn.smallyoung.websiteadmin.dao.sys;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.sys.SysRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author yangn
 */
public interface SysRoleDao extends BaseDao<SysRole, Long> {

    /**
     * 修改角色状态
     *
     * @param id       需要修改的角色id
     * @param isDelete 修改的删除字段标识
     * @return 修改成功条数
     */
    @Modifying
    @Query("update SysRole r set r.isDelete=?2 where r.id=?1")
    Integer updateStatus(Long id, String isDelete);

    List<SysRole> findByIdInAndIsDelete(List<Long> idList, String isDelete);
}
