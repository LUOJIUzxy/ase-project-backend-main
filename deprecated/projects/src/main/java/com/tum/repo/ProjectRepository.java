package com.tum.repo;
import com.tum.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProjectRepository extends MongoRepository<Project, String> {

    @Query("{name: ?0}")
    Project findByName(String name);
}
