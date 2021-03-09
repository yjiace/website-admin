package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Article;
import cn.smallyoung.websiteadmin.entity.Category;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.CategoryService;
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
import java.util.List;
import java.util.Optional;

/**
 * @author smallyoung
 * @date 2020/7/28
 */

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/article")
public class SysArticleController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;

    /**
     * 查询所有文章列表
     *
     * @param page    页码
     * @param request 参数
     * @param limit   页数
     */
    @GetMapping("findAll")
    @PreAuthorize("hasRole('ROLE_ARTICLE')")
    public Page<Article> findAllArticle(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest request,
                                        @RequestParam(defaultValue = "9") Integer limit) {
        return articleService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime")));
    }

    /**
     * 根据ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章对象
     */
    @GetMapping("/findById")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public Article getArticle(String id) {
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        Optional<Article> article = articleService.findById(id);
        if (article.isPresent()) {
            return article.get();
        }
        log.error("获取文章对象失败");
        throw new RuntimeException("获取文章对象失败");
    }

    /**
     * 获取文章的Markdown文档
     *
     * @param id 文章ID
     */
    @GetMapping("getMdContentById")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public String getMdContentById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        Optional<Article> article = articleService.findById(id);
        if (article.isPresent()) {
            return EscapeUtil.escape(article.get().getMdContent());
        }
        log.error("获取文章对象失败");
        throw new RuntimeException("获取文章对象失败");
    }

    /**
     * 更改Markdown内容
     *
     * @param id          文章ID
     * @param mdContent   Markdown内容
     * @param htmlContent html内容
     */
    @PostMapping("updateMd")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public void updateMd(String id, String mdContent, String htmlContent) {
        articleService.updateMdContentAndHtmlContent(id, mdContent, htmlContent);
    }

    /**
     * 上次文章内的图片
     *
     * @param file 二进制文件
     * @param path 路径
     */
    @PostMapping("uploadImg/{path}")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATE_MDCONTENT')")
    public String uploadImg(MultipartFile file, @PathVariable String path) throws IOException, UpException {
        return articleService.uploadImg(file, "/article/" + path + "/");
    }

    /**
     * 文件封面
     *
     * @param file 二进制文件
     */
    @PostMapping("uploadCover")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public String uploadCover(MultipartFile file) throws IOException, UpException {
        return articleService.uploadImg(file, "/article/cover/");
    }

    /**
     * 保存
     */
    @PostMapping("save")
    @PreAuthorize("hasRole('ROLE_ARTICLE_SAVE')")
    public Article saveArticle(ArticleVO articleVO) {
        Article article = new Article();
        if (StrUtil.isBlank(articleVO.getId())) {
            article.setIsDelete("N");
            article.setStatus("N");
            article.setRecommend("N");
        } else {
            Optional<Article> optional = articleService.findById(articleVO.getId());
            if (!optional.isPresent()) {
                log.error("获取文章对象失败");
                throw new RuntimeException("获取文章对象失败");
            }
            article = optional.get();
        }
        BeanUtil.copyProperties(articleVO, article);
        if (StrUtil.isNotBlank(articleVO.getCategoryId())) {
            Optional<Category> optional = categoryService.findById(articleVO.getCategoryId());
            if (!optional.isPresent()) {
                log.error("获取文章类别失败");
                throw new RuntimeException("获取文章类别失败");
            }
            article.setCategory(optional.get());
        }
        if (StrUtil.isBlank(article.getId())) {
            article.setIsDelete("N");
        }
        return articleService.save(article);
    }

    /**
     * 更改状态、推荐
     *
     * @param id  文章ID
     * @param key 类型，status：状态；recommend：推荐
     * @param val 修改后的值
     */
    @PostMapping("updateStatus")
    @PreAuthorize("hasRole('ROLE_ARTICLE_UPDATESTATUS')")
    public Article updateStatus(String id, String key, String val) {
        if (StrUtil.isBlank(id) || StrUtil.hasBlank(key, val)) {
            throw new NullPointerException("参数错误");
        }
        Optional<Article> optional = articleService.findById(id);
        if (!optional.isPresent()) {
            log.error("获取文章对象失败");
            throw new RuntimeException("获取文章对象失败");
        }
        Article article = optional.get();
        boolean statics = false;
        if (key.equals("status")) {
            article.setStatus(val);
            if("Y".equals(val)){
                statics = true;
            }
        }
        if (key.equals("recommend")) {
            article.setRecommend(val);
        }
        articleService.save(article);
        if(statics){
            articleService.staticArticle(article);
        }
        return article;
    }

    /**
     * 删除文章
     *
     * @param ids 文章ID集合
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ARTICLE_DEL')")
    public Integer delete(@RequestParam(value = "ids") List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw new NullPointerException("参数错误");
        }
        return articleService.updateIsDeleteByIdIn(ids);
    }

    /**
     * 根据id静态化文章列表
     */
    @PostMapping("staticArticle")
    public void staticArticle(@RequestParam(value = "ids") List<String> ids) {
        articleService.staticAllArticle(ids);
    }

}
