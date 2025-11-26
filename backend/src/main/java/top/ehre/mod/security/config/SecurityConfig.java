package top.ehre.mod.security.config;

import top.ehre.mod.security.authentication.LoginTokenFilter;
import top.ehre.mod.security.authentication.UsernamePasswordFilter;
import top.ehre.mod.security.authentication.UsernamePasswordProvider;
import top.ehre.mod.security.authentication.handler.AuthFailureHandler;
import top.ehre.mod.security.authentication.handler.AuthSuccessHandler;
import top.ehre.mod.security.authentication.handler.AuthEntryPointIHandler;
import top.ehre.mod.security.authentication.handler.LogoutHandler;
import top.ehre.mod.security.authorization.MenuDeniedHandler;
import top.ehre.mod.security.captcha.CaptchaFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @作者 曾续缘
 * @日期 2024/11/25 20:49
 */
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    UsernamePasswordProvider usernamePasswordProvider;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    LoginTokenFilter loginTokenFilter;


    @Bean
    public AuthenticationManager userAuthenticationManager() {
        return new ProviderManager(usernamePasswordProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");
        // 允许所有来源的请求，* 表示通配符，即允许任何来源。
        cors.setAllowCredentials(true);
        // 允许携带身份凭证（如 Cookie，Authorization Header 等）进行跨域请求。
        cors.addAllowedHeader("*");
        // 允许所有的请求头。
        cors.addAllowedMethod("*");
        // 允许所有的请求方法。
        cors.addExposedHeader("*");
        // 允许在响应头中暴露的自定义响应头。

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 创建一个基于 URL 的 CORS 配置源。
        source.registerCorsConfiguration("/**", cors);
        // 注册 CORS 配置信息，"/**" 表示匹配所有的路径。
        return source;
    }

    @Bean
    public FilterRegistrationBean<LoginTokenFilter> tenantFilterRegistration(LoginTokenFilter filter) {
        FilterRegistrationBean<LoginTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }


    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        AuthSuccessHandler authSuccessHandler = new AuthSuccessHandler(redisTemplate);
        AuthFailureHandler authFailureHandler = new AuthFailureHandler();
        UsernamePasswordFilter filter = new UsernamePasswordFilter();
        filter.setAuthenticationManager(userAuthenticationManager());
        filter.setAuthenticationSuccessHandler(authSuccessHandler);
        filter.setAuthenticationFailureHandler(authFailureHandler);
        CaptchaFilter captchaFilter = new CaptchaFilter(redisTemplate, authFailureHandler);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .securityMatcher("/login", "/user/register")
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new AuthEntryPointIHandler())
                        .accessDeniedHandler(new MenuDeniedHandler())
                )
        ;
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .securityMatcher("/**")
                .addFilterAt(loginTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout.addLogoutHandler(new LogoutHandler(redisTemplate)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/getCaptcha").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/default/avatar.png").permitAll()
                        .requestMatchers("/mods/page").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new AuthEntryPointIHandler())
                        .accessDeniedHandler(new MenuDeniedHandler()))
        ;
        return http.build();
    }

}
