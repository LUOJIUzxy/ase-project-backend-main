package com.tum.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {

    private String password;
    private String username;

    public AuthRequest(String password, String username) {
        super();
        this.password = password;
        this.username = username;
    }
}
