package cn.smallyoung.websiteadmin.dao;


import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.MessageNotification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageNotificationDao extends BaseDao<MessageNotification, String> {

    @Query(value = "select count(*) from t_message_notification where recipient_username = ?1 and status = 'unread' and is_delete = 'N' ", nativeQuery = true)
    Integer countByRecipientUsername(String recipientUsername);

    @Modifying
    @Query(value = "update t_message_notification set status = 'Read' where recipient_username = ?1 and status = 'unread' ", nativeQuery = true)
    Integer markReadWithOneClick(String username);

    List<MessageNotification> findByIdIn(List<String> ids);
}
