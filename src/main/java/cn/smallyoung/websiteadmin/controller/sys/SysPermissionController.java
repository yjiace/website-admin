package cn.smallyoung.websiteadmin.controller.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.sys.SysPermissionService;
import cn.smallyoung.websiteadmin.vo.SysPermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * @author smallyoung
 * @data 2020/12/23
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/permission")
public class SysPermissionController {

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 分页查询所有
     */
    @GetMapping(value = "findTree")
    @PreAuthorize("hasRole('ROLE_PERMISSION') or hasRole('ROLE_PERMISSION_FIND')")
    public List<Tree<String>> findAll(HttpServletRequest request) {
        List<SysPermission> sysPermissions = sysPermissionService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                Sort.by(Sort.Direction.DESC, "updateTime"));

        return TreeUtil.build(sysPermissions, "0", new TreeNodeConfig(),
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                    tree.putExtra("url", treeNode.getUrl());
                    tree.putExtra("icon", treeNode.getIcon());
                    tree.putExtra("orderNumber", treeNode.getOrderNumber());
                    tree.putExtra("type", treeNode.getType());
                    tree.putExtra("updateTime", treeNode.getUpdateTime());
                });
    }

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @PreAuthorize("hasRole('ROLE_PERMISSION') or hasRole('ROLE_PERMISSION_FIND')")
    public Page<SysPermission> findAll(@RequestParam(defaultValue = "1") Integer page,
                                       HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return sysPermissionService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    @GetMapping(value = "findById")
    @PreAuthorize("hasRole('ROLE_PERMISSION') or hasRole('ROLE_PERMISSION_FIND')")
    public SysPermission findById(String id){
        return sysPermissionService.findById(id).orElse(null);
    }

    @PostMapping("save")
    @PreAuthorize("hasRole('ROLE_PERMISSION') or hasRole('ROLE_PERMISSION_SAVE')")
    public SysPermission save(SysPermissionVO sysPermissionVO){
        if(StrUtil.hasBlank(sysPermissionVO.getName())){
            throw new NullPointerException("参数错误");
        }
        SysPermission sysPermission = new SysPermission();
        if(StrUtil.isNotBlank(sysPermissionVO.getId())){
            Optional<SysPermission> optional = sysPermissionService.findById(sysPermissionVO.getId());
            if(!optional.isPresent()){
                String error = String.format("根据ID【%s】没有找到该权限", sysPermission.getId());
                log.error(error);
                throw new UsernameNotFoundException(error);
            }
            sysPermission = optional.get();
        }else{
            sysPermission.setIsDelete("N");
        }
        BeanUtil.copyProperties(sysPermissionVO, sysPermission, CopyOptions.create().setIgnoreNullValue(true));
        return sysPermissionService.save(sysPermission);
    }

    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ROLE_PERMISSION') or hasRole('ROLE_PERMISSION_DELETE')")
    public void delete(String id){
        if(StrUtil.hasBlank(id)){
            throw new NullPointerException("参数错误");
        }
        Optional<SysPermission> optional = sysPermissionService.findById(id);
        if(!optional.isPresent()){
            String error = String.format("根据ID【%s】没有找到该权限", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        SysPermission sysPermission = optional.get();
        sysPermission.setIsDelete("Y");
        sysPermissionService.save(sysPermission);
    }

}
