package cn.smallyoung.websiteadmin.dao;

import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.SysUser;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 * @date 2020/7/26
 */
public interface SysUserDao extends BaseDao<SysUser, String> {

    /**
     * 根据用户昵称查询用户
     *
     * @param username 用户名
     * @return 查询到的用户实体
     */
    @Query("select u from SysUser u where u.username = ?1 and u.status = 'Y' and u.isDelete = 'N' ")
    SysUser findEffectiveByUsername(String username);

    /**
     * 根据用户昵称查询用户
     *
     * @param username 用户名
     * @return 查询到的用户实体
     */
    SysUser findByUsername(String username);

    /**
     * 根据用户名、状态、删除标识查询用户列表
     *
     * @param usernames 用户名
     * @param status    用户账号状态
     * @param isDelete  删除标识
     * @return 查询到的用户列表
     */
    List<SysUser> findByUsernameInAndStatusAndIsDelete(List<String> usernames, String status, String isDelete);

}
