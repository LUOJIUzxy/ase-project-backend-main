package com.tum.repo;

import com.tum.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser, String> {

    public AppUser findAppUsersById(String id);

    public AppUser findByUsername(String name);

    public AppUser findByEmail(String email);
}
