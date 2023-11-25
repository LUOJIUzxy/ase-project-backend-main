package edu.tum.ase.boxservice;

import edu.tum.ase.boxservice.model.Customer;
import edu.tum.ase.boxservice.model.Deliverer;
import edu.tum.ase.boxservice.model.Order;
import edu.tum.ase.boxservice.model.Box;
import edu.tum.ase.boxservice.service.BoxService;

import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoxServiceApplication implements CommandLineRunner { 
	private static final Logger log = LoggerFactory.getLogger(BoxServiceApplication.class);
	
	@Autowired
	MongoClient mongoClient;
	@Autowired
	BoxService boxService;

	public static void main(String[] args) {
		SpringApplication.run(BoxServiceApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
        log.info("MongoClient = " + mongoClient.getClusterDescription());

		if (boxService.getAllBoxes().size() == 0) {
            Customer c = new Customer("C C", "C.C@ase.tum", "CCCCCCCCCCCC");
			Deliverer d = new Deliverer("D D", "D.D@ase.tum", "DDDDDDDDDDDD");
			Order o  = new Order("001", c, d);
			Box b = new Box("001", "Garching01", "BBBBBB,BBBBBBB,BBBBB", "BBBBBBBBBBBB");
			boxService.saveBox(b);
			boxService.addNewOrder(b.getId(), o);
			log.info(String.format("Box %s with id %s", b.getName(), b.getId()));
			log.info("Ordr infor: " + o);
			List<Box> boxes = boxService.getAllBoxes();
			log.info("Number of Box in DB is " + boxes.size());
        }
	}
}
