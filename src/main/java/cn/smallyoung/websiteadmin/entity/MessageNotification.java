package cn.smallyoung.websiteadmin.entity;

import cn.smallyoung.websiteadmin.interfaces.DataName;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息通知
 * @author smallyoung
 * @data 2020/11/14
 */
@Getter
@Setter
@Entity
@Table(name = "t_message_notification")
@Where(clause = " is_delete = 'N' ")
@EntityListeners({AuditingEntityListener.class})
public class MessageNotification implements Serializable {

    private static final long serialVersionUID = 3133450187488906036L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id" )
    private String id;

    /**
     * 发起人用户名
     */
    @DataName(name = "发起人用户名")
    @Column(name = "initiator_username" )
    private String initiatorUsername;

    /**
     * 接收人用户名
     */
    @DataName(name = "接收人用户名")
    @Column(name = "recipient_username" )
    private String recipientUsername;

    /**
     * 状态，Read：已读；unread：未读
     */
    @DataName(name = "状态")
    @Column(name = "status" )
    private String status;

    /**
     * 消息通知内容
     */
    @DataName(name = "消息通知内容")
    @Column(name = "content" )
    private String content;

    /**
     * 消息来源
     */
    @DataName(name = "消息来源")
    @Column(name = "source" )
    private String source;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @DataName(name = "阅读时间")
    @Column(name = "reading_time")
    private LocalDateTime readingTme;

    /**
     * N正常，Y删除
     */
    @DataName(name = "删除标识")
    @Column(name = "is_delete")
    private String isDelete;
}
