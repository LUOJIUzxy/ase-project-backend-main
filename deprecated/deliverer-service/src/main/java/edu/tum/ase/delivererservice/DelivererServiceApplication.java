package edu.tum.ase.delivererservice;

import edu.tum.ase.delivererservice.model.Deliverer;
import edu.tum.ase.delivererservice.service.DelivererService;

import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mongodb.client.MongoClient;

@SpringBootApplication
public class DelivererServiceApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(DelivererServiceApplication.class);

	@Autowired
	MongoClient mongoClient;
	@Autowired
    DelivererService delivererService;

	public static void main(String[] args) {
		SpringApplication.run(DelivererServiceApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
		log.info("MongoClient = " + mongoClient.getClusterDescription());
		
		if (delivererService.getAllDeliverers().size() == 0) {
            Deliverer d = new Deliverer("D D", "D.D@ase.tum", "DDDDDDDDDDDDD");
            delivererService.saveDeliverer(d);
			log.info(String.format("deliverer %s with id %s", d.getName(), d.getEmail()));
        }

		List<Deliverer> deliverers = delivererService.getAllDeliverers();
		log.info("Number of Dispathcer in DB is " + deliverers.size());
	}
}
