package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Website;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.WebsiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * 官网设置
 *
 * @author smallyoung
 * @data 2021/1/2
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/web/website")
public class SysWebsiteController {

    @Resource
    private WebsiteService websiteService;

    /**
     * 根据条件查询信息
     */
    @GetMapping("findAll")
    @PreAuthorize("hasRole('ROLE_WEBSITE')")
    public List<Website> findAll(HttpServletRequest request) {
        return websiteService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                Sort.by(Sort.Direction.DESC, "updateTime"));
    }

    /**
     * 保存信息并执行静态化
     *
     * @param id 需要执行静态化记录的id
     * @param data     需要保存、执行静态化的必备数据
     */
    @PostMapping("save")
    @PreAuthorize("hasRole('ROLE_WEBSITE_SAVE')")
    public void save(String id, String type, String key, String data, String fileName) {
        if(StrUtil.hasBlank(type, key, fileName)){
            throw new NullPointerException("参数错误");
        }
        Website website = new Website();
        if(StrUtil.isNotBlank(id)){
            Optional<Website> optional = websiteService.findById(id);
            if(!optional.isPresent()){
                String error = String.format("根据ID【%s】没有查询到内容", id);
                log.error(error);
                throw new UsernameNotFoundException(error);
            }
            website = optional.get();
        }
        website.setData(data);
        website.setType(type);
        website.setKey(key);
        website.setFileName(fileName);
        websiteService.save(website);
        websiteService.staticWebsite(website);
    }

}
