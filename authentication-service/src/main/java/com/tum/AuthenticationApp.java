package com.tum;


import com.mongodb.client.MongoClient;

import com.tum.constant.UserRole;
import com.tum.model.AppUser;
import com.tum.repo.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootApplication
@Slf4j
@EnableEurekaClient
public class AuthenticationApp implements CommandLineRunner {
    @Autowired
    MongoClient mongoClient;

    @Autowired
    AppUserRepository appUserRepository;


    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("MongoClient = " + mongoClient.getClusterDescription());
        // TODO: remove
        appUserRepository.deleteAll();
        if (appUserRepository.findAll().size() == 0) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            AppUser testDispatcher = new AppUser("dispatcher", "test1@gmail.com", encoder.encode("password"), UserRole.DISPATCHER);
            testDispatcher.setId("63d76a8b982f9143e56af4bb");
            AppUser testDeliverer = new AppUser("deliverer", "test2@gmail.com", encoder.encode("password"), UserRole.DELIVERER);
            testDeliverer.setId("63d76a8b982f9143e56af4bc");
            AppUser testCustomer = new AppUser("customer", "siyun.liang@tum.de", encoder.encode("password"), UserRole.CUSTOMER);
            testCustomer.setId("63d76a8b982f9143e56af4bd");
            AppUser testBox = new AppUser("group13", "group13", encoder.encode("VeryStrongPassword"), UserRole.BOX);
            testBox.setId("63d76a8b982f9143e56af4be");

            appUserRepository.save(testDispatcher);
            appUserRepository.save(testDeliverer);
            appUserRepository.save(testCustomer);
            appUserRepository.save(testBox);
        }
    }
}
