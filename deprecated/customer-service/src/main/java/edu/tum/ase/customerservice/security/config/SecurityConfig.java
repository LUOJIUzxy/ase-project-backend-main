package edu.tum.ase.customerservice.security.config;

import edu.tum.ase.customerservice.security.filter.AuthRequestFilter;
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
    protected void configure(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository customCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        customCsrfTokenRepository.setCookieName("DISPATCHER-XSRF-TOKEN");
//        customCsrfTokenRepository.setHeaderName("PROJECT-X-XSRF-TOKEN");
        http
                .cors()
                .and()
                .csrf()
//                .disable()
                .csrfTokenRepository(customCsrfTokenRepository) // setting up CSRF token to be included in cookie
                .and()
                .authorizeRequests()
                // other URLs need to be authenticated
                .anyRequest().authenticated()
                // httpBasic is to show log in pop up if username password is incorrectly entered
//                .and()
//                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


}
