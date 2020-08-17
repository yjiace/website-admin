package cn.smallyoung.websiteadmin.service;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.CategoryDao;
import cn.smallyoung.websiteadmin.entity.Category;
import cn.smallyoung.websiteadmin.util.ConvertBean;
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
public class CategoryService extends BaseService<Category, String> {

    @Resource
    private CategoryDao categoryDao;

    public List<Map<String, Object>> findAllCategory() {
        List<Category> categories = this.findAll();
        return categories.stream().map(category -> ConvertBean.bean2Map(Category.class, "id","name","backgroundColor","count")).collect(Collectors.toList());
    }
}
