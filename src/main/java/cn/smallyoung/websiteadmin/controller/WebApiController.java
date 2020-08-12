package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author smallyoung
 * @date 2020/7/25
 */

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/api/web")
public class WebApiController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    /**
     * 查询类目
     */
    @GetMapping("findCategory")
    public List<Map<String, Object>> findCategory(){
        return categoryService.findAllCategory();
    }

    /**
     * 根据类目查询文章
     */
    @GetMapping("findArticle")
    public Page<Map<String, Object>> findArticle(String category, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "9")Integer size){
        if(StrUtil.hasBlank(category)){
            throw new NullPointerException("Invalid parameter");
        }
        return articleService.findArticle(category, page, size);
    }
}
