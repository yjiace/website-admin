package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Comparator;
import java.util.List;
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

    @Transactional(rollbackFor = Exception.class)
    public Integer updateIsDeleteByIdIn(List<String> ids){
        return categoryDao.updateIsDeleteByIdIn(ids);
    }

    public void staticCategory(List<String> ids){
        List<Category> categories = CollUtil.isNotEmpty(ids) ? categoryDao.findByIdInOrderByWeightDescCreateTimeDesc(ids) :
                categoryDao.findAll(Sort.by(Sort.Direction.DESC, "weight","createTime"));
        if(CollUtil.isEmpty(categories)){
            String error = String.format("根据ID【%s】没有找到该分类", ids);
            log.error(error);
            throw new UsernameNotFoundException(error);
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
                        .set("categories", categories));
                writer = new FileWriter(dirPath + c.getId() + File.separator + i + ".html", "UTF-8");
                writer.write(result);
            }
        }
    }
}
