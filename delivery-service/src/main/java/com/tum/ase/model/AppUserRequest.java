package com.tum.ase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRequest {
    private String id;

    private String email;

    private String username;

    private String password;

    private String role;

    private String token;
}
