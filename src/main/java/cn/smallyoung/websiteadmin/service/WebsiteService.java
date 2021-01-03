package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONObject;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.entity.Website;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void staticWebsite(Website website){
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate(website.getFileName());
        String result = template.render(new JSONObject().getJSONObject(website.getData()));
        FileWriter writer = new FileWriter(dirPath + website.getFileName(), "UTF-8");
        writer.write(result);
    }
}
