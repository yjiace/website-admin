package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.SysRole;
import cn.smallyoung.websiteadmin.entity.SysUser;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.SysRoleService;
import cn.smallyoung.websiteadmin.service.SysUserService;
import cn.smallyoung.websiteadmin.vo.SysUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/user")
public class SysUserController {

    @Value("${default.password}")
    private String defaultPassword;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @PreAuthorize("hasRole('ROLE_SYSUSER')")
    public Page<SysUser> findAll(@RequestParam(defaultValue = "1") Integer page,
                                 HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "search_");
        return sysUserService.findAll(map,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 根据用户名查询详细信息
     *
     * @param username 用户名
     */
    @GetMapping(value = "findByUsername")
    @PreAuthorize("hasRole('ROLE_SYSUSER') or authentication.principal.username.equals(#username)")
    public SysUser findById(String username) {
        if (StrUtil.hasBlank(username)) {
            username = sysUserService.currentlyLoggedInUser();
        }
        return sysUserService.loadUserByUsername(username);
    }

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     */
    @GetMapping("checkUsername")
    @PreAuthorize("hasRole('ROLE_SYSUSER_SAVE')")
    public boolean checkUsername(String username) {
        return StrUtil.isNotBlank(username) && sysUserService.findByUsername(username) != null;
    }

    /**
     * 保存用户
     */
    @PostMapping(value = "save")
    @PreAuthorize("hasRole('ROLE_SYSUSER_SAVE')")
    public SysUser save(SysUserVO sysUserVO) {
        if (StrUtil.isBlank(sysUserVO.getUsername())) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(sysUserVO.getUsername());
        if (user == null) {
            user = new SysUser();
            user.setStatus("Y");
            user.setIsDelete("N");
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }
        BeanUtil.copyProperties(sysUserVO, user);
        return sysUserService.save(user);
    }

    /**
     * 更改用户状态
     *
     * @param username 用户名
     * @param status   需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus")
    @PreAuthorize("hasRole('ROLE_SYSUSER_UPDATE_STATUS')")
    public SysUser updateStatus(String username, String status) {
        if (StrUtil.hasBlank(status, username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(username);
        String isDelete = "Y";
        if (isDelete.equals(user.getIsDelete())) {
            String error = String.format("该用户【%s】已删除", username);
            log.error(error);
            throw new RuntimeException(error);
        }
        user.setStatus(status);
        return sysUserService.save(user);
    }

    /**
     * 重置密码
     *
     * @param username 用户名
     */
    @PostMapping(value = "resetPassword")
    @PreAuthorize("hasRole('ROLE_SYSUSER_RESET_PASSWORD')")
    public SysUser resetPassword(String username) {
        SysUser user = checkUser(username);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        return sysUserService.save(user);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PostMapping(value = "updatePassword")
    public void updatePassword(String oldPassword, String newPassword) {
        if (StrUtil.hasBlank(oldPassword, newPassword)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.loadUserByUsername(sysUserService.currentlyLoggedInUser());
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("密码错误");
            throw new RuntimeException("密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserService.save(user);
    }

    /**
     * 删除
     *
     * @param username 用户名
     */
    @DeleteMapping(value = "delete")
    @PreAuthorize("hasRole('ROLE_SYSUSER_DEL')")
    public SysUser delete(String username) {
        SysUser user = checkUser(username);
        user.setIsDelete("Y");
        return sysUserService.save(user);
    }

    /**
     * 查询用户权限树
     *
     * @param username 用户名
     */
    @GetMapping(value = "findRolesList")
    @PreAuthorize("hasRole('ROLE_SYSUSER_ASSIGN_ROLES')")
    public Dict findList(String username) {
        SysUser user = checkUser(username);
        List<SysRole> roleList = sysRoleService.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        return Dict.create().set("have", user.getRoles().stream().map(SysRole::getId).collect(Collectors.toList()))
                .set("all", roleList.stream().map(r -> Dict.create().set("value", r.getId()).set("title", r.getName())).collect(Collectors.toList()));
    }

    /**
     * 设置用户角色
     *
     * @param username 用户名
     * @param roles    角色id集合，逗号分割
     */
    @PostMapping("assignRoles")
    @PreAuthorize("hasRole('ROLE_SYSUSER_ASSIGN_ROLES')")
    public SysUser assignRoles(String username, String roles) {
        SysUser user = checkUser(username);
        if (StrUtil.isNotBlank(roles)) {
            List<SysRole> roleList = sysRoleService.findByIdInAndIsDelete(Stream.of(roles.split(","))
                    .map(String::trim).collect(Collectors.toList()));
            user.setRoles(roleList);
        } else {
            user.setRoles(null);
        }
        return sysUserService.save(user);
    }

    /**
     * 根据用户名校验、获取对象
     *
     * @param username 用户名
     */
    private SysUser checkUser(String username) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.loadUserByUsername(username);
        String isDelete = "Y";
        if (isDelete.equals(user.getIsDelete())) {
            String error = String.format("该用户【%s】已删除", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        return user;
    }
}
