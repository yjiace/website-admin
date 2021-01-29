package cn.smallyoung.websiteadmin;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

/**
 * @author yangn
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class WebsiteAdminApplication {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${alipay.config.appId}")
    private String appId;
    @Value("${alipay.config.protocol}")
    private String protocol;
    @Value("${alipay.config.gatewayHost}")
    private String gatewayHost;
    @Value("${alipay.config.signType}")
    private String signType;
    @Value("${alipay.config.merchantPrivateKey}")
    private String merchantPrivateKey;
    @Value("${alipay.config.alipayPublicKey}")
    private String alipayPublicKey;

    @Autowired
    private MultipartConfigElement multipartConfigElement;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader(tokenHeader);
        corsConfiguration.setMaxAge(3000L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    public void alipayConfig(){
        Config config = new Config();
        config.protocol = protocol;
        config.gatewayHost = gatewayHost;
        config.signType = signType;
        config.appId = appId;
        config.merchantPrivateKey = merchantPrivateKey;
        config.alipayPublicKey = alipayPublicKey;
        Factory.setOptions(config);
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> servletMvc() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        ServletRegistrationBean<DispatcherServlet> registrationBean = new ServletRegistrationBean<>(dispatcherServlet);
        registrationBean.setMultipartConfig(multipartConfigElement);
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebsiteAdminApplication.class, args);
    }

}
