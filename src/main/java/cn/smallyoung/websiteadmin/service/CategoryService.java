package cn.smallyoung.websiteadmin.service;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.CategoryDao;
import cn.smallyoung.websiteadmin.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Service
@Transactional(readOnly = true)
public class CategoryService extends BaseService<Category, Long> {

    @Resource
    private CategoryDao categoryDao;

    public List<Map<String, Object>> findAllCategory() {
        List<Category> categories = this.findAll();
        return categories.stream().map(Category::toMap).collect(Collectors.toList());
    }
}
