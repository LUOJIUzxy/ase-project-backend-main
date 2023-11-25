package com.tum.security.filter;


import com.tum.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static constant.SecurityConstant.TOKEN_EXPIRED;
import static constant.SecurityConstant.UNMATCHED_SIGNATURE;


/**
 * Custom Filter to extract the JWT from incoming requests, and authenticate the user if their JWT is valid.
 */
@Component
public class AuthRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;


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
                        setAuthentication(username,jwt,request);
                    }
                }
            } catch (ExpiredJwtException ex) {
                System.err.println(TOKEN_EXPIRED);
                response.sendError(HttpStatus.EXPECTATION_FAILED.value(), TOKEN_EXPIRED);
            } catch (Exception ex) {
                System.err.println(UNMATCHED_SIGNATURE);
                response.sendError(HttpStatus.EXPECTATION_FAILED.value(), UNMATCHED_SIGNATURE);
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username,String jwt,HttpServletRequest request) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        List<GrantedAuthority> authorities = jwtUtil.extractAuthoritiesFromJwt(jwt);
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authentication = userPasswordAuthToken;
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
