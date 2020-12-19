package cn.smallyoung.websiteadmin.vo;

import cn.smallyoung.websiteadmin.entity.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/12/19
 */
@Getter
@Setter
@ToString
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = -6817718850342743418L;

    /**
     * ID
     */
    private String id;
    /**
     * 标题
     */
    private String title;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 分类
     */
    private Category category;

    /**
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 标签
     */
    private String tags;
}
