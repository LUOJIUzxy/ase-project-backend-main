package com.tum.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;

/**
 * Cookie configuration. Use cookie to send JWT, because frontend script cannot access the cookie but can access localstorage. It is not safe to put JWT in local storage.
 */
@Configuration
public class CookieConfig {
    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(2 * 60 * 60); // 2 hours
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        return cookie;
    }

}
