package cn.smallyoung.websiteadmin.entity;

import cn.smallyoung.websiteadmin.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2021/1/2
 */

@Getter
@Setter
@Entity
@Table(name = "t_website")
@Where(clause = " is_delete = 'N' ")
public class Website  extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7908505236355220083L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "key")
    private String key;

    @Column(name = "data")
    private String data;

    @Column(name = "file_name")
    private String fileName;
}
