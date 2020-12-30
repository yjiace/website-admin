package cn.smallyoung.websiteadmin.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/12/25
 */
@Getter
@Setter
@ToString
public class SysPermissionVO implements Serializable {

    private static final long serialVersionUID = 1816312146122783641L;

    private String id;
    /**
     * 上级权限
     */
    private String parentId;
    /**
     * 权限名
     */
    private String name;
    /**
     * 权限值
     */
    private String val;

    /**
     * 跳转路径
     */
    private String jumpPath;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * 图标
     */
    private String icon;
    /**
     * 菜单排序编号
     */
    private Integer orderNumber;
    /**
     * 权限类型：0菜单，1按钮
     */
    private Integer type;
}
