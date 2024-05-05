package com.project.config.security;

import com.project.config.jwt.filter.AuthTokenFilter;
import com.project.config.jwt.handler.AuthEntryPointHandler;
import com.project.config.jwt.service.MemberDetailsServiceImpl;
import com.project.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.project.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.project.config.oauth2.service.OAuth2MemberService;
import com.project.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2MemberService oAuth2MemberService;
    private final AuthTokenFilter authTokenFilter;
    private final MemberDetailsServiceImpl memberDetailsService;
    // OAuth2 인증 성공시, 수행하는 Handler
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // OAuth2 인증 실패시, 수행하는 Handler
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final AuthEntryPointHandler authEntryPointHandler;
    @Value("${spring.security.jwt.token.cookie.name}")
    private String JWTCookieName;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authEntryPointHandler));
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/private/**").authenticated()
                .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                .requestMatchers("/oauth2/authorization/**").permitAll()
                .requestMatchers("/loginForm").permitAll()
                .requestMatchers("/main").permitAll()
                .anyRequest().authenticated()
        );
        http.oauth2Login(oauth2 -> oauth2.loginPage("/loginForm")
                .defaultSuccessUrl("/main")
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2MemberService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );
        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .deleteCookies(JWTCookieName)
                .logoutSuccessUrl("/loginForm"));
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
