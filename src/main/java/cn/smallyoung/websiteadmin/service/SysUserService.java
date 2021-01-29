package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.SysUserDao;
import cn.smallyoung.websiteadmin.entity.SysUser;
import cn.smallyoung.websiteadmin.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/7/26
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SysUserService extends BaseService<SysUser, String> implements UserDetailsService {

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    public SysUser findByUsername(String username) {
        return StrUtil.hasBlank(username) ? null : sysUserDao.findByUsername(username);
    }

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findEffectiveByUsername(username);
        if (user == null) {
            String error = String.format("根据用户名【%s】没有找到用户", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        user.getAuthorities();
        return user;
    }

    /**
     * 登录
     *
     * @param sysUser 用户
     * @param password 密码
     * @return token
     */
    public String login(SysUser sysUser, String password) {
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            log.error("用户【{}】登录系统，密码【{}】错误", sysUser.getUsername(), password);
            throw new BadCredentialsException("密码不正确");
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(sysUser.getUsername(), JwtTokenUtil.UserType.SYS);
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前登录的用户名
     */
    public String currentlyLoggedInUser() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 根据用户名批量查询用户
     *
     * @param usernames 用户名集合
     */
    public List<SysUser> findByUsernameIn(List<String> usernames) {
        return CollUtil.isNotEmpty(usernames) ? sysUserDao.findByUsernameInAndStatusAndIsDelete(usernames, "Y", "N") : null;
    }

    public String getTokenByAliPay(String userId){
        return jwtTokenUtil.generateToken(userId, JwtTokenUtil.UserType.ALIPAY);
    }

}
