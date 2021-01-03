package cn.smallyoung.websiteadmin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

class WebsiteAdminApplicationTests {

    @Test
    void contextLoads() {
        String sitemap = "/usr/local/nginx/html/sitemap.txt";
        String url = "https://www.smallyoung.cn/article/1234567.html";
        if(StrUtil.isBlank(sitemap)){
            return;
        }
        File file = new File(sitemap);
        if(!file.isFile()){
            FileUtil.touch(file);
        }
        FileReader fileReader = new FileReader(file);
        if(fileReader.readLines().stream().noneMatch(s -> s.equals(url))){
            FileAppender appender = new FileAppender(file, 16, true);
            appender.append(url);
            appender.flush();
        }
    }

}
