package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.lang.Dict;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.ArticleDao;
import cn.smallyoung.websiteadmin.entity.Article;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Service
@Transactional(readOnly = true)
public class ArticleService extends BaseService<Article, Long> {

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article save(Article article){

        article.setIntroduction(article.getMdContent().substring(0, 250).replaceAll("\\t|\\r|\\n|\\!\\[(.*?)\\]+\\(http(.*?)\\)", ""));
        return articleDao.save(article);
    }

}
