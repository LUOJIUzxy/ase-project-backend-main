package edu.tum.ase.customerservice;

import edu.tum.ase.customerservice.model.Customer;
import edu.tum.ase.customerservice.service.CustomerService;

import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerServiceApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CustomerServiceApplication.class);

	@Autowired
    MongoClient mongoClient;
    @Autowired
    CustomerService customerService;

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
		log.info("MongoClient = " + mongoClient.getClusterDescription());
		
		if (customerService.getAllCustomers().size() == 0) {
            Customer c = new Customer("C C", "C.C@ase.tum", "CCCCCCCCCCCC");
            customerService.saveCustomer(c);
			log.info(String.format("customer %s with id %s", c.getName(), c.getEmail()));
        }
		
		List<Customer> customers = customerService.getAllCustomers();
		log.info("Number of Dispathcer in DB is " + customers.size());
	}
}
