package cn.smallyoung.websiteadmin.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author smallyoung
 * @data 2020/12/20
 */
@Getter
@Setter
@ToString
public class SysUserVO {

    private String username;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 性别,1男；2女
     */
    private Integer sex;
}
