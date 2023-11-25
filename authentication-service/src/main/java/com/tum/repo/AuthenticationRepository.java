package com.tum.repo;

import com.tum.model.Authentication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AuthenticationRepository extends MongoRepository<Authentication, String> {

    @Query("{email: ?0}")
    Authentication findByEmail(String email);
}
