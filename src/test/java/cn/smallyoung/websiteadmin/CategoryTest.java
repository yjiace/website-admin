package cn.smallyoung.websiteadmin;

import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.CategoryService;
import com.upyun.UpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author SmallYoung
 * @date 2021/3/6
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryTest {

    @Resource
    private CategoryService categoryService;
    @Resource
    private ArticleService articleService;

    @Test
    public void testHtml2Js() throws IOException, UpException {
        categoryService.staticCategoryModel();
    }

    @Test
    public void testCategory() throws IOException, UpException {
        categoryService.staticCategory(null);
    }

    @Test
    public void testStaticArticle(){
        articleService.staticArticle(articleService.findById("603f4cffe4b0bf4688e54f1b").get());
    }
}
