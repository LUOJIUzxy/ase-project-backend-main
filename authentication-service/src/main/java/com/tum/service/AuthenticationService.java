package com.tum.service;

import com.tum.model.Authentication;
import com.tum.repo.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    public Authentication createAuthentication(Authentication authentication){
        authenticationRepository.save(authentication);
        return authentication;
    }

    public Authentication findByEmail(String email) {
        return authenticationRepository.findByEmail(email);
    }

    public List<Authentication> getAllAuthentications(){
        return authenticationRepository.findAll();
    }
}
