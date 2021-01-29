package cn.smallyoung.websiteadmin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2021/1/29
 */
@Getter
@Setter
@Entity
@Table(name = "t_note")
public class Note {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "content")
    private String content;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updateTime;
}
