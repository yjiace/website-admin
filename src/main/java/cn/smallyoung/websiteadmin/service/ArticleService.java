package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.HtmlUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.ArticleDao;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.util.BaiduSiteApiInclusion;
import cn.smallyoung.websiteadmin.util.UPYunUtil;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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

    @Value("${article.img}")
    private String dirPath;
    @Value("${article.catalog}")
    private String articleCatalog;
    @Value("${website.sitemap}")
    private String sitemap;

    @Resource
    private ArticleDao articleDao;
    @Resource
    private BaiduSiteApiInclusion baiduSiteApiInclusion;

    @Transactional(rollbackFor = Exception.class)
    public Integer updateIsDeleteByIdIn(List<String> ids) {
        return articleDao.updateIsDeleteByIdIn(ids);
    }

    public Page<Map<String, Object>> findArticle(String category, Integer page, Integer limit) {
        Map<String, Object> map = Dict.create().set("AND_INNERJOIN_category-id", category);
        Pageable pageable = PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "weight", "createTime"));

        Page<Article> articles = this.findAll(map, pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(Article::toMap).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    public Page<Map<String, Object>> findAll(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "weight", "createTime"));
        Page<Article> articles = this.findAll(Dict.create().set("AND_EQ_status", "Y"), pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(Article::toMap).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    public List<Map<String, Object>> findRecommendArticle() {
        List<Article> list = this.findAll(Dict.create().set("AND_EQ_status", "Y").set("AND_EQ_recommend", "Y"),
                Sort.by(Sort.Direction.DESC, "weight", "createTime"));
        return list.stream().map(Article::toMap).collect(Collectors.toList());
    }

    public void staticAllArticle() {
        List<Article> articles = articleDao.findEffectiveArticle();
        if (CollUtil.isEmpty(articles)) {
            return;
        }
        articles.forEach(this::staticArticle);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMdContentAndHtmlContent(String id, String mdContent, String htmlContent) {
        Article article = articleDao.getOne(id);
        article.setMdContent(mdContent);
        article.setHtmlContent(htmlContent);
        //生成简介、字数统计
        String content = HtmlUtil.cleanHtmlTag(htmlContent);
        if (StrUtil.isBlank(article.getIntroduction())) {
            String introduction = content.substring(0, 50);
            article.setIntroduction(introduction);
        }
        articleDao.save(article);
        staticArticle(article);
    }

    private void staticArticle(Article article) {
        String filePath = articleCatalog + article.getId() + ".html";
        boolean haveFile = (new File(filePath)).isFile();
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("article.html");
        String content = HtmlUtil.cleanHtmlTag(article.getHtmlContent());
        String result = template.render(Dict.create().set("article", article).set("tags", article.getTags().split(","))
                .set("createTime", article.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .set("worksNum", NumberUtil.div(content.length(), 1000, 1))
                .set("readTime", BigDecimal.valueOf(content.length()).divide(BigDecimal.valueOf(Long.parseLong("500")), BigDecimal.ROUND_UP)));
        FileWriter writer = new FileWriter(filePath, "UTF-8");
        writer.write(result);
        if (!haveFile) {
            String url = StrUtil.format("https://www.smallyoung.cn/article/{}.html", article.getId());
            log.info("百度站长API提交新链，返回结果：" + baiduSiteApiInclusion.inclusion(url));
            if (StrUtil.isBlank(sitemap)) {
                return;
            }
            File file = new File(sitemap);
            if (!file.isFile()) {
                FileUtil.touch(file);
            }
            FileReader fileReader = new FileReader(file);
            if (fileReader.readLines().stream().noneMatch(s -> s.equals(url))) {
                FileAppender appender = new FileAppender(file, 16, true);
                appender.append(url);
                appender.flush();
            }
        }
    }

    public String uploadImg(MultipartFile file, String path) throws IOException, UpException {
        if (file == null || file.isEmpty()) {
            throw new NullPointerException("参数错误");
        }
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            throw new NullPointerException("参数错误");
        }
        //重命名图片地址
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String localFileName = IdUtil.simpleUUID() + fileSuffix;
        //上传到又拍云
        Response response = UPYunUtil.writeFile(path + localFileName, file.getBytes(), null);
        return response.isSuccessful() ? path + localFileName : "";
    }

}
