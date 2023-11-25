package com.tum.ase.security.inteceptor;

import com.tum.ase.constant.AuthenticationAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final String authenticationServiceBaseURL = "http://authentication-service:8081";
//    private static final String authenticationServiceBaseURL = "https://localhost:8080";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (request.getMethod() != HttpMethod.GET) {
           RestTemplate restTemplate = new RestTemplate();
           HttpEntity<String> response = restTemplate.getForEntity(authenticationServiceBaseURL + AuthenticationAPI.CSRF.getAPI(), String.class);
           HttpHeaders headers = response.getHeaders();
           String set_cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
           String token = set_cookie.split("AUTH-XSRF-TOKEN=")[1].split(";")[0];
           request.getHeaders().add("AUTH-XSRF-TOKEN", token);
           request.getHeaders().add(HttpHeaders.COOKIE, set_cookie);
        }
        return execution.execute(request, body);
    }
}
