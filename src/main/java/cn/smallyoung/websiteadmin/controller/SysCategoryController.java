package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Category;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 文章类别
 *
 * @author smallyoung
 * @data 2020/12/31
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/category")
public class SysCategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 查询所有分类
     */
    @GetMapping("findAll")
    @PreAuthorize("hasRole('ROLE_CATEGORY')")
    public List<Category> findAll() {
        return categoryService.findAll();
    }


    /**
     * 根据ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章对象
     */
    @GetMapping("/findById")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public Category findById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        Optional<Category> optional = categoryService.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        log.error("获取文章对象失败");
        throw new RuntimeException("获取文章对象失败");
    }


    /**
     * 保存
     *
     * @param id              分类ID
     * @param name            分类名称
     * @param backgroundColor 分类背景颜色
     */
    @PostMapping("save")
    @PreAuthorize("hasRole('ROLE_CATEGORY_SAVE')")
    public void save(String id, String name, String backgroundColor) {
        if (StrUtil.hasBlank(name, backgroundColor)) {
            throw new NullPointerException("参数错误");
        }
        Category category = new Category();
        if (StrUtil.isNotBlank(id)) {
            Optional<Category> optional = categoryService.findById(id);
            if (!optional.isPresent()) {
                String error = String.format("根据ID【%s】没有找到该分类", id);
                log.error(error);
                throw new UsernameNotFoundException(error);
            }
            category = optional.get();
        } else {
            category.setIsDelete("N");
        }
        category.setName(name);
        category.setBackgroundColor(backgroundColor);
        categoryService.save(category);
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ROLE_CATEGORY_DEL')")
    public Integer delete(@RequestParam(value = "ids") List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw new NullPointerException("参数错误");
        }
        return categoryService.updateIsDeleteByIdIn(ids);
    }

    /**
     * 根据id初始化
     * @param ids 分类ID
     */
    @GetMapping("staticCategory")
    public void staticCategory(@RequestParam(value = "ids") List<String> ids){
        categoryService.staticCategory(ids);
    }
}
