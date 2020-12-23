package cn.smallyoung.websiteadmin.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/12/22
 */
@Getter
@Setter
@ToString
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 1131549114132128731L;

    private String id;

    private String name;
    /**
     * 备注
     */
    private String comments;
}
