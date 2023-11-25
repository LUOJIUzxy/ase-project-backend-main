package edu.tum.ase.boxservice.controller;

import edu.tum.ase.boxservice.constant.UserRole;
import edu.tum.ase.boxservice.model.Box;
import edu.tum.ase.boxservice.model.Order;
import edu.tum.ase.boxservice.service.BoxService;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
public class BoxController {
	private static final Logger log = LoggerFactory.getLogger(BoxController.class);

	@Autowired
	BoxService boxService;

    @GetMapping("/boxes")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Box> getAllBoxes() {
		return boxService.getAllBoxes();
    }

    @GetMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')")
    public Box getBoxById(@PathVariable String id) {
        Box box =  boxService.getBoxById(id);
		if (box == null) {
			throw new BoxNotFoundException(); 
		}
		else {
			return box; 
		}
    }
	
	@GetMapping("/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    //@PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Order> getAllOrders() {
        return boxService.getAllOrders();
    }

	@GetMapping("/box/{id}/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')")
    public List<Order> getOrdersByBox(@PathVariable String id) throws BoxNotFoundException {
        return boxService.getOrdersByBox(id);
    }
	
	@GetMapping("/order/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')" +
	"|| hasAuthority('" + UserRole.DELIVERER + "')" +
	"|| hasAuthority('" + UserRole.CUSTOMER + "')")
    public Order getOrderById(@PathVariable String id) {
		Order o = boxService.getOrderById(id);
		if (o == null) {
			throw new OrderNotFoundException();
		}
		return o;
    }
	
	@GetMapping("/customer/{id}/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"hasAuthority('" + UserRole.CUSTOMER + "')")
    public List<Order> getCustomerOrders(@PathVariable String id) throws OrderNotFoundException {
		List<Order> orders = boxService.getCustomerOrders(id);
		if (orders.size() == 0) {
			throw new OrderNotFoundException();
		}
		return orders;
    }
	
	@GetMapping("/deliverer/{id}/orders")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"hasAuthority('" + UserRole.DELIVERER + "')")
    public List<Order> getDelivererOrders(@PathVariable String id) throws OrderNotFoundException {
		List<Order> orders = boxService.getDelivererOrders(id);
		if (orders.size() == 0) {
			throw new OrderNotFoundException();
		}
		return orders;
    }
	
    @PostMapping("/box")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Box createBox(@RequestBody Box newBox) {
		boxService.saveBox(newBox);
		return newBox;
    }

	@PostMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Order createOrder(@PathVariable String id, @RequestBody Order newOrder) {
		boxService.addNewOrder(id, newOrder);
		return newOrder;
    }
	
    @PutMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')")
    public Box updateBox(@PathVariable String id, @RequestBody Box updatedBox) throws BoxNotFoundException {
        log.info(updatedBox.toString());
		boxService.updateBox(id, updatedBox);
		return updatedBox;
    }

	@PutMapping("/boxes/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Box> updateMultiBox(@PathVariable String[] ids, @RequestBody List<Box> updatedBoxes) throws BoxNotFoundException {
        log.info(ids.toString());
		boxService.updateBoxes(ids, updatedBoxes);
		return updatedBoxes;
    }
	
	@PutMapping("/box/{boxID}/order/{orderID}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')" +
	"|| hasAuthority('" + UserRole.DELIVERER + "')")
    public Order updateOrder(@PathVariable String boxID, @PathVariable String orderID, @RequestBody Order updatedOrder) throws OrderNotFoundException {
        log.info(updatedOrder.toString());
		boxService.updateOrder(orderID, orderID, updatedOrder);
		return updatedOrder;
    }

	@PutMapping("/box/{boxId}/orders/{orderIds}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')" +
	"|| hasAuthority('" + UserRole.BOX + "')" +
	"|| hasAuthority('" + UserRole.DELIVERER + "')")
    public List<Order> updateOrders(@PathVariable String boxId, @PathVariable String[] orderIds, @RequestBody List<Order> updatedOrders) throws OrderNotFoundException {
        log.info(orderIds.toString());
		boxService.updateOrders(boxId, orderIds, updatedOrders);
		return updatedOrders;
    }
	
    @DeleteMapping("/box/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Box deleteBox(@PathVariable String id) throws BoxNotFoundException {
        return boxService.deleteBox(id);
    }

	@DeleteMapping("/boxes/{ids}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Box> deleteBoxes(@PathVariable String[] ids) throws BoxNotFoundException {
        return boxService.deleteBoxes(ids);
    }

	@DeleteMapping("/box/{boxId}/order/{orderId}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Order deleteOrder(@PathVariable String boxId, @PathVariable String orderId) throws OrderNotFoundException {
        return boxService.deleteOrder(boxId, orderId);
    }

	@DeleteMapping("/box/{boxId}/orders/{orderIds}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Order> deleteOrders(@PathVariable String boxId, @PathVariable String[] orderIds) throws OrderNotFoundException {
        return boxService.deleteOrders(boxId, orderIds);
    }
}
