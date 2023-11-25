package com.tum.filter;

import com.tum.jwt.JwtUtil;
import com.tum.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.tum.constant.SecurityConstant.*;

/**
 * Custom Filter to extract the JWT from incoming requests, and authenticate the user if their JWT is valid.
 */
@Component
public class AuthRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        String jwt = null;

        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null && cookie.getValue() != null) {
            jwt = cookie.getValue();
            try {
                if (jwtUtil.verifyJwtSignature(jwt)) {
                    username = jwtUtil.extractUsername(jwt);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        authService.setAuthentication(username,jwt,request);
                    }
                }
            } catch (ExpiredJwtException ex) {
                System.err.println(TOKEN_EXPIRED);
                //response.sendError(HttpStatus.EXPECTATION_FAILED.value(), TOKEN_EXPIRED);
                throw ex;
            } catch (Exception ex) {
                System.err.println(UNMATCHED_SIGNATURE);
                response.sendError(HttpStatus.FORBIDDEN.value(), UNMATCHED_SIGNATURE);
            }
        }
        filterChain.doFilter(request, response);

    }
}
