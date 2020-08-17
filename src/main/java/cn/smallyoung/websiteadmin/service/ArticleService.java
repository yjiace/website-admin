package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.HtmlUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.ArticleDao;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.util.ConvertBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Service
@Transactional(readOnly = true)
public class ArticleService extends BaseService<Article, String> {

    @Value("${article.catalog}")
    private String articleCatalog;

    @Resource
    private ArticleDao articleDao;

    public Page<Map<String, Object>> findArticle(String category, Integer page, Integer size) {
        Map<String, Object> map = Dict.create().set("AND_INNERJOIN_category-id", category);
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "weight", "updateTime"));

        Page<Article> articles = this.findAll(map, pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(article -> ConvertBean.bean2Map(Article.class, "id","title","coverUrl","introduction","tags",
                        "updateTime", "category.id","category.name","category.backgroundColor","category.count")).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article save(Article article){
        return articleDao.save(article);
    }

    @Transactional(rollbackFor = Exception.class)
    public Article updateMdContentAndHtmlContent(String id, String mdContent, String htmlContent, String tags){
        Article article = articleDao.getOne(id);
        article.setMdContent(mdContent);
        article.setHtmlContent(htmlContent);
        article.setTags(tags);
        //生成简介、字数统计
        String content = HtmlUtil.cleanHtmlTag(htmlContent);
        String introduction = content.substring(0, 50);
        article.setIntroduction(introduction);
        //生成静态文件
        String filePath = articleCatalog + id + ".html";
        FileUtil.touch(filePath);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("article.html");
        System.out.println(content.length() + "**************");
        String result = template.render(Dict.create().set("article", article).set("tags", article.getTags().split(","))
                .set("updateTime", article.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .set("worksNum", NumberUtil.div(content.length(), 1000, 1))
                .set("readTime", BigDecimal.valueOf(content.length()).divide(BigDecimal.valueOf(Long.parseLong("500")), BigDecimal.ROUND_UP)));
        FileWriter writer = new FileWriter(filePath, "UTF-8");
        writer.write(result);
        return articleDao.save(article);
    }

}
