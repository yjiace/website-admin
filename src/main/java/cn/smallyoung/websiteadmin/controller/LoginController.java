package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.SysUser;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.SysPermissionService;
import cn.smallyoung.websiteadmin.service.SysUserService;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@RestController
@ResponseResultBody
public class LoginController {

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    private CaptchaService captchaService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    @GetMapping("/login")
    public Dict login(String username, String password, String captchaVerification) {
        if (StrUtil.hasBlank(username, password, captchaVerification)) {
            throw new NullPointerException("参数错误");
        }
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaVerification);
        ResponseModel responseModel = captchaService.verification(captchaVO);
        if(!responseModel.isSuccess()){
            log.error("用户【{}】登录系统，验证码【{}】错误，{}", username, captchaVerification, responseModel);
            throw new NullPointerException(responseModel.getRepMsg());
        }
        SysUser sysUser = sysUserService.loadUserByUsername(username);
        String token = sysUserService.login(sysUser, password);
        log.info("用户【{}】登录系统", username);
        return Dict.create().set("token", tokenHead + " " + token)
                .set("username", username).set("permissions", sysPermissionService.toTree(sysUser.getAllPermission()));
    }
}
