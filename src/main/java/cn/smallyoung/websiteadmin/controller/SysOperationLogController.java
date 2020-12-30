package cn.smallyoung.websiteadmin.controller;


import cn.smallyoung.websiteadmin.entity.SysOperationLog;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author smallyoung
 * @data 2020/11/9
 */

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/log")
public class SysOperationLogController {


    @Resource
    private SysOperationLogService sysOperationLogService;

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @PreAuthorize("hasRole('ROLE_SYS_LOG') or hasRole('ROLE_SYS_LOG_FIND')")
    public Page<SysOperationLog> findAll(@RequestParam(defaultValue = "1") Integer page,
                                         HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return sysOperationLogService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "endTime")));
    }
}
