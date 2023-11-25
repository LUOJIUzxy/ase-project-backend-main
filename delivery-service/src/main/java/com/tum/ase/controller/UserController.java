package com.tum.ase.controller;

import com.tum.ase.constant.ErrorMsg;
import com.tum.ase.constant.UserRole;
import com.tum.ase.exception.notfound.UserNotFoundException;
import com.tum.ase.exception.user.FailUpdateCredentialException;
import com.tum.ase.model.AppUser;
import com.tum.ase.model.AppUserRequest;
import com.tum.ase.service.EmailService;
import com.tum.ase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    @Autowired
    public UserController(UserService userService,EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAnyAuthority('" + UserRole.DISPATCHER + "','" + UserRole.DELIVERER + "','" + UserRole.CUSTOMER + "')")
    public ResponseEntity<AppUser> getUserById(@PathVariable("id") String id) {
        String username = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        AppUser user = userService.getUserById(id);
        if (user != null) {
            if (!username.equals(user.getUsername())) {
                throw new AccessDeniedException("Access denied");
            }
        } else {
            throw new UserNotFoundException(ErrorMsg.USER_NOT_FOUND);
        }
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<AppUser> createNewUser(HttpServletRequest request, @RequestBody AppUserRequest user) throws MessagingException {
        String jwt = this.extractJWT(request);
        AppUser saved = userService.createUser(user, jwt);
        if (saved != null) {
            this.emailService.sendHtmlEmail(saved.getEmail(), "Welcome to ASE Delivery Service!",
                    String.format("Dear %s, please use username %s and password %s to login to system", saved.getRole(), saved.getUsername(), user.getPassword()));
        }
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<AppUser> updateUser(HttpServletRequest request, @PathVariable("id") String userId, @RequestBody AppUser user) {
        String jwt = this.extractJWT(request);
        return new ResponseEntity<>(userService.updateUser(userId, user, jwt), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteUsers(HttpServletRequest request, @RequestParam(name = "id") List<String> ids) {
        if (ids.size() != 0) {
            String jwt = this.extractJWT(request);
            userService.deleteUsers(ids, jwt);
        }
    }

    private String extractJWT(HttpServletRequest request) {
        String jwt = null;
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null && cookie.getValue() != null) {
            jwt = cookie.getValue();
        }
        if (jwt == null) {
            throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
        }
        return jwt;
    }
}
