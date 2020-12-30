package cn.smallyoung.websiteadmin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志
 *
 * @author smallyoung
 * @data 2020/11/4
 */
@Getter
@Setter
@Entity
@Table(name = "t_sys_operation_log")
@EntityListeners({AuditingEntityListener.class})
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 2415594410963135575L;

    @Id
    @Column(name = "id" )
    private String id;

    /**
     * 操作人
     */
    @CreatedBy
    @Column(name = "username" )
    private String username;

    /**
     * 操作模块
     */
    @Column(name = "module" )
    private String module;

    /**
     * 操作类型
     */
    @Column(name = "method" )
    private String method;

    /**
     * 请求参数
     */
    @Column(name = "params" )
    private String params;

    @Column(name = "package_and_method" )
    private String packageAndMethod;

    /**
     * 操作前的数据
     */
    @Column(name = "before_data" )
    private String beforeData;

    /**
     * 操作后的数据
     */
    @Column(name = "after_data" )
    private String afterData;

    /**
     * 说明
     */
    @Column(name = "content" )
    private String content;

    /**
     * 开始时间
     */
    @Column(name = "start_time" )
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time" )
    private LocalDateTime endTime;

    /**
     * 操作状态
     */
    @Column(name = "result_status" )
    private String resultStatus;

    /**
     * 操作结果
     */
    @Column(name = "result_msg" )
    private String resultMsg;

}


