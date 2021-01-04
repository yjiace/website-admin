package cn.smallyoung.websiteadmin.controller;

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
     * 保存
     *
     * @param id              类型ID
     * @param name            类型名称
     * @param backgroundColor 类型背景颜色
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
     * 删除类型
     *
     * @param id 类型ID
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ROLE_CATEGORY_DEL')")
    public void delete(String id) {
        Optional<Category> optional = categoryService.findById(id);
        if (!optional.isPresent()) {
            String error = String.format("根据ID【%s】没有找到该分类", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        Category category = optional.get();
        category.setIsDelete("Y");
        categoryService.save(category);
    }

    @GetMapping("staticCategory")
    public void staticCategory(@RequestParam(defaultValue = "")  String id){
        categoryService.staticCategory(id);
    }
}
