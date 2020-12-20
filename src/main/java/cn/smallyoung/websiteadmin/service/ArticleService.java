package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpRequest;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.ArticleDao;
import cn.smallyoung.websiteadmin.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArticleService extends BaseService<Article, String> {

    @Value("${article.catalog}")
    private String articleCatalog;
    @Value("${baidu.siteUrl}")
    private String baiduSiteUrl;

    @Resource
    private ArticleDao articleDao;

    public Page<Map<String, Object>> findArticle(String category, Integer page, Integer size) {
        Map<String, Object> map = Dict.create().set("AND_INNERJOIN_category-id", category);
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "weight", "updateTime"));

        Page<Article> articles = this.findAll(map, pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(Article::toMap).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    public Page<Map<String, Object>> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "weight", "updateTime"));
        Page<Article> articles = this.findAll(Dict.create().set("AND_EQ_status", "Y"), pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(Article::toMap).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    public List<Map<String, Object>> findRecommendArticle() {
        List<Article> list = this.findAll(Dict.create().set("AND_EQ_status", "Y").set("AND_EQ_recommend", "Y"),
                Sort.by(Sort.Direction.DESC, "weight", "updateTime"));
        return list.stream().map(Article::toMap).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMdContentAndHtmlContent(String id, String mdContent, String htmlContent){
        Article article = articleDao.getOne(id);
        article.setMdContent(mdContent);
        article.setHtmlContent(htmlContent);
        //生成简介、字数统计
        String content = HtmlUtil.cleanHtmlTag(htmlContent);
        if(StrUtil.isBlank(article.getIntroduction())){
            String introduction = content.substring(0, 50);
            article.setIntroduction(introduction);
        }
        //生成静态文件
        String filePath = articleCatalog + id + ".html";
        boolean haveFile = (new File(filePath)).isFile();
        FileUtil.touch(filePath);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("article.html");
        String result = template.render(Dict.create().set("article", article).set("tags", article.getTags().split(","))
                .set("createTime", article.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .set("worksNum", NumberUtil.div(content.length(), 1000, 1))
                .set("readTime", BigDecimal.valueOf(content.length()).divide(BigDecimal.valueOf(Long.parseLong("500")), BigDecimal.ROUND_UP)));
        FileWriter writer = new FileWriter(filePath, "UTF-8");
        writer.write(result);
        articleDao.save(article);
        if(!haveFile){
            String url = StrUtil.format(baiduSiteUrl, id);
            log.info("百度站长API提交新链，请求链接：" + url);
            log.info("百度站长API提交新链，返回结果：" + HttpRequest.get(url).execute().body());
        }
    }

}
