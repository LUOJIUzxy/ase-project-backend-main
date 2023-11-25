package edu.tum.ase.boxservice.repo;

//import edu.tum.ase.boxservice.model.Order;
//import edu.tum.ase.boxservice.model.OrderAggregate;
import edu.tum.ase.boxservice.model.Box;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.mongodb.repository.Aggregation;

//import java.util.List;

public interface BoxCollection extends MongoRepository<Box, String> {
	@Query("{ 'id': ?0 }")
	Box findBoxById(String boxID);
}
