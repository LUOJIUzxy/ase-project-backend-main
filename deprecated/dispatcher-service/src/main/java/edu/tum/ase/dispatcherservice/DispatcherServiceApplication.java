package edu.tum.ase.dispatcherservice;

import edu.tum.ase.dispatcherservice.model.Dispatcher;
import edu.tum.ase.dispatcherservice.service.DispatcherService;

import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
//import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import com.mongodb.client.MongoClient;

@SpringBootApplication //(exclude = {MongoAutoConfiguration.class}) //, MongoDataAutoConfiguration.class})
public class DispatcherServiceApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServiceApplication.class);

	@Autowired
	MongoClient mongoClient;
	@Autowired
    DispatcherService dispatcherService;
	
	public static void main(String[] args) {
		SpringApplication.run(DispatcherServiceApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
        
        log.info("MongoClient = " + mongoClient.getClusterDescription());
		
		if (dispatcherService.getAllDispatchers().size() == 0) {
            Dispatcher d = new Dispatcher("D D", "D.D@ase.tum");
            dispatcherService.saveDispatcher(d);
			log.info(String.format("dispatcher %s with id %s", d.getName(), d.getEmail()));
        }
		
		List<Dispatcher> dispatchers = dispatcherService.getAllDispatchers();
		log.info("Number of Dispathcer in DB is " + dispatchers.size());
	}
}
