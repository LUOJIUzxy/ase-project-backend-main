package com.tum.controller;

import com.tum.constant.UserRole;
import com.tum.model.AppUser;
import com.tum.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credential")
public class AppUserController {

    @Autowired
    AppUserService appUserService;

    // This controller holds REST request from delivery-service to update the authentication database
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<List<AppUser>> listUserCredential() throws Exception {
        return new ResponseEntity<>(appUserService.listUserCredentials(), HttpStatus.OK);
    }


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<AppUser> saveUserCredential(@RequestBody AppUser user) throws Exception{
        return new ResponseEntity<>(appUserService.saveUserCredential(user), HttpStatus.CREATED);
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<AppUser> resetUserPassword(@PathVariable("id") String userId, @RequestBody AppUser user) throws Exception {
        return new ResponseEntity<>(appUserService.resetPassword(userId, user), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<AppUser> updateUserCredential(@PathVariable("id") String userId, @RequestBody AppUser user) throws Exception{
        return new ResponseEntity<>(appUserService.updateUserCredential(userId, user), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteUserCredentials(@RequestParam("id") List<String> ids) throws Exception {
        appUserService.deleteUserCredentials(ids);
    }
}
