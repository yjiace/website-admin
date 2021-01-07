package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONObject;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.entity.Category;
import cn.smallyoung.websiteadmin.entity.Website;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @data 2021/1/2
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class WebsiteService extends BaseService<Website, String> {

    @Value("${website.catalog}")
    private String dirPath;
    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    public void staticWebsite(Website website){
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate(website.getFileName());
        String result = template.render(this.getData(website.getKey(), website.getData()));
        FileWriter writer = new FileWriter(dirPath + website.getFileName(), "UTF-8");
        writer.write(result);
    }


    private JSONObject getData(String key, String data){
        JSONObject jsonObject = new JSONObject();
        if(StrUtil.isNotBlank(data)){
            jsonObject.getJSONObject(data);
        }
        if(StrUtil.isNotBlank(key)){
            switch (key){
                case "index":
                    jsonObject.set("recommend", articleService.findRecommendArticle());
                    break;
                case "blog":
                    jsonObject.set("categories", categoryService.findAll().stream().map(Category::toMap).collect(Collectors.toList()));
                    break;
                default:
                    break;
            }
        }
        return jsonObject;
    }
}
