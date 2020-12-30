package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.vo.ArticleVO;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

/**
 * @author smallyoung
 * @date 2020/7/28
 */

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("findAll")
    @PreAuthorize("hasRole('ROLE_ARTICLE')")
    public Page<Article> findAllArticle(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest request,
                                 @RequestParam(defaultValue = "9")Integer size){
        return articleService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    @GetMapping("/findById")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
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

    @GetMapping("getMdContentById")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public String getMdContentById(String id){
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        Optional<Article> article = articleService.findById(id);
        if(article.isPresent()){
            return EscapeUtil.escape(article.get().getMdContent());
        }
        throw new NullPointerException("获取文章对象失败");
    }


    @PostMapping("updateMd")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public void updateMd(String id, String mdContent, String htmlContent){
        articleService.updateMdContentAndHtmlContent(id, mdContent, htmlContent);
    }


    @PostMapping("uploadImg/{path}")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public String uploadImg(MultipartFile file, @PathVariable String path) throws IOException, UpException {
        return articleService.uploadImg(file, "/article/" + path + "/");
    }

    @PostMapping("uploadCover")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public String uploadCover(MultipartFile file) throws IOException, UpException {
        return articleService.uploadImg(file, "/article/cover/");
    }

    @PostMapping("save")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public Article saveArticle(ArticleVO articleVO) {
        Article article = new Article();
        if(StrUtil.isBlank(articleVO.getId())){
            article.setIsDelete("N");
            article.setStatus("N");
            article.setRecommend("N");
        }else{
            Optional<Article> optional = articleService.findById(articleVO.getId());
            if(!optional.isPresent()){
                throw new NullPointerException("获取文章对象失败");
            }
            article = optional.get();
        }
        BeanUtil.copyProperties(articleVO, article, CopyOptions.create().setIgnoreNullValue(true));
        if(StrUtil.isBlank(article.getId())){
            article.setIsDelete("N");
        }
        return articleService.save(article);
    }

    @PostMapping("updateStatus")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATESTATUS')")
    public Article updateStatus(String id, String key, String val){
        if(id == null || StrUtil.hasBlank(key, val)){
            throw new NullPointerException("参数错误");
        }
        Optional<Article> optional = articleService.findById(id);
        if(!optional.isPresent()){
            throw new NullPointerException("获取文章对象失败");
        }
        Article article = optional.get();
        if(key.equals("status")){
            article.setStatus(val);
        }
        if(key.equals("recommend")){
            article.setRecommend(val);
        }
        return articleService.save(article);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ARTICLE_DEL')")
    public void delete(String id){
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        Optional<Article> optional = articleService.findById(id);
        if(!optional.isPresent()){
            throw new NullPointerException("获取文章对象失败");
        }
        Article article = optional.get();
        article.setIsDelete("Y");
        articleService.save(article);
    }
}
