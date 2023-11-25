package com.tum.ase.controller;

import com.tum.ase.constant.OrderStatus;
import com.tum.ase.constant.UserRole;
import com.tum.ase.model.*;
import com.tum.ase.service.EmailService;
import com.tum.ase.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") String id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }

    @GetMapping("/list/customer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.CUSTOMER + "')")
    public ResponseEntity<List<Order>> getOrderListByCustomerId(@PathVariable("id") String id) {
        return new ResponseEntity<>(orderService.getOrderListByCustomerId(id), HttpStatus.OK);
    }

    @GetMapping("/list/deliverer/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public ResponseEntity<List<Order>> getOrderListByDelivererId(@PathVariable("id") String id) {
        return new ResponseEntity<>(orderService.getOrderListByDelivererId(id), HttpStatus.OK);
    }

    @PostMapping("/add/{boxName}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Order> addOrderToBox(@PathVariable("boxName") String boxName, @RequestBody Order order) throws MessagingException {
        Order saved = orderService.addOrder(boxName, order);
        if (saved != null) {
            AssignUser customer = saved.getCustomer();
            this.emailService.sendHtmlEmail(customer.getEmail(), "A delivery is created for you!",
                    String.format("Please track your new delivery with your track code: %s and pick your delivery on time.", saved.getTrackCode()));
        }
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") String id, @RequestBody Order order) {
        return new ResponseEntity<>(orderService.updateOrder(id, order), HttpStatus.OK);
    }

    @PutMapping("/pickup/{userId}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public ResponseEntity<Order> pickUpOrder(@PathVariable("userId") String delivererId, @RequestParam(value = "id") String orderId) {
        return new ResponseEntity<>(orderService.pickUpOrder(delivererId, orderId), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteOrders(@RequestParam("id") List<String> ids) {
        orderService.deleteOrders(ids);
    }

    /** Call By box **/
    @GetMapping("/list/{rfid}")
    @PreAuthorize("hasAuthority('" + UserRole.BOX + "')")
    public ResponseEntity<List<Order>> findAllOrdersInBoxByUserToken(@PathVariable("rfid") String boxRfid, @RequestParam(value = "token", required = false) String userToken) {
        return new ResponseEntity<>(orderService.findAllOrdersInBoxByUserToken(boxRfid, userToken), HttpStatus.OK);
    }
    @PutMapping("/change-status/{rfid}/{token}")
    @PreAuthorize("hasAuthority('" + UserRole.BOX + "')")
    public void updateOrderStatusByBox(@PathVariable("rfid") String boxRfid, @PathVariable("token") String userToken) throws MessagingException {
        // call when deliverers or customers open and close the box successfully
        List<Order> updatedOrders = orderService.updateOrderStatusByBox(boxRfid, userToken);
        if (updatedOrders.size() > 0) {
            Order sample = updatedOrders.get(0);
            AssignUser customer = sample.getCustomer();
            String text = "";
            String subject = "";
            if (sample.getStatus().equals(OrderStatus.Delivered)) {
                subject = "You deliveries are delivered to your box!";
                text = "Your deliveries has been delivered to your box, please check you page.";
            } else if (sample.getStatus().equals(OrderStatus.Finished)) {
                subject = "You deliveries are finished!";
                text = "Your deliveries has been picked-up from your box, please check you page.";
            }
            if (!subject.isEmpty() && !subject.isBlank()) {
                this.emailService.sendHtmlEmail(customer.getEmail(), subject, text);
            }
        }
    }

}
