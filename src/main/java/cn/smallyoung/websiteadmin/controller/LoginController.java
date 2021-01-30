package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.SysPermission;
import cn.smallyoung.websiteadmin.entity.SysUser;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.SysPermissionService;
import cn.smallyoung.websiteadmin.service.SysUserService;
import com.alipay.easysdk.base.oauth.models.AlipaySystemOauthTokenResponse;
import com.alipay.easysdk.factory.Factory;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@Controller
public class LoginController {

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${alipay.config.appId}")
    private String appId;
    @Value("${alipay.config.redis_key}")
    private String redisKey;
    @Value("${alipay.config.redis_expiration}")
    private Long redisExpiration;
    @Resource
    private CaptchaService captchaService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    @GetMapping("/login")
    @ResponseResultBody
    public Dict login(String username, String password, String captchaVerification) {
        if (StrUtil.hasBlank(username, password, captchaVerification)) {
            throw new NullPointerException("参数错误");
        }
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaVerification);
        ResponseModel responseModel = captchaService.verification(captchaVO);
        if (!responseModel.isSuccess()) {
            log.error("用户【{}】登录系统，验证码【{}】错误，{}", username, captchaVerification, responseModel);
            throw new NullPointerException(responseModel.getRepMsg());
        }
        SysUser sysUser = sysUserService.loadUserByUsername(username);
        String token = sysUserService.login(sysUser, password);
        log.info("用户【{}】登录系统", username);
        List<SysPermission> sysPermissionList = sysUser.getAllPermission();
        return Dict.create().set("token", tokenHead + " " + token)
                .set("permissionVal", sysPermissionList.size() > 0 ? sysUser.getAllPermission().stream()
                        .map(p -> "." + p.getVal()).collect(Collectors.joining(",")) : new ArrayList<>())
                .set("username", username).set("permissions", sysPermissionService.toTree(sysPermissionList));
    }

    @GetMapping("login/getToken")
    @ResponseResultBody
    public Dict toDocument(HttpServletRequest request) throws Exception {
        AlipaySystemOauthTokenResponse response = Factory.Base.OAuth().getToken(request.getParameter("auth_code"));
        if (response != null && StrUtil.isNotBlank(response.getUserId())) {
            String userId = response.getUserId();
            redisTemplate.opsForSet().add(redisKey, userId);
            redisTemplate.expire(redisKey, redisExpiration, TimeUnit.MINUTES);
            return Dict.create().set("token", tokenHead + " " + sysUserService.getTokenByAliPay(userId));
        }
        log.error("支付宝扫码登录失败");
        throw new RuntimeException("支付宝扫码登录失败");
    }
}
