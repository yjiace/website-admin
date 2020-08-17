package cn.smallyoung.websiteadmin.entity;

import cn.smallyoung.websiteadmin.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 类目
 * @author smallyoung
 * @date 2020/7/25
 */
@Getter
@Setter
@Entity
@Table(name = "t_category")
public class Category extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -6096659055897729110L;

    @Id
    @Column(name = "id" )
    private String id;

    /**
     * 类目名称
     */
    @Column(name = "name" )
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy="category")
    private List<Article> articles;

    @Column(name = "background_color")
    private String backgroundColor;

    @Transient
    private Integer count;
}
