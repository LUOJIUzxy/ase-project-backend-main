package com.tum.controller;

import com.tum.constant.UserRole;
import com.tum.model.AppUser;
import com.tum.model.AuthRequest;
import com.tum.service.AppUserService;
import com.tum.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserService userService;

    // authenticate with username, password in jwe (username plaintext, password encrypted)
    @PostMapping("/jwe")
    public ResponseEntity<AppUser> loginJwe(@RequestBody AuthRequest loginRequest, HttpServletResponse response) throws IOException {
        return authService.createAuthTokenAndReturnAppUser(false, loginRequest,response);
    }

    @PostMapping("/jwe/box")
    public ResponseEntity<AppUser> loginJweForBox(@RequestBody AuthRequest loginRequest, HttpServletResponse response) throws IOException {
        return authService.createAuthTokenAndReturnAppUser(true, loginRequest,response);
    }

    @GetMapping("/jwt")
    @PreAuthorize("hasAnyAuthority('" + UserRole.DISPATCHER + "','" + UserRole.DELIVERER + "','" + UserRole.CUSTOMER + "')")
    public ResponseEntity<AppUser> getUserFromJWT() {
        String username = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new ResponseEntity<>(userService.getUserFromUsername(username), HttpStatus.OK);
    }

    @DeleteMapping("/clean")
    public ResponseEntity<String> cleanJWTToken(HttpServletResponse response) {
        return authService.deleteAuthToken(response);
    }

    // send public key to frontend, so frontend can use it to encrypt password
    @GetMapping("/pkey")
    public ResponseEntity<HashMap<String,String>> getPublicKey() throws Exception {
        HashMap<String, String> pKeyData = authService.getPublicKeyData();
        return new ResponseEntity<HashMap<String, String>>(pKeyData, HttpStatus.OK);
    }

    @GetMapping("/pem")
    public String getPem() {
        PublicKey pem = authService.getPem();
        return Base64.getEncoder().encodeToString(pem.getEncoded());
    }

    // send CSRF token to frontend, used in POST,DELETE,PUT requests
    @GetMapping("/csrf")
    public ResponseEntity<String> csrf() throws Exception {
        return new ResponseEntity<String>("{}",HttpStatus.OK);
    }

    // TODO: To Delete later
    @GetMapping("/debug/{role}")
    public ResponseEntity<AppUser> getTokenForDebug(HttpServletResponse response, @PathVariable("role") String role) throws IOException {
        AppUser debugUser = authService.createDebugToken(response, role);
        return new ResponseEntity<>(debugUser, HttpStatus.OK);
    }
}
