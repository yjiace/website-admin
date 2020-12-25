package cn.smallyoung.websiteadmin.controller.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.sys.SysPermission;
import cn.smallyoung.websiteadmin.entity.sys.SysRole;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.sys.SysPermissionService;
import cn.smallyoung.websiteadmin.service.sys.SysRoleService;
import cn.smallyoung.websiteadmin.vo.SysRoleVO;
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
 * @data 2020/10/31
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 查询所有权限
     */
    @GetMapping(value = "findAllPermission")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_SAVE')")
    public List<Tree<String>> findAllPermission(HttpServletRequest request) {
        List<SysPermission> sysPermissions = sysPermissionService.findAll(WebUtils.getParametersStartingWith(request, "search_"));
        return TreeUtil.build(sysPermissions, "0", new TreeNodeConfig(),
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                });
    }

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_FIND')")
    public Page<SysRole> findAll(@RequestParam(defaultValue = "1") Integer page,
                                 HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return sysRoleService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    @GetMapping(value = "findById")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_FIND')")
    public SysRole findById(String id){
        return checkRole(id);
    }

    /**
     * 新增角色
     */
    @PostMapping(value = "save")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_SAVE')")
    public SysRole save(SysRoleVO roleVO) {
        if(StrUtil.hasBlank(roleVO.getName())){
            throw new NullPointerException("参数错误");
        }
        SysRole role = new SysRole();
        if(StrUtil.isNotBlank(roleVO.getId())){
            role = checkRole(roleVO.getId());
        }else{
            role.setIsDelete("N");
        }
        BeanUtil.copyProperties(roleVO, role, CopyOptions.create().setIgnoreNullValue(true));
        return sysRoleService.save(role);
    }

    /**
     * 删除角色
     *
     * @param id 需要删除的角色ID
     * @return ResultMap封装好的返回数据
     */
    @DeleteMapping(value = "delete")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_DELETE')")
    public SysRole delete(String id) {
        SysRole role = checkRole(id);
        role.setIsDelete("Y");
        return sysRoleService.save(role);
    }

    @PostMapping("grantPermissions")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_GRANT_PERMISSIONS')")
    public void grantPermissions(String id, List<String> ids){
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        SysRole role = checkRole(id);
        List<SysPermission> permissions = sysPermissionService.findByIdInAndIsDelete(ids);
        role.setSysPermissions(permissions);
        sysRoleService.save(role);
    }

    private SysRole checkRole(String id) {
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        Optional<SysRole> optional = sysRoleService.findById(id);
        if (!optional.isPresent()) {
            String error = String.format("根据ID【%s】没有找到该角色", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        SysRole role = optional.get();
        String isDelete = "Y";
        if (isDelete.equals(role.getIsDelete())) {
            String error = String.format("该角色【%s】已删除", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        return role;
    }

}
