package com.tum.ase.service;

import com.tum.ase.constant.AuthenticationAPI;
import com.tum.ase.model.AppUser;
import com.tum.ase.model.AppUserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AuthService {
    // the service is a helper to call authentication-service api
    @Autowired
    RestTemplate restTemplate;

    private static final String authenticationServiceBaseURL = "http://authentication-service:8081";
//    private static final String authenticationServiceBaseURL = "https://localhost:8080";

    public String getPublicKey() {
        String pem = restTemplate.getForObject(authenticationServiceBaseURL + AuthenticationAPI.PEM.getAPI(), String.class);
        return pem;
    }

    public AppUserCredential saveUserCredential(AppUserCredential credential, String jwt) {
        HttpHeaders cookie = new HttpHeaders();
        cookie.add(HttpHeaders.COOKIE, "jwt=" + jwt);
        HttpEntity<AppUserCredential> request = new HttpEntity<>(credential, cookie);

        AppUserCredential saved = restTemplate.exchange(authenticationServiceBaseURL + AuthenticationAPI.SAVE.getAPI(),
                HttpMethod.POST, request, AppUserCredential.class).getBody();
        return saved;
    }
    public AppUserCredential updateUserCredential(String username, AppUserCredential credential, String jwt) {
        // update by name because the id of the user in two database could be different
        HttpHeaders cookie = new HttpHeaders();
        cookie.add(HttpHeaders.COOKIE, "jwt=" + jwt);
        HttpEntity<AppUserCredential> request = new HttpEntity<>(credential, cookie);

        return restTemplate.exchange(authenticationServiceBaseURL + AuthenticationAPI.UPDATE.getAPI() + username, HttpMethod.PUT,
                request, AppUserCredential.class).getBody();
    }

    public void deleteUserCredentials(List<String> ids, String jwt) {
        //restTemplate.delete(authenticationServiceBaseURL + AuthenticationAPI.DELETE.getAPI(), HttpMethod.DELETE, ids);
        HttpHeaders cookie = new HttpHeaders();
        cookie.add(HttpHeaders.COOKIE, "jwt=" + jwt);
        HttpEntity request = new HttpEntity(cookie);
        restTemplate.exchange(authenticationServiceBaseURL + AuthenticationAPI.DELETE.getAPI() + "?id=" + String.join(",", ids), HttpMethod.DELETE, request, Void.class);
    }
}
