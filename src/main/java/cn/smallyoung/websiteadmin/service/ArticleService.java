package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.io.FileUtil;
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

    @Resource
    private ArticleDao articleDao;

    public Page<Map<String, Object>> findArticle(String category, Integer page, Integer size) {
        Map<String, Object> map = Dict.create().set("AND_INNERJOIN_category-id", category);
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "weight", "createTime"));

        Page<Article> articles = this.findAll(map, pageable);

        return new PageImpl<>(articles.getContent().stream()
                .map(Article::toMap).collect(Collectors.toList())
                , pageable, articles.getTotalElements());
    }

    public Page<Map<String, Object>> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size,
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
    }

    public String uploadImg(MultipartFile file, String path) throws IOException, UpException {
        if(file == null || file.isEmpty()){
            throw new NullPointerException("参数错误");
        }
        String fileName = file.getOriginalFilename();
        if(StrUtil.isBlank(fileName)){
            throw new NullPointerException("参数错误");
        }
        //重命名图片地址
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String localFileName = IdUtil.simpleUUID() + fileSuffix;
        String filePath = dirPath + File.separator + path + File.separator + localFileName;
        FileUtil.touch(filePath);
        FileWriter writer = new FileWriter(filePath, "UTF-8");
        writer.writeFromStream(file.getInputStream());
        //上传到又拍云
        Response response = UPYunUtil.writeFile(path + localFileName, writer.getFile(), null);
        return response.isSuccessful() ? path + localFileName : "";
    }

}
