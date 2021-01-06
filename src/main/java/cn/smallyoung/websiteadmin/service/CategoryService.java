package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.CategoryDao;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryService extends BaseService<Category, String> {

    @Value("${website.catalog}")
    private String dirPath;
    @Resource
    private CategoryDao categoryDao;

    public List<Map<String, Object>> findAllCategory() {
        List<Category> categories = this.findAll();
        return categories.stream().map(Category::toMap).collect(Collectors.toList());
    }

    public void staticCategory(String id){
        List<Category> categories = new ArrayList<>();
        if (StrUtil.isNotBlank(id)){
            Optional<Category> optional = this.findById(id);
            if(!optional.isPresent()){
                String error = String.format("根据ID【%s】没有找到该分类", id);
                log.error(error);
                throw new UsernameNotFoundException(error);
            }
            categories.add(optional.get());
        }else {
            categories = categoryDao.findAll();
        }
        TemplateEngine engine;
        Template template;
        String result;
        FileWriter writer;
        List<Article> articles;
        List<Article> nowPage;
        int pageSize = 12;
        int page;
        for(Category c : categories){
            articles = c.getArticles();
            if(CollectionUtil.isEmpty(articles)){
                continue;
            }
            articles = articles.stream().sorted(Comparator.comparing(Article::getWeight).reversed())
                    .sorted(Comparator.comparing(Article::getCreateTime).reversed()).collect(Collectors.toList());
            page = articles.size() / pageSize;
            if(articles.size() % pageSize != 0){
                page++;
            }
            for(int i = 1; i <= page; i++){
                nowPage = ListUtil.page(i - 1, pageSize, articles);
                engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
                template = engine.getTemplate("category.html");
                result = template.render(Dict.create().set("category", c)
                        .set("pageNo", i).set("page", page)
                        .set("data", nowPage.stream().map(Article::toMap).collect(Collectors.toList()))
                        .set("categories", categories.stream().map(Category::toMap).collect(Collectors.toList())));
                writer = new FileWriter(dirPath + c.getId() + File.separator + i + ".html", "UTF-8");
                writer.write(result);
            }
        }
    }
}
