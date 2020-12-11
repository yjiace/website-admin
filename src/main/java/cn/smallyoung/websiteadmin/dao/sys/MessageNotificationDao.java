package cn.smallyoung.websiteadmin.dao.sys;


import cn.smallyoung.websiteadmin.base.BaseDao;
import cn.smallyoung.websiteadmin.entity.sys.MessageNotification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageNotificationDao extends BaseDao<MessageNotification, Long> {

    @Query(value = "select count(*) from t_message_notification where recipient_username = ?1 and status = 'unread' and is_delete = 'N' order by create_time desc ", nativeQuery = true)
    Long countByRecipientUsername(String recipientUsername);

    @Modifying
    @Query(value = "update t_message_notification set status = 'Read' where recipient_username = ?1 and status = 'unread' ", nativeQuery = true)
    Integer markReadWithOneClick(String username);

    List<MessageNotification> findByIdIn(List<Long> ids);
}
