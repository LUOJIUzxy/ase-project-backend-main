package com.tum.ase.model;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class AppUser implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    // for avoid duplicate key errors when refer in orders and further refers in boxes
    // unique is guarantee by check before saving
    private String email;

    @Indexed(unique = true)
    private String username;

    @NonNull
    private String role;

    private String token;

    public AppUser(String username, String email, String token, String role) {
        this.username = username;
        this.email = email;
        this.token = token;
        this.role = role;
    }
}
