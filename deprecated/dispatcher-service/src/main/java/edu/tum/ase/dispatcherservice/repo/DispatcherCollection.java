package edu.tum.ase.dispatcherservice.repo;

import edu.tum.ase.dispatcherservice.model.Dispatcher;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DispatcherCollection extends MongoRepository<Dispatcher, String> {
	@Query("{ 'email': ?0 }")
	Dispatcher findDispatcherById(String dispatcherEmail);
}
