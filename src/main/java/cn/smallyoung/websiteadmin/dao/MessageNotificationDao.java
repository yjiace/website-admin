package cn.smallyoung.websiteadmin.dao;


import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.MessageNotification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 */
public interface MessageNotificationDao extends BaseDao<MessageNotification, String> {

    /**
     * 查询用户未读数量
     *
     * @param recipientUsername 接收消息用户名
     * @return 未读数量
     */
    @Query(value = "select count(*) from t_message_notification where recipient_username = ?1 and status = 'unread' and is_delete = 'N' ", nativeQuery = true)
    Integer countByRecipientUsername(String recipientUsername);

    /**
     * 标记已读
     *
     * @param username 接收消息用户名
     * @return 修改数据库数量
     */
    @Modifying
    @Query(value = "update t_message_notification set status = 'Read' where recipient_username = ?1 and status = 'unread' ", nativeQuery = true)
    Integer markReadWithOneClick(String username);

    /**
     * 根据ID列表查询信息
     *
     * @param ids id列表
     * @return 查询到的信息列表
     */
    List<MessageNotification> findByIdIn(List<String> ids);

    /**
     * 根据ID列表删除文章
     *
     * @param ids id列表
     * @return 删除条数
     */
    @Modifying
    @Query(value = "update t_message_notification set is_delete = 'Y' where id in ?1 ", nativeQuery = true)
    Integer updateIsDeleteByIdIn(List<String> ids);
}
