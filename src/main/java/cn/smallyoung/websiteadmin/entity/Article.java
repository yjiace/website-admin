package cn.smallyoung.websiteadmin.entity;

import cn.hutool.core.lang.Dict;
import cn.smallyoung.websiteadmin.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Getter
@Setter
@Entity
@Table(name = "t_article")
@Where(clause = " is_delete = 'N' ")
public class Article extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 9144829538735195054L;

    @Id
    @Column(name = "id" )
    private String id;
    /**
     * 标题
     */
    @Column(name = "title" )
    private String title;

    /**
     * Markdown 内容
     */
    @JsonIgnore
    @Column(name = "md_content" )
    private String mdContent;

    /**
     * 转换的页面内容
     */
    @JsonIgnore
    @Column(name = "html_content" )
    private String htmlContent;

    /**
     * 权重
     */
    @Column(name = "weight" )
    private Integer weight;

    /**
     * 分类
     */
    @JoinColumn(name = "category_id")
    @ManyToOne
    private Category category;

    /**
     * 封面图片地址
     */
    @Column(name = "cover_url" )
    private String coverUrl;

    /**
     * 简介
     */
    @Column(name = "introduction" )
    private String introduction;

    /**
     * 标签
     */
    @Column(name = "tags" )
    private String tags;

    /**
     * 状态，Y:上线，N：下线
     */
    @Column(name = "status" )
    private String status;

    /**
     * 是否推荐，Y:推荐，N：不推荐
     */
    @Column(name = "recommend" )
    private String recommend;

    public Map<String, Object> toMap(){
        return Dict.create().set("id", this.id).set("title", this.title)
                .set("category", this.category.toMap()).set("coverUrl", this.coverUrl)
                .set("introduction", this.introduction).set("tags", this.tags == null ? null : this.tags.split(","))
                .set("recommend", this.recommend)
                .set("createTime", this.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .set("updateTime", this.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}


