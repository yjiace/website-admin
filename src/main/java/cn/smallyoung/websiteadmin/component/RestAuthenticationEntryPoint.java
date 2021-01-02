package cn.smallyoung.websiteadmin.component;

import cn.hutool.json.JSONUtil;
import cn.smallyoung.websiteadmin.util.result.Result;
import cn.smallyoung.websiteadmin.util.result.ResultStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 *  @author smallyoung
 * @data 2020/10/31
 */

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSONUtil.parse(Result.result(ResultStatus.UNAUTHORIZED, authException.getMessage())));
        response.getWriter().flush();
    }
}
