package cn.smallyoung.websiteadmin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author smallyoung
 * @data 2020/11/1
 */
@Slf4j
@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
//        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        log.info("审计获取用户{}", authentication.getName());
//        return Optional.of(authentication.getName());
        return Optional.of("smallyoung");
    }

}
