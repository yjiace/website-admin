package cn.smallyoung.websiteadmin.controller.sys;

import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author smallyoung
 * @date 2020/7/28
 */

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/api/sys/web")
public class WebController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    @GetMapping("findAllArticle")
    public Page<Article> findAllArticle(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest request,
                                 @RequestParam(defaultValue = "9")Integer size){
        return articleService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    @GetMapping("getMdContentById")
    public String getMdContentById(String id){
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        Optional<Article> article = articleService.findById(id);
        if(article.isPresent()){
            return article.get().getMdContent();
        }
        throw new NullPointerException("获取文章对象失败");
    }

    @GetMapping("/findArticle")
    public Article getArticle(String id){
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        Optional<Article> article = articleService.findById(id);
        if(article.isPresent()){
            return article.get();
        }
        throw new NullPointerException("获取文章对象失败");
    }

    @PostMapping("saveArticle")
    public Article saveArticle(Article article){
        return articleService.save(article);
    }

    @DeleteMapping("/deleteArticle")
    public void deleteArticle(String id){
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        articleService.delete(id);
    }

    @PostMapping("updateMd")
    public void updateMd(String id, String mdContent, String htmlContent){
        articleService.updateMdContentAndHtmlContent(id, mdContent, htmlContent);
    }
}
