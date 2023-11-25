package com.tum.ase.repo;

import com.tum.ase.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    Order findOrderById(String id);
    Order findOrderByQrCode(String qrCode);

    Order findOrderByTrackCode(String trackCode);

    @Query(value = "{ 'box.rfid': ?0, 'customer.token': ?1}")
    List<Order> findOrdersByBoxTokenAndCustomerToken(String boxId, String customerId);

    @Query(value = "{ 'box.rfid': ?0, 'deliverer.token': ?1}")
    List<Order> findOrdersByBoxTokenAndDelivererToken(String boxId, String delivererId);

    @Query(value = "{ 'customer.id': ?0}")
    List<Order> findOrdersByCustomerId(String customerId);

    @Query(value = "{ 'deliverer.id': ?0}")
    List<Order> findOrdersByDelivererId(String delivererId);

    @Query(value = "{ 'box.id': ?0}")
    List<Order> findOrdersByBoxId(String boxId);
}
