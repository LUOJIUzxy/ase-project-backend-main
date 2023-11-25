package com.tum.resource;


import com.tum.model.Authentication;
import com.tum.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/list")
    public List<Authentication> getAllAuthentication(){
        return authenticationService.getAllAuthentications();
    }

}
