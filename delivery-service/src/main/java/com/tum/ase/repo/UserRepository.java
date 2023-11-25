package com.tum.ase.repo;

import com.tum.ase.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<AppUser, String> {

    public AppUser findAppUserById(String id);

    public AppUser findAppUserByEmail(String email);

    public AppUser findAppUserByUsername(String username);

    public AppUser findAppUserByToken(String token);

    public List<AppUser> findAllByRole(String role);

}
