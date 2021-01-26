package cn.smallyoung.websiteadmin.entity;

import cn.smallyoung.websiteadmin.base.BaseEntity;
import cn.smallyoung.websiteadmin.interfaces.DataName;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/7/26
 */
@Getter
@Setter
@Entity
@Table(name = "t_sys_role")
@Where(clause = " is_delete = 'N' ")
public class SysRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -2203702702813478824L;

    @Id
    @Column(name = "id" )
    private String id;

    @Column(name = "name" )
    private String name;
    /**
     * 备注
     */
    @Column(name = "comments" )
    private String comments;

    @DataName(name = "权限")
    @Where(clause = " is_delete = 'N' ")
    @OrderBy(value = " order_number desc ")
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private List<SysPermission> sysPermissions = new ArrayList<>();
}
