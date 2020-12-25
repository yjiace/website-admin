package cn.smallyoung.websiteadmin.entity.sys;

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
 * @date 2020/7/26
 */
@Getter
@Setter
@Entity
@Table(name = "t_sys_permission")
@Where(clause = " is_delete = 'N' ")
public class SysPermission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4357934206294056837L;

    @Id
    @Column(name = "id" )
    private String id;
    /**
     * 上级权限
     */
    @Column(name = "parent_id" )
    private String parentId;
    /**
     * 权限名
     */
    @Column(name = "name" )
    private String name;
    /**
     * 权限值
     */
    @Column(name = "val" )
    private String val;

    /**
     * 权限路径
     */
    @Column(name = "url" )
    private String url;

    /**
     * 图标
     */
    @Column(name = "icon" )
    private String icon;
    /**
     * 菜单排序编号
     */
    @Column(name = "order_number" )
    private Integer orderNumber;
    /**
     * 权限类型：0菜单，1按钮
     */
    @Column(name = "type" )
    private Integer type;

}
