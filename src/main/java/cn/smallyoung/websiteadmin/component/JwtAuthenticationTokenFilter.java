package cn.smallyoung.websiteadmin.component;


import cn.smallyoung.websiteadmin.entity.SysUser;
import cn.smallyoung.websiteadmin.service.SysUserService;
import cn.smallyoung.websiteadmin.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT登录授权过滤器
 *
 * @author smallyoung
 * @data 2020/10/27
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${alipay.config.redis_key}")
    private String redisKey;
    @Value("${alipay.config.redis_expiration}")
    private Long redisExpiration;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,  FilterChain chain) throws ServletException, IOException {
        response.setHeader("userId", null);
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            boolean isAliPay = false;
            if(jwtTokenUtil.canRefresh(authToken)){
                if(jwtTokenUtil.getTypeFromToken(authToken) == JwtTokenUtil.UserType.ALIPAY){
                    Boolean haveToken = redisTemplate.opsForSet().isMember(redisKey, authToken);
                    if(haveToken != null && haveToken){
                        String newToken = jwtTokenUtil.refreshToken(authToken);
                        redisTemplate.opsForSet().add(redisKey, newToken);
                        redisTemplate.expire(redisKey, redisExpiration, TimeUnit.MINUTES);
                        response.setHeader(tokenHeader, tokenHead + " " + newToken);
                        response.setHeader("userId", username);
                        isAliPay = true;
                    }
                }else{
                    String newToken = jwtTokenUtil.refreshToken(authToken);
                    response.setHeader(tokenHeader, tokenHead + " " + newToken);
                    response.setHeader("userId", username);
                }
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SysUser user = isAliPay ? sysUserService.loadAliPayUser(username) :  sysUserService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, user.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
