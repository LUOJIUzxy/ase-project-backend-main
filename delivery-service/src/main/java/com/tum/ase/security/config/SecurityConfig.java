package com.tum.ase.security.config;

import com.tum.ase.security.filter.CustomAuthenticationEntryPoint;
import com.tum.ase.security.filter.AuthRequestFilter;
import com.tum.ase.security.filter.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // security is on method level
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthRequestFilter authRequestFilter;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    protected void configure(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository customCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        customCsrfTokenRepository.setCookieName("DELIVERY-XSRF-TOKEN");
        customCsrfTokenRepository.setHeaderName("DELIVERY-XSRF-TOKEN");

        http
                .cors()
                .and()
                .csrf()
                .csrfTokenRepository(customCsrfTokenRepository)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
