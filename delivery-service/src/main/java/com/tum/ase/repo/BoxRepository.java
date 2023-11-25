package com.tum.ase.repo;

import com.tum.ase.model.AppUser;
import com.tum.ase.model.Box;

import com.tum.ase.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BoxRepository extends MongoRepository<Box, String> {

	Box findBoxById(String id);
	Box findBoxByName(String name);

	Box findBoxByRfid(String rfid);
}
