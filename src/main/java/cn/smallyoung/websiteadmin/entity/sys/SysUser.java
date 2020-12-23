package cn.smallyoung.websiteadmin.entity.sys;

import cn.smallyoung.websiteadmin.base.BaseEntity;
import cn.smallyoung.websiteadmin.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/26
 */
@Getter
@Setter
@Entity
@Table(name = "t_sys_user")
@Where(clause = " is_delete = 'N' ")
public class SysUser extends BaseEntity implements Serializable, UserDetails {


    private static final long serialVersionUID = -1419339429097967881L;

    /**
     * 用户名
     */
    @Id
    @Column(name = "username" )
    private String username;
    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password" )
    private String password;
    /**
     * 昵称
     */
    @Column(name = "nick_name" )
    private String nickName;
    /**
     * 手机号
     */
    @Column(name = "phone" )
    private String phone;
    /**
     * 性别,1男；2女
     */
    @Column(name = "sex" )
    private Integer sex;

    /**
     * 状态，Y正常，N冻结
     */
    @Column(name = "status" )
    @DataName(name = "状态")
    private String status;

    @JsonIgnore
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "t_sys_user_role", joinColumns = {@JoinColumn(name = "username")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<SysRole> roles = new ArrayList<>();

    @Transient
    private List<String> role = new ArrayList<>();

    public List<String> getRole() {
        return this.roles.stream().map(SysRole::getName).collect(Collectors.toList());
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(SysRole::getSysPermissions)
                .flatMap(Collection::stream)
                .filter(p -> "N".equals(p.getIsDelete()))
                .map(SysPermission::getVal).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * 帐号是否不过期，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 帐号是否不锁定，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否不过期，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 该帐号是否启用，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }

}
