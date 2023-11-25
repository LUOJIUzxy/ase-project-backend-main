package com.tum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;


@Getter
@Setter
@Document(collection = "users")
@NoArgsConstructor
public class AppUser implements Serializable {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // write only so it cannot be seen in browser
    private String password;
    private String role;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // write only so it cannot be seen in browser
    private User user;

    public AppUser(String username, String email,String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


}
