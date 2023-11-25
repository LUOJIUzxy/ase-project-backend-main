package com.tum.ase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery/csrf")
public class AuthController {

    @GetMapping("")
    public ResponseEntity<String> csrf() throws Exception {
        return new ResponseEntity<String>("{}", HttpStatus.OK);
    }
}
