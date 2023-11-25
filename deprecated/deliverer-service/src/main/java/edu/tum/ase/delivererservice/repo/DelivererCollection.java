package edu.tum.ase.delivererservice.repo;

import edu.tum.ase.delivererservice.model.Deliverer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DelivererCollection extends MongoRepository<Deliverer, String> {
	@Query("{ 'email': ?0 }")
	Deliverer findDelivererById(String delivererEmail);
}
