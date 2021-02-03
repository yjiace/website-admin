package cn.smallyoung.websiteadmin.component;

import cn.hutool.json.JSONUtil;
import cn.smallyoung.websiteadmin.util.JwtTokenUtil;
import cn.smallyoung.websiteadmin.util.result.Result;
import cn.smallyoung.websiteadmin.util.result.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出成功处理器
 * @author SmallYoung
 * @date 2021/2/3
 */
@Slf4j
@Component
public class LogoutHandler implements LogoutSuccessHandler {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.redis_key}")
    private String redisKey;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            if(username != null){
                redisTemplate.opsForSet().remove(redisKey, username);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().println(JSONUtil.parse(Result.result(ResultStatus.SUCCESS, "退出成功")));
                response.getWriter().flush();
            }
        }
    }
}
