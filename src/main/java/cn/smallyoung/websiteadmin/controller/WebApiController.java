package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Category;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/web")
public class WebApiController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    /**
     * 查询类目
     */
    @GetMapping("findCategory")
    public List<Category> findCategory(){
        return categoryService.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    /**
     * 根据类目查询文章
     */
    @GetMapping("findArticleByCategory")
    public Page<Map<String, Object>> findArticleByCategory(String category, @RequestParam(defaultValue = "1") Integer page,
                                                           @RequestParam(defaultValue = "9")Integer size){
        if(StrUtil.hasBlank(category)){
            throw new NullPointerException("Invalid parameter");
        }
        return articleService.findArticle(category, page, size);
    }

    /**
     * 查询所以的文章列表
     */
    @GetMapping("findAllArticle")
    public Page<Map<String, Object>> findAllArticle(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "9")Integer size){
        return articleService.findAll(page, size);
    }

    /**
     * 查询推荐文章列表
     */
    @GetMapping("findRecommendArticle")
    public List<Map<String, Object>> findRecommendArticle(){
        return articleService.findRecommendArticle();
    }

}
