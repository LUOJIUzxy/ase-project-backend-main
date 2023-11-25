package edu.tum.ase.customerservice.repo;

import edu.tum.ase.customerservice.model.Customer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CustomerCollection extends MongoRepository<Customer, String> {
	@Query("{ 'email': ?0 }")
	Customer findCustomerById(String customerEmail);
}
