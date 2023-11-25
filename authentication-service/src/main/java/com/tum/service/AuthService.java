package com.tum.service;

import com.tum.config.CookieConfig;
import com.tum.constant.UserRole;
import com.tum.jwt.JweUtil;
import com.tum.jwt.JwtUtil;
import com.tum.jwt.KeyStoreManager;
import com.tum.model.AppUser;
import com.tum.model.AuthRequest;
import com.tum.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Service
public class AuthService {
    private JweUtil jweUtil;

    private JwtUtil jwtUtil;
    private MongoUserDetailsService mongoUserDetailsService;
    private CookieConfig cookieConfig;

    private KeyStoreManager keyStoreManager;

    private AuthenticationManager authManager;

    private AppUserRepository appUserRepository;

    @Autowired
    public AuthService(JweUtil jweUtil, MongoUserDetailsService mongoUserDetailsService,
                       AuthenticationManager authManager, KeyStoreManager keyStoreManager,
                       JwtUtil jwtUtil, CookieConfig cookieConfig, AppUserRepository appUserRepository) {
        this.jweUtil = jweUtil;
        this.jwtUtil = jwtUtil;
        this.mongoUserDetailsService = mongoUserDetailsService;
        this.authManager = authManager;
        this.keyStoreManager = keyStoreManager;
        this.cookieConfig = cookieConfig;
        this.appUserRepository = appUserRepository;
    }

    public ResponseEntity<String> deleteAuthToken(HttpServletResponse response) {
        Cookie cookie = cookieConfig.createCookie("jwt", null);
        response.addCookie(cookie);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    public ResponseEntity<AppUser> createAuthTokenAndReturnAppUser(boolean isBox, AuthRequest request, HttpServletResponse response) throws IOException {
        String pw = isBox? request.getPassword(): jweUtil.decryptPasswordInJwe(request.getPassword());

        final UserDetails aseUserDetails = mongoUserDetailsService.loadUserByUsername(request.getUsername());

        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(aseUserDetails, pw);
            authentication = authManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (UsernameNotFoundException ex) {
            ex.printStackTrace();
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Email or password is incorrect");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Authentication principal: " + authentication.getPrincipal());

        if (authentication != null) {
            final String jwt = jwtUtil.generateToken(aseUserDetails);
            Cookie cookie = cookieConfig.createCookie("jwt", jwt);
            response.addCookie(cookie);

            AppUser loginUser = appUserRepository.findByUsername(request.getUsername());

            return new ResponseEntity<>(loginUser, HttpStatus.OK);
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Email or password is incorrect");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    public HashMap<String, String> getPublicKeyData() {
        HashMap<String, String> pKeyResponse = new HashMap<String, String>();
        RSAPublicKey rsaPubKey = (RSAPublicKey) keyStoreManager.getPublicKey();
        byte[] modulusByte = rsaPubKey.getModulus().toByteArray(); // Get Modulus of Public key;
        // Format the modulus into byte format (e.g. AB:CD:E1)
        String modulusByteStr = "";
        for (byte b : modulusByte) {
            modulusByteStr += String.format("%02X:", b);
        }
        modulusByteStr = modulusByteStr.substring(0, modulusByteStr.length() - 1);
        // base64 encoded format of public key
        pKeyResponse.put("key", Base64.getMimeEncoder().encodeToString(rsaPubKey.getEncoded()));
        // Modulus string of Public key;
        pKeyResponse.put("n", modulusByteStr);
        // public exponent of RSAPublicKey
        pKeyResponse.put("e", rsaPubKey.getPublicExponent().toString());
        return pKeyResponse;

    }

    public void setAuthentication(String username,String jwt,HttpServletRequest request) {
        List<GrantedAuthority> authorities = jwtUtil.extractAuthoritiesFromJwt(jwt);
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authentication = userPasswordAuthToken;
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public PublicKey getPem() {
        return keyStoreManager.getPublicKey();
    }

    // TODO: To delete later
    public AppUser createDebugToken(HttpServletResponse response, String role) throws IOException {

        UserDetails aseUserDetails = null;
        switch (role) {
            case UserRole.DISPATCHER:
                aseUserDetails = mongoUserDetailsService.loadUserByUsername("dispatcher");
                break;
            case UserRole.CUSTOMER:
                aseUserDetails = mongoUserDetailsService.loadUserByUsername("customer");
                break;
            case UserRole.DELIVERER:
                aseUserDetails = mongoUserDetailsService.loadUserByUsername("deliverer");
                break;
            case UserRole.BOX:
                aseUserDetails = mongoUserDetailsService.loadUserByUsername("group13");
                break;
        }

        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
            if (role.equals(UserRole.BOX)) {
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(aseUserDetails, "VeryStrongPassword");
            } else {
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(aseUserDetails, "password");
            }
            authentication = authManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (UsernameNotFoundException ex) {
            ex.printStackTrace();
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Email or password is incorrect");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Authentication principal: " + authentication.getPrincipal());

        if (authentication != null) {
            final String jwt = jwtUtil.generateToken(aseUserDetails);
            Cookie cookie = cookieConfig.createCookie("jwt", jwt);
            response.addCookie(cookie);

            AppUser loginUser = null;
            switch (role) {
                case UserRole.DISPATCHER:
                    loginUser = appUserRepository.findByUsername("dispatcher");
                    break;
                case UserRole.CUSTOMER:
                    loginUser = appUserRepository.findByUsername("customer");
                    break;
                case UserRole.DELIVERER:
                    loginUser = appUserRepository.findByUsername("deliverer");
                    break;
                case UserRole.BOX:
                    loginUser = appUserRepository.findByUsername("group13");
                    break;
            }


            return loginUser;
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Email or password is incorrect");
            return null;
        }
    }
}
